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

class OverrideMethodsTandemUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
    }

    private lateinit var method1NameTextField: JBTextField
    private lateinit var method2NameTextField: JBTextField

    override fun getStepState(): WizardStepState {
        return RuleParamsStepState(
            "OverrideMethodsTandem",
            hashMapOf(
                "method1Name" to method1NameTextField.text,
                "method2Name" to method2NameTextField.text,
            )
        )
    }

    override fun build(): JComponent {
        return panel {
            row("Specify first method name") {  }
            row {
                method1NameTextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(200, 50)
                    }
                method1NameTextField.invoke()
            }
            row("Specify second method name") {  }
            row {
                method2NameTextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(200, 50)
                    }
                method2NameTextField.invoke()
            }
        }.withPreferredSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    }
}
