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

class MethodWAnnMustBeCalledUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
    }

    private lateinit var paramsCountIntTextField: JBTextField

    private var analyzeConstructors = false

    override fun getStepState(): WizardStepState {
        return RuleParamsStepState(
            "MethodWithAnnotationMustBeCalled",
            hashMapOf(
                "paramsCount" to paramsCountIntTextField.text,
                "analyzeConstructors" to analyzeConstructors.toString(),
            )
        )
    }

    override fun build(): JComponent {
        return panel {
            row {
                paramsCountIntTextField = intTextField(dummyIntBinding)
                    .component.also {
                        it.maximumSize = Dimension(200, 50)
                    }
                paramsCountIntTextField.invoke()
            }
            row {
                checkBox(
                    text = "CHECKBOX_ANALYZE_CONSTRUCTORS_TEXT",
                    isSelected = analyzeConstructors
                ).component.also {
                    it.addActionListener { _ ->
                        analyzeConstructors = it.isSelected
                    }
                }
            }
        }.withPreferredSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    }
}
