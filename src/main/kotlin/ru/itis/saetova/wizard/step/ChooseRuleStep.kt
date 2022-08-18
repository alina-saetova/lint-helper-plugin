package ru.itis.saetova.wizard.step

import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardStep
import ru.itis.saetova.wizard.CreateRuleWithParamsWizardModel
import ru.itis.saetova.wizard.base.BaseWizardStep
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.state.ChooseRuleStepState
import ru.itis.saetova.wizard.ui.ChooseRuleUIBuilder

class ChooseRuleStep(
    project: Project,
    model: CreateRuleWithParamsWizardModel,
    stepStateListener: (WizardStepState) -> Unit,
    private val onRuleChosen: (ChooseRuleStepState.Rule) -> Unit
) : BaseWizardStep<CreateRuleWithParamsWizardModel, ChooseRuleUIBuilder, ChooseRuleStepState>(model, project, stepStateListener, STEP_TITLE) {

    companion object {
        const val STEP_TITLE = "Choose preferred rule"
    }

    override fun getViewBuilder(): ChooseRuleUIBuilder = ChooseRuleUIBuilder(project)

    override fun onNext(model: CreateRuleWithParamsWizardModel): WizardStep<*> {
        val state = uiBuilder.getStepState() as ChooseRuleStepState
        onRuleChosen.invoke(state.rule)
        return super.onNext(model)
    }
}