package ru.itis.saetova.wizard.step

import com.intellij.openapi.project.Project
import ru.itis.saetova.wizard.CreateRuleWithParamsWizardModel
import ru.itis.saetova.wizard.base.BaseWizardStep
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.state.RuleParamsStepState
import ru.itis.saetova.wizard.ui.RequiredMethodCallingUIBuilder

class RequiredMethodCallingStep(
    project: Project,
    model: CreateRuleWithParamsWizardModel,
    stepStateListener: (WizardStepState) -> Unit
) : BaseWizardStep<CreateRuleWithParamsWizardModel, RequiredMethodCallingUIBuilder, RuleParamsStepState>(model, project, stepStateListener, STEP_TITLE) {

    companion object {
        const val STEP_TITLE = "Specify the options"
    }

    override fun getViewBuilder(): RequiredMethodCallingUIBuilder = RequiredMethodCallingUIBuilder(project)
}