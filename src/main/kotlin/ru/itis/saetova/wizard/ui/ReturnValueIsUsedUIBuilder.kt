package ru.itis.saetova.wizard.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import ru.itis.saetova.utils.dummyTextBinding
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.base.WizardStepUIBuilder
import ru.itis.saetova.wizard.state.RuleParamsStepState
import java.awt.Dimension
import javax.swing.JComponent

class ReturnValueIsUsedUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
    }

    private lateinit var methodNameTextField: JBTextField

    private var analyzeConstructors = false

    override fun getStepState(): WizardStepState {
        return RuleParamsStepState(
            "ReturnValueIsUsed",
            hashMapOf(
                "methodName" to methodNameTextField.text,
            )
        )
    }

    override fun build(): JComponent {
        return panel {
            row {
                methodNameTextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(200, 50)
                    }
                methodNameTextField.invoke()
            }
        }.withPreferredSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    }
}
