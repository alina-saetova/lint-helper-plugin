package ru.itis.saetova.wizard.step

import com.intellij.openapi.project.Project
import ru.itis.saetova.wizard.CreatePreparedRuleWizardModel
import ru.itis.saetova.wizard.base.BaseWizardStep
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.state.LimitScopePreparedStepState
import ru.itis.saetova.wizard.ui.LimitScopePreparedUIBuilder

class LimitScopePreparedStep(
    project: Project,
    model: CreatePreparedRuleWizardModel,
    stepStateListener: (WizardStepState) -> Unit
) : BaseWizardStep<CreatePreparedRuleWizardModel, LimitScopePreparedUIBuilder, LimitScopePreparedStepState>(
    model,
    project,
    stepStateListener,
    STEP_TITLE
) {

    companion object {
        const val STEP_TITLE = "Limit rule scope"
    }

    override fun getViewBuilder(): LimitScopePreparedUIBuilder = LimitScopePreparedUIBuilder(project)
}