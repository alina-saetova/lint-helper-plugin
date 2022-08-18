package ru.itis.saetova.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.util.logging.Logger

@Service
class LoggerService(
    private val project: Project
) {

    companion object {
        fun getInstance(project: Project): LoggerService = project.service()
    }

    fun info(message: String) {
        Logger.getLogger(project.name).info(message)
    }

    fun error(message: String) {
        Logger.getLogger(project.name).warning(message)
    }
}