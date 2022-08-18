package ru.itis.saetova.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.itis.saetova.dialog.CreateRuleTemplateDialog
import ru.itis.saetova.utils.logInfo

class CreateRuleTemplateAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = requireNotNull(e.project)
        project.logInfo("Create rule template action performed")
        project.logInfo("project name = ${project.basePath}")

        CreateRuleTemplateDialog(project).show()
    }
}