package ru.itis.saetova.wizard.step

import com.intellij.openapi.project.Project
import ru.itis.saetova.wizard.CreateRuleWithParamsWizardModel
import ru.itis.saetova.wizard.base.BaseWizardStep
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.state.LimitScopeStepState
import ru.itis.saetova.wizard.ui.LimitScopeUIBuilder

class LimitScopeStep(
    project: Project,
    model: CreateRuleWithParamsWizardModel,
    stepStateListener: (WizardStepState) -> Unit
) : BaseWizardStep<CreateRuleWithParamsWizardModel, LimitScopeUIBuilder, LimitScopeStepState>(model, project, stepStateListener, STEP_TITLE) {

    companion object {
        const val STEP_TITLE = "Limit rule scope"
    }

    override fun getViewBuilder(): LimitScopeUIBuilder = LimitScopeUIBuilder(project)
}