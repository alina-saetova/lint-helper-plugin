package ru.itis.saetova.wizard

import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardModel
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.state.ChooseRuleStepState
import ru.itis.saetova.wizard.step.AttributesInitStep
import ru.itis.saetova.wizard.step.CheckChildrenViewsStep
import ru.itis.saetova.wizard.step.CheckObjectsInitStep
import ru.itis.saetova.wizard.step.ChooseRuleStep
import ru.itis.saetova.wizard.step.ClassMembersOrderStep
import ru.itis.saetova.wizard.step.ComponentsNameLengthStep
import ru.itis.saetova.wizard.step.DeprecatedSourceStep
import ru.itis.saetova.wizard.step.FinishStep
import ru.itis.saetova.wizard.step.HardcodedResourcesStep
import ru.itis.saetova.wizard.step.LimitScopeStep
import ru.itis.saetova.wizard.step.MethodParamsRuleStep
import ru.itis.saetova.wizard.step.MethodWithAnnMustBeCalledStep
import ru.itis.saetova.wizard.step.OverrideMethodsTandemStep
import ru.itis.saetova.wizard.step.RequiredAnnotationOnClassStep
import ru.itis.saetova.wizard.step.RequiredAnnotationOnMethodStep
import ru.itis.saetova.wizard.step.RequiredMethodCallingStep
import ru.itis.saetova.wizard.step.RequiresApiLevelStep
import ru.itis.saetova.wizard.step.ReturnValueIsUsedStep

class CreateRuleWithParamsWizardModel(
    val project: Project,
    stepStateListener: (WizardStepState) -> Unit
) : WizardModel("Create Rule") {

    private val onRuleChosen: (ChooseRuleStepState.Rule) -> Unit = { rule ->
        when (rule) {
            ChooseRuleStepState.Rule.METHOD_PARAMS_COUNT -> {
                val newStep = MethodParamsRuleStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.REQUIRED_METHOD_CALLING -> {
                val newStep = RequiredMethodCallingStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.ATTRIBUTES_INIT -> {
                val newStep = AttributesInitStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.REQUIRES_API_LEVEL -> {
                val newStep = RequiresApiLevelStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.HARDCODED_RESOURCES -> {
                val newStep = HardcodedResourcesStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.CHECK_CHILDREN_VIEWS -> {
                val newStep = CheckChildrenViewsStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.DEPRECATED_SOURCE -> {
                val newStep = DeprecatedSourceStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.COMPONENTS_NAME_LENGTH -> {
                val newStep = ComponentsNameLengthStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.CHECK_OBJECTS_INIT -> {
                val newStep = CheckObjectsInitStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.RETURN_VALUE_IS_USED -> {
                val newStep = ReturnValueIsUsedStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.METHOD_WITH_ANN_MUST_BE_CALLED -> {
                val newStep = MethodWithAnnMustBeCalledStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.OVERRIDE_METHODS_TANDEM -> {
                val newStep = OverrideMethodsTandemStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.REQUIRED_ANNOTATION_ON_METHOD -> {
                val newStep = RequiredAnnotationOnMethodStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.REQUIRED_ANNOTATION_ON_CLASS -> {
                val newStep = RequiredAnnotationOnClassStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
            ChooseRuleStepState.Rule.CLASS_MEMBERS_ORDER -> {
                val newStep = ClassMembersOrderStep(project, this, stepStateListener)
                addAfter(currentStep, newStep)
            }
        }
    }

    init {
        add(ChooseRuleStep(project, this, stepStateListener, onRuleChosen))
        add(LimitScopeStep(project, this, stepStateListener))
        add(FinishStep())
    }
}