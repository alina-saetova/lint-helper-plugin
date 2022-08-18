package ru.itis.saetova.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.itis.saetova.utils.logInfo
import ru.itis.saetova.wizard.CreateRuleWithParamsWizardDialog

class CreateRuleWithParamsAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = requireNotNull(e.project)
        project.logInfo("Create rule with parameters action performed")
        project.logInfo("project name = ${e.project?.basePath}")

        CreateRuleWithParamsWizardDialog(project).show()
    }
}