package ru.itis.saetova.wizard.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.Row
import com.intellij.ui.layout.panel
import ru.itis.saetova.utils.dummyNullableTextBinding
import ru.itis.saetova.utils.dummyTextBinding
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.base.WizardStepUIBuilder
import ru.itis.saetova.wizard.state.RuleParamsStepState
import java.awt.Dimension
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent

class RequiredAnnotationOnClassUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
    }

    private lateinit var classNameTextField: JBTextField

    private var checkRequired = false
    private var selectedVersion = false

    override fun getStepState(): WizardStepState {
        return RuleParamsStepState(
            "RequiredAnnotationOnClass",
            hashMapOf(
                "checkRequired" to checkRequired,
                "analyzeConstructors" to selectedVersion.toString(),
            )
        )
    }

    private fun Row.getVersionComboBox(model: DefaultComboBoxModel<String>): ComboBox<String> {
        return comboBox(
            model = model,
            modelBinding = dummyNullableTextBinding
        ).component.also {
            it.isEnabled = false
            it.addActionListener { _ ->
                selectedVersion = it.selectedItem as Boolean
            }
        }
    }

    override fun build(): JComponent {
        return panel {
            row {
                getVersionComboBox(DefaultComboBoxModel(emptyArray<String>()))
                classNameTextField = textField(dummyTextBinding)
                    .component.also {
                        it.text = checkRequired.toString()
                        it.maximumSize = Dimension(200, 50)
                    }
                classNameTextField.invoke()
            }
            row {
                checkBox(
                    text = "Check required annotation on class",
                    isSelected = checkRequired
                ).component.also {
                    it.addActionListener { _ ->
                        checkRequired = it.isSelected
                    }
                }
            }
        }.withPreferredSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    }
}
