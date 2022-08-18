package ru.itis.saetova.wizard.step

import com.intellij.ui.layout.panel
import com.intellij.ui.wizard.WizardNavigationState
import com.intellij.ui.wizard.WizardStep
import ru.itis.saetova.wizard.CreateRuleWithParamsWizardModel
import javax.swing.JComponent

class FinishStep : WizardStep<CreateRuleWithParamsWizardModel>() {

    override fun prepare(state: WizardNavigationState?): JComponent {
        return panel {
            row("Everything is ready!") {}
        }
    }
}