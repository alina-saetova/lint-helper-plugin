package ru.itis.saetova.wizard.state

import ru.itis.saetova.wizard.base.WizardStepState

data class RuleParamsStepState(
    val ruleName: String,
    val parameters: Map<String, Any?> = emptyMap()
) : WizardStepState