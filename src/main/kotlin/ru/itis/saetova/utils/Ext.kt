package ru.itis.saetova.utils

import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile

fun String.makeLintVersion(): String {
    val majorMinorPatch = split(DOT_SYMBOL).toMutableList()
    val major = majorMinorPatch.removeAt(0)
    majorMinorPatch.add(0, (major.toInt() + LINT_UP_COUNT).toString())
    return majorMinorPatch.joinToString(DOT_SYMBOL_)
}

fun Module.getBuildGradleFilePsiFile(): GroovyFile? {
    return getPsiFileByName(BUILD_GRADLE_FILE_NAME)
}

inline fun <reified T : PsiFile> Module.getPsiFileByName(name: String): T? {
    val moduleRootManager = ModuleRootManager.getInstance(this)
    var virtualFile: VirtualFile? = null
    moduleRootManager.fileIndex.iterateContent { item ->
        if (item.name == name) {
            virtualFile = item
        }
        return@iterateContent true
    }
    return virtualFile?.let { PsiManager.getInstance(project).findFile(it) } as? T
}