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
import javax.swing.JRadioButton

class AttributesInitUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
    }

    private lateinit var viewNameTextField: JBTextField
    private lateinit var attrNameTextField: JBTextField

    private var selectedWay = "view"

    override fun getStepState(): WizardStepState {
        return RuleParamsStepState(
            "AttributesInit",
            hashMapOf(
                "viewName" to viewNameTextField.text,
                "attrName" to attrNameTextField.text,
                "selectedWay" to selectedWay,
            )
        )
    }

    override fun build(): JComponent {
        return panel {
            row {
                buttonGroup {
                    JRadioButton("Check in View class", true).also {
                        it.addChangeListener {
                            selectedWay = "view"
                        }
                    }.invoke()
                    JRadioButton("Check in xml", false).also {
                        it.addChangeListener {
                            selectedWay = "xml"
                        }
                    }.invoke()
                }
            }
            row {
                viewNameTextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(200, 50)
                    }
                viewNameTextField.invoke()
            }
            row {
                attrNameTextField = textField(dummyTextBinding)
                    .component.also {
                        it.maximumSize = Dimension(200, 50)
                    }
                attrNameTextField.invoke()
            }
        }.withPreferredSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    }
}
