package ru.itis.saetova.wizard.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.panel
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.base.WizardStepUIBuilder
import ru.itis.saetova.wizard.state.ChoosePreparedRuleStepState
import ru.itis.saetova.wizard.state.ChooseRuleStepState
import java.awt.Dimension
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent

class ChoosePreparedRuleUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
        const val RULE_LABEL_TEXT = "Choose rule"
    }

    private var ruleModel = DefaultComboBoxModel(ChoosePreparedRuleStepState.Rule.getPresentationModels())
    private var selectedRule = ChoosePreparedRuleStepState.Rule.VIEW_ID_DUPLICATION

    override fun getStepState(): WizardStepState {
        return ChoosePreparedRuleStepState(
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
                getter = { ChoosePreparedRuleStepState.Rule.VIEW_ID_DUPLICATION.presentationName }, setter = {}
            ).component.also {
                it.addActionListener { _ ->
                    selectedRule = ChoosePreparedRuleStepState.Rule.getDataModel(it.selectedItem as String)
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
