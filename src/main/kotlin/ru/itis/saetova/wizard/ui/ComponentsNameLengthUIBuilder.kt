package ru.itis.saetova.wizard.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import ru.itis.saetova.utils.dummyIntBinding
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.base.WizardStepUIBuilder
import ru.itis.saetova.wizard.state.RuleParamsStepState
import java.awt.Dimension
import javax.swing.JComponent

class ComponentsNameLengthUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
    }

    private lateinit var methodsNameLengthIntTextField: JBTextField
    private lateinit var classesNameLengthIntTextField: JBTextField
    private lateinit var variablesNameLengthIntTextField: JBTextField

    override fun getStepState(): WizardStepState {
        return RuleParamsStepState(
            "ComponentsNameLength",
            hashMapOf(
                "methodsNameLength" to methodsNameLengthIntTextField.text,
                "classesNameLength" to classesNameLengthIntTextField.text,
                "variablesNameLength" to variablesNameLengthIntTextField.text,
            )
        )
    }

    override fun build(): JComponent {
        return panel {
            row ("Specify method name length") {}
            row {
                methodsNameLengthIntTextField = intTextField(dummyIntBinding)
                    .component.also {
                        it.text = "20"
                        it.maximumSize = Dimension(200, 50)
                    }
                methodsNameLengthIntTextField.invoke()
            }
            row ("Specify class name length") {}
            row {
                classesNameLengthIntTextField = intTextField(dummyIntBinding)
                    .component.also {
                        it.text = "20"
                        it.maximumSize = Dimension(200, 50)
                    }
                classesNameLengthIntTextField.invoke()
            }
            row ("Specify variable name length") {}
            row {
                variablesNameLengthIntTextField = intTextField(dummyIntBinding)
                    .component.also {
                        it.text = "20"
                        it.maximumSize = Dimension(200, 50)
                    }
                variablesNameLengthIntTextField.invoke()
            }
        }.withPreferredSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    }
}
