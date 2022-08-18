package ru.itis.saetova.wizard.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import ru.itis.saetova.utils.dummyIntBinding
import ru.itis.saetova.utils.dummyTextBinding
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.base.WizardStepUIBuilder
import ru.itis.saetova.wizard.state.RuleParamsStepState
import java.awt.Dimension
import javax.swing.JComponent

class RequiresApiLevelUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
    }

    private lateinit var apiLevelIntTextField: JBTextField
    private lateinit var classNameTextField: JBTextField
    private lateinit var methodNameTextField: JBTextField

    override fun getStepState(): WizardStepState {
        return RuleParamsStepState(
            "RequiresApiLevel",
            hashMapOf(
                "apiLevel" to apiLevelIntTextField.text,
                "className" to classNameTextField.text,
                "methodName" to methodNameTextField.text,
            )
        )
    }

    override fun build(): JComponent {
        return panel {
            row {
                apiLevelIntTextField = intTextField(dummyIntBinding)
                    .component.also {
                        it.text = "21"
                        it.maximumSize = Dimension(200, 50)
                    }
                apiLevelIntTextField.invoke()
            }
            row ("Specify class name") {}
            row {
                classNameTextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(200, 50)
                    }
                classNameTextField.invoke()
            }
            row ("Specify method name") {}
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
