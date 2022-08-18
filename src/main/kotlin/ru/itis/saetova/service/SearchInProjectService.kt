package ru.itis.saetova.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiSearchHelper
import com.intellij.psi.search.UsageSearchContext
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

@Service
class SearchInProjectService(
    private val project: Project
) {

    companion object {
        fun getInstance(project: Project): SearchInProjectService = project.service()
    }

    private val helper = PsiSearchHelper.getInstance(project)

    fun searchClasses(searchText: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        if (searchText.isEmpty()) return emptyMap()
        helper.processElementsWithWord(
            { element, _ ->
                if (element.text.contains("class $searchText")) {
                    if (element is KtFile) {
                        element.classes.forEach {
                            if (it.name?.contains(searchText) == true && it.getKotlinFqName() != null) {
                                result[it.getKotlinFqName().toString()] = it.name.toString()
                            }
                        }
                    }
                    if (element is PsiJavaFile && element.virtualFile.canonicalPath?.contains("build/generated") == false) {
                        element.classes.forEach {
                            if (it.name?.contains(searchText) == true && it.getKotlinFqName() != null) {
                                result[it.getKotlinFqName().toString()] = it.name.toString()
                            }
                        }
                    }
                }
                return@processElementsWithWord true
            },
            GlobalSearchScope.projectScope(project),
            searchText,
            UsageSearchContext.IN_CODE,
            false
        )
        return result
    }

    fun searchMethods(searchText: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        if (searchText.isEmpty()) return emptyMap()
        helper.processElementsWithWord(
            { element, _ ->
                if (element is KtFile) {
                    element.declarations.forEach {
                        if (it.name?.contains(searchText) == true && it is KtNamedFunction && it.getKotlinFqName() != null) {
                            result[it.getKotlinFqName().toString()] = it.name.toString()
                        }
                    }
                }
                if (element is KtClass) {
                    element.declarations.forEach {
                        if (it.name?.contains(searchText) == true && it is KtNamedFunction && it.getKotlinFqName() != null) {
                            result[it.getKotlinFqName().toString()] = it.name.toString()
                        }
                    }
                }
                if (element is PsiClass) {
                    element.methods.forEach {
                        if (it.name.contains(searchText) && it.getKotlinFqName() != null) {
                            result[it.getKotlinFqName().toString()] = it.name
                        }
                    }
                }
                return@processElementsWithWord true
            },
            GlobalSearchScope.projectScope(project),
            searchText,
            UsageSearchContext.IN_CODE,
            false
        )
        return result
    }

    fun searchAnnotations(searchText: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        if (searchText.isEmpty()) return emptyMap()
        helper.processElementsWithWord(
            { element, _ ->
                if (element.text.contains("annotation class $searchText") && element is KtFile) {
                    element.classes.forEach {
                        if (it.name?.contains(searchText) == true && it.getKotlinFqName() != null) {
                            result[it.getKotlinFqName().toString()] = it.name.toString()
                        }
                    }
                }
                return@processElementsWithWord true
            },
            GlobalSearchScope.projectScope(project),
            searchText,
            UsageSearchContext.IN_CODE,
            false
        )
        return result
    }
}