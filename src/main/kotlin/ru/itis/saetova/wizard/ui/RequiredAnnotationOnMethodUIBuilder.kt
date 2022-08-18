package ru.itis.saetova.wizard.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import ru.itis.saetova.service.SearchInProjectService
import ru.itis.saetova.utils.dummyIntBinding
import ru.itis.saetova.utils.dummyTextBinding
import ru.itis.saetova.utils.logInfo
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.base.WizardStepUIBuilder
import ru.itis.saetova.wizard.state.RuleParamsStepState
import ru.itis.saetova.wizard.ui.search.SearchResultTable
import ru.itis.saetova.wizard.ui.search.SearchResultTableModel
import java.awt.Dimension
import javax.swing.JComponent

class RequiredAnnotationOnMethodUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
    }

    private lateinit var triggerMethodTextField: JBTextField
    private lateinit var requiredMethodTextField: JBTextField
    private lateinit var classNameTextField: JBTextField

    private val searchInProjectService = SearchInProjectService.getInstance(project)

    private var searchMethodTableModel = SearchResultTableModel()
    private var searchClassTableModel = SearchResultTableModel()
    private var searchMethod2TableModel = SearchResultTableModel()

    override fun getStepState(): WizardStepState {
        return RuleParamsStepState(
            "RequiredAnnotationOnMethod",
            hashMapOf(
                "triggerMethod" to triggerMethodTextField.text.takeIf { it.isNotEmpty() },
                "requiredMethod" to requiredMethodTextField.text,
                "className" to classNameTextField.text,
            )
        )
    }

    override fun build(): JComponent {
        return panel {
            row(label = "Method's name. Specify without braces `()`") {}
            row {
                requiredMethodTextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(600, 50)
                        it.addKeyListener(ShiftKeyListener {
                            val foundedMethods = searchInProjectService.searchMethods(it.text)
                            if (foundedMethods.isNotEmpty()) {
                                searchMethodTableModel.setData(foundedMethods.entries.map { entry -> "${entry.value} ${entry.key} " }
                                    .toMutableList())
                            }
                        })
                    }
                requiredMethodTextField.invoke()
            }
            row {
                SearchResultTable(searchMethodTableModel) {
                    requiredMethodTextField.text = it.split(" ")[0]
                    project.logInfo("requiredMethod selected: $it")
                }.also {
                    it.preferredSize = Dimension(600, 100)
                }.invoke()
            }

            row(label = "Method's class name") {}
            row {
                classNameTextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(600, 50)
                        it.addKeyListener(ShiftKeyListener {
                            val foundedClasses = searchInProjectService.searchClasses(it.text)
                            if (foundedClasses.isNotEmpty()) {
                                searchClassTableModel.setData(foundedClasses.entries.map { entry -> "${entry.value} ${entry.key} " }
                                    .toMutableList())
                            }
                            project.logInfo("foundedClasses: $foundedClasses")
                        })
                    }
                classNameTextField.invoke()
            }
            row {
                SearchResultTable(searchClassTableModel) {
                    classNameTextField.text = it.split(" ")[1]
                    project.logInfo("className selected: $it")
                }.also {
                    it.preferredSize = Dimension(600, 100)
                }.invoke()
            }

            row {
                checkBox(
                    text = "Detect if specified method was called",
                    isSelected = false
                ).component.also {
                    it.addActionListener { _ ->
                        triggerMethodTextField.isEnabled = it.isSelected
                        if (it.isSelected.not()) {
                            searchMethod2TableModel.clearData()
                        }
                    }
                }
            }
        }.withPreferredSize(RequiredAnnotationUIBuilder.WINDOW_WIDTH, RequiredAnnotationUIBuilder.WINDOW_HEIGHT)
    }
}
