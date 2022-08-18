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

class CheckChildrenViewsUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
        const val CHECKBOX_TEXT = "View must have childre"
    }

    private lateinit var viewNameTextField: JBTextField
    private lateinit var childType1TextField: JBTextField
    private lateinit var childType2TextField: JBTextField
    private lateinit var childType3TextField: JBTextField
    private lateinit var childType4TextField: JBTextField

    private var mustHaveChildren = false
    private var childType1 = ""
    private var childType2 = ""
    private var childType3 = ""
    private var childType4 = ""

    override fun getStepState(): WizardStepState {
        return RuleParamsStepState(
            "CheckChildrenViews",
            hashMapOf(
                "viewName" to viewNameTextField.text,
                "mustHaveChildren" to mustHaveChildren.toString(),
                "childType1" to childType1,
                "childType2" to childType2,
                "childType3" to childType3,
                "childType4" to childType4,
            )
        )
    }

    override fun build(): JComponent {
        return panel {
            row {
                viewNameTextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(200, 50)
                    }
                viewNameTextField.invoke()
            }
            row {
                checkBox(
                    text = CHECKBOX_TEXT,
                    isSelected = mustHaveChildren
                ).component.also {
                    it.addActionListener { _ ->
                        mustHaveChildren = it.isSelected
                    }
                }
            }
            row {
                childType1TextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(200, 50)
                    }
                childType1TextField.invoke()
            }
            row {
                childType2TextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(200, 50)
                    }
                childType2TextField.invoke()
            }
            row {
                childType3TextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(200, 50)
                    }
                childType3TextField.invoke()
            }
            row {
                childType4TextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(200, 50)
                    }
                childType4TextField.invoke()
            }
        }.withPreferredSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    }
}
