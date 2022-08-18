package ru.itis.saetova.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.itis.saetova.service.LintModuleCreatingService
import ru.itis.saetova.utils.logInfo

class CreateLintModuleAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = requireNotNull(e.project)
        e.project?.logInfo("Create lint module action performed")
        e.project?.logInfo("project name = ${e.project?.basePath}")

        LintModuleCreatingService.getInstance(project).create()
    }
}