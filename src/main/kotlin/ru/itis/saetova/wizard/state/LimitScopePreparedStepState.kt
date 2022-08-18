package ru.itis.saetova.wizard.state

import ru.itis.saetova.wizard.base.WizardStepState

data class LimitScopePreparedStepState(
    val packages: List<String>,
    val classes: List<String>
) : WizardStepState