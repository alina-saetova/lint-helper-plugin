package ru.itis.saetova.wizard.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.panel
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.base.WizardStepUIBuilder
import ru.itis.saetova.wizard.state.RuleParamsStepState
import javax.swing.JComponent

class ClassMembersOrderUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
    }

    override fun getStepState(): WizardStepState {
        return RuleParamsStepState(
            "ClassMembersOrder",
            hashMapOf(
                // TODO
            )
        )
    }

    override fun build(): JComponent {
        return panel {
            // TODO
        }.withPreferredSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    }
}
