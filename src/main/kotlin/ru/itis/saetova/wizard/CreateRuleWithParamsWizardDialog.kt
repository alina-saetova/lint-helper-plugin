package ru.itis.saetova.wizard

import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardDialog
import ru.itis.saetova.service.RuleCreatingService
import ru.itis.saetova.utils.logInfo
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.state.ChooseRuleStepState
import ru.itis.saetova.wizard.state.LimitScopeStepState
import ru.itis.saetova.wizard.state.RuleParamsStepState

class CreateRuleWithParamsWizardDialog(
    val project: Project
) : WizardDialog<CreateRuleWithParamsWizardModel>(project, true, CreateRuleWithParamsWizardModel(project) { state -> stepStates.add(state) } ) {

    companion object {
        var stepStates: MutableList<WizardStepState> = mutableListOf()
    }

    override fun doCancelAction() {
        super.doCancelAction()
        project.logInfo("Cancel action performed")
    }

    override fun onWizardGoalAchieved() {
        super.onWizardGoalAchieved()
        project.logInfo("onWizardGoalAchieved $stepStates")
        val rule = (stepStates.find { it is ChooseRuleStepState } as ChooseRuleStepState).rule
        val ruleParamsStepState = (stepStates.find { it is RuleParamsStepState } as RuleParamsStepState)
        val limitScopeStepState = (stepStates.find { it is LimitScopeStepState } as LimitScopeStepState)
        RuleCreatingService.getInstance(project).createRuleWithParams(
            ruleName = ruleParamsStepState.ruleName,
            detectorTemplateName = rule.templateName,
            parameters = ruleParamsStepState.parameters,
            packagesScope = limitScopeStepState.packages,
            classesScope = limitScopeStepState.classes,
        )
        stepStates.clear()
    }
}