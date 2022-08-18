package ru.itis.saetova.wizard.ui

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import com.intellij.ui.table.JBTable
import com.intellij.util.indexing.FileBasedIndex
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import ru.itis.saetova.service.SearchInProjectService
import ru.itis.saetova.utils.dummyTextBinding
import ru.itis.saetova.utils.logInfo
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.base.WizardStepUIBuilder
import ru.itis.saetova.wizard.state.LimitScopePreparedStepState
import ru.itis.saetova.wizard.ui.search.SearchResultTable
import ru.itis.saetova.wizard.ui.search.SearchResultTableModel
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import java.util.stream.Collectors
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JRadioButton
import javax.swing.table.DefaultTableModel
import kotlin.collections.HashSet

class LimitScopePreparedUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 600
        const val WINDOW_HEIGHT = 200
    }

    private lateinit var inputTextField: JBTextField
    private lateinit var addedPackagesTable: JBTable
    private lateinit var searchPackagesTable: JBTable
    private lateinit var searchClassesTable: JBTable

    private val addedPackagesTableModel = DefaultTableModel(0, 1)
    private val searchPackagesTableModel = DefaultTableModel(0, 1)
    private var searchClassTableModel = SearchResultTableModel()
    private val searchInProjectService = SearchInProjectService.getInstance(project)

    private var selectedPackages = mutableListOf<String>()
    private var selectedClasses = mutableListOf<String>()

    private var packagesList = listOf<String>()
    private var selectedSearchWay: SelectedSearchWay = SelectedSearchWay.PACKAGES

    override fun getStepState(): WizardStepState {
        return LimitScopePreparedStepState(
            packages = selectedPackages,
            classes = selectedClasses,
        )
    }

    override fun build(): JComponent {
        findPackages()
        return panel {
            row(label = "") {}
            row {
                addedPackagesTable = JBTable(addedPackagesTableModel)
                    .also {
                        it.preferredSize = Dimension(600, 100)
                        it.isVisible = false
                    }
                addedPackagesTable.invoke()
            }
            row {
                inputTextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(600, 50)
                        it.addKeyListener(ShiftKeyListener { handleSearch() })
                    }
                inputTextField.invoke()
            }
            row {
                buttonGroup {
                    JRadioButton("Search through packages", true).also {
                        it.addChangeListener {
                            selectedSearchWay = SelectedSearchWay.PACKAGES
                            searchPackagesTableModel.rowCount = 0
                            searchClassTableModel.clearData()
                            searchClassesTable.isVisible = false
                            searchPackagesTable.isVisible = false
                            inputTextField.text = ""
                        }
                    }.invoke()
                    JRadioButton("Search through classes", false).also {
                        it.addChangeListener {
                            selectedSearchWay = SelectedSearchWay.CLASSES
                            searchPackagesTableModel.rowCount = 0
                            searchClassTableModel.clearData()
                            searchPackagesTable.isVisible = false
                            searchClassesTable.isVisible = false
                            inputTextField.text = ""
                        }
                    }.invoke()
                }
            }
            row {
                JButton("Add")
                    .also {
                        it.addActionListener { handleAdding() }
                    }
                    .invoke()
            }
            row {
                searchPackagesTable = JBTable(searchPackagesTableModel)
                    .also {
                        it.addMouseListener(object : MouseAdapter() {
                            override fun mouseClicked(e: MouseEvent?) {
                                val selectedRowValue =
                                    searchPackagesTableModel.getValueAt(searchPackagesTable.selectedRow, 0).toString()
                                inputTextField.text = selectedRowValue
                            }
                        })
                        it.preferredSize = Dimension(600, 100)
                        it.isVisible = false
                    }

                searchPackagesTable.invoke()
            }
            row {
                searchClassesTable = SearchResultTable(searchClassTableModel) {
                    inputTextField.text = it.split(" ")[1]
                }.also {
                    it.preferredSize = Dimension(600, 100)
                    it.isVisible = false
                }

                searchClassesTable.invoke()
            }
        }.withPreferredSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    }

    private fun handleSearch() {
        when (selectedSearchWay) {
            SelectedSearchWay.CLASSES -> {
                val foundedClasses = searchInProjectService.searchClasses(inputTextField.text)
                if (foundedClasses.isEmpty()) {
                    searchClassesTable.isVisible = false
                    return
                }
                searchClassesTable.isVisible = true
                searchClassTableModel.setData(
                    (foundedClasses.entries.map { entry -> "${entry.value} ${entry.key} " }.take(7) + "... ")
                        .toMutableList()
                )
            }
            SelectedSearchWay.PACKAGES -> {
                val res = packagesList.filter { it.startsWith(inputTextField.text) }.take(7) + "..."
                searchPackagesTableModel.rowCount = 0
                if (res.isEmpty()) {
                    searchPackagesTable.isVisible = false
                    return
                }
                searchPackagesTable.isVisible = true
                res.forEach { str ->
                    searchPackagesTableModel.addRow(arrayOf(str))
                }
            }
        }
    }

    private fun handleAdding() {
        if (getCurrentAddedPackages().contains(inputTextField.text).not() && inputTextField.text.isNotBlank()) {
            addedPackagesTable.isVisible = true
            addedPackagesTableModel.addRow(arrayOf(inputTextField.text))
            when (selectedSearchWay) {
                SelectedSearchWay.CLASSES -> {
                    selectedClasses.add(inputTextField.text)
                }
                SelectedSearchWay.PACKAGES -> {
                    selectedPackages.add(inputTextField.text)
                }
            }
        }
    }

    private fun findPackages() {
        val set = HashSet<String>()

        val virtualFiles = FileBasedIndex.getInstance().getContainingFiles(
            FileTypeIndex.NAME,
            KotlinFileType.INSTANCE,
            GlobalSearchScope.projectScope(project)
        )

        for (vf in virtualFiles) {
            val psifile = PsiManager.getInstance(project).findFile(vf!!)
            if (psifile is PsiJavaFile) {
                set.add(psifile.packageName)
            }
            if (psifile is KtFile) {
                set.add(psifile.packageName)
            }
        }
        project.logInfo(set.sortedBy { it }.toString())
        packagesList = set.toList()
    }

    private fun getCurrentAddedPackages(): List<String> {
        return (addedPackagesTableModel.dataVector as Vector<Vector<String>>)
            .stream()
            .flatMap { it.stream() }
            .collect(Collectors.toList())
    }

    private enum class SelectedSearchWay {
        CLASSES, PACKAGES
    }
}