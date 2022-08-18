 package ru.itis.saetova.utils

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import ru.itis.saetova.service.LoggerService

fun Project.logError(message: String) {
    LoggerService.getInstance(this).error(message)
}

fun Project.logInfo(message: String) {
    LoggerService.getInstance(this).info(message)
}

fun Project.getAppModule(): Module {
    val manager = ModuleManager.getInstance(this)
    val projectName = name.replace("\\s".toRegex(), "_")
    val appModuleName = "$projectName.app"
    return manager.findModuleByName(appModuleName) ?: throw RuntimeException("Unable to determine app module !")
}

fun Project.getAppModuleDeps(): List<Module> {
    val moduleManager = ModuleRootManager.getInstance(getAppModule())
    return moduleManager.dependencies.toList()
}
