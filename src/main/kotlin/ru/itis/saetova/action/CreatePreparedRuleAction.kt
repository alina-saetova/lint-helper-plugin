package ru.itis.saetova.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.itis.saetova.utils.logInfo
import ru.itis.saetova.wizard.CreatePreparedRuleWizardDialog

class CreatePreparedRuleAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = requireNotNull(e.project)
        project.logInfo("Create prepared rule action performed")
        project.logInfo("project name = ${e.project?.basePath}")

        CreatePreparedRuleWizardDialog(project).show()
    }
}