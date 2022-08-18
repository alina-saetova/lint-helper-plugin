package ru.itis.saetova.wizard.step

import com.intellij.openapi.project.Project
import ru.itis.saetova.wizard.CreatePreparedRuleWizardModel
import ru.itis.saetova.wizard.base.BaseWizardStep
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.state.ChoosePreparedRuleStepState
import ru.itis.saetova.wizard.ui.ChoosePreparedRuleUIBuilder

class ChoosePreparedRuleStep(
    project: Project,
    model: CreatePreparedRuleWizardModel,
    stepStateListener: (WizardStepState) -> Unit,
) : BaseWizardStep<CreatePreparedRuleWizardModel, ChoosePreparedRuleUIBuilder, ChoosePreparedRuleStepState>(
    model,
    project,
    stepStateListener,
    STEP_TITLE
) {

    companion object {
        const val STEP_TITLE = "Choose preferred rule"
    }

    override fun getViewBuilder(): ChoosePreparedRuleUIBuilder = ChoosePreparedRuleUIBuilder(project)
}