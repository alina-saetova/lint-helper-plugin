package ru.itis.saetova.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import ru.itis.saetova.service.RuleCreatingService
import ru.itis.saetova.utils.dummyTextBinding
import java.awt.Dimension
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent

class CreateRuleTemplateDialog(
    val project: Project
) : DialogWrapper(project, true) {

    companion object {
        private const val DIALOG_TITLE = "Create New Rule Template"
        private const val TEXT_FIELD_TITLE = "Enter the name of your rule: "
        private const val COMBO_BOX_TITLE = "Choose the scanner: "
        private const val DIALOG_PREFERRED_WIDTH = 550
        private const val TEXTFIELD_WIDTH = 400
        private const val TEXTFIELD_HEIGHT = 50

        private val scannerToScope = mapOf(
            "GradleScanner" to "GRADLE_SCOPE",
            "SourceCodeScanner" to "JAVA_FILE_SCOPE",
            "XmlScanner" to "RESOURCE_FILE_SCOPE",
            "ClassScanner" to "CLASS_FILE_SCOPE",
            "BinaryResourceScanner" to "BINARY_RESOURCE_FILE_SCOPE",
            "ResourceFolderScanner" to "RESOURCE_FOLDER_SCOPE",
        )
    }

    private lateinit var textField: JBTextField
    private var scannerModel = DefaultComboBoxModel(emptyArray<String>())

    init {
        title = DIALOG_TITLE
        init()
        scannerToScope.keys.forEach { scannerModel.addElement(it) }
    }

    override fun createCenterPanel(): JComponent? {
        return panel {
            row(TEXT_FIELD_TITLE) {}
            row {
                textField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(TEXTFIELD_WIDTH, TEXTFIELD_HEIGHT)
                    }
                textField.invoke()
            }
            row(COMBO_BOX_TITLE) {
                comboBox(
                    model = scannerModel,
                    getter = { "" }, setter = {}
                )
            }
        }.withPreferredWidth(DIALOG_PREFERRED_WIDTH)
    }

    override fun doOKAction() {
        val ruleName = textField.text.filter { it.isLetter() }.capitalize()
        val scanner = scannerToScope.entries.find { it.key == scannerModel.selectedItem.toString() }!!.toPair()
        RuleCreatingService.getInstance(project).createTemplate(ruleName, scanner)

        super.doOKAction()
    }
}