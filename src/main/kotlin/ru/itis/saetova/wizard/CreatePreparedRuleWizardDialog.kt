package ru.itis.saetova.wizard

import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardDialog
import ru.itis.saetova.service.RuleCreatingService
import ru.itis.saetova.utils.findInstance
import ru.itis.saetova.utils.logInfo
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.state.ChoosePreparedRuleStepState
import ru.itis.saetova.wizard.state.LimitScopePreparedStepState

class CreatePreparedRuleWizardDialog(
    val project: Project
) : WizardDialog<CreatePreparedRuleWizardModel>(
    project,
    true,
    CreatePreparedRuleWizardModel(project) { state -> stepStates.add(state) }
) {

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
        val rule = stepStates.findInstance<ChoosePreparedRuleStepState>().rule
        val limitScopeStepState = stepStates.findInstance<LimitScopePreparedStepState>()
        RuleCreatingService.getInstance(project).createPreparedRule(
            ruleName = rule.detectorName,
            detectorTemplateName = rule.templateName,
            packagesScope = limitScopeStepState.packages,
            classesScope = limitScopeStepState.classes,
        )
        stepStates.clear()
    }
}