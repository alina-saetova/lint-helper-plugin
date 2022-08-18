package ru.itis.saetova.wizard

import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardModel
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.step.ChoosePreparedRuleStep
import ru.itis.saetova.wizard.step.FinishStep
import ru.itis.saetova.wizard.step.LimitScopePreparedStep

class CreatePreparedRuleWizardModel(
    val project: Project,
    stepStateListener: (WizardStepState) -> Unit
) : WizardModel("Create Prepared Rule") {

    init {
        add(ChoosePreparedRuleStep(project, this, stepStateListener))
        add(LimitScopePreparedStep(project, this, stepStateListener))
        add(FinishStep())
    }
}