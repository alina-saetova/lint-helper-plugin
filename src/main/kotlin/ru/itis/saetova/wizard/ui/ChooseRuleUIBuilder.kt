package ru.itis.saetova.wizard.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.panel
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.base.WizardStepUIBuilder
import ru.itis.saetova.wizard.state.ChooseRuleStepState
import java.awt.Dimension
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent

class ChooseRuleUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
        const val RULE_LABEL_TEXT = "Choose rule"
    }

    private var ruleModel = DefaultComboBoxModel(ChooseRuleStepState.Rule.getPresentationModels())
    private var selectedRule: ChooseRuleStepState.Rule = ChooseRuleStepState.Rule.METHOD_PARAMS_COUNT

    override fun getStepState(): WizardStepState {
        return ChooseRuleStepState(
            rule = selectedRule
        )
    }

    override fun build(): JComponent {
        return panel {
            buttonGroup {
                getRuleSelection()
            }
        }.withPreferredSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    }

    private fun LayoutBuilder.getRuleSelection() {
        row {
            val ruleComboBox = comboBox(
                model = ruleModel,
                getter = { ChooseRuleStepState.Rule.METHOD_PARAMS_COUNT.presentationName }, setter = {}
            ).component.also {
                it.addActionListener { _ ->
                    selectedRule = ChooseRuleStepState.Rule.getDataModel(it.selectedItem as String)
                }
                it.size = Dimension(300, 50)
            }

            row {
                label(RULE_LABEL_TEXT)
                ruleComboBox.invoke()
            }
        }
    }
}
