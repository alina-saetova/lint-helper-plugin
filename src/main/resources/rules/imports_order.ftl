@file:Suppress("UnstableApiUsage")

package com.example.lint_rules.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiClass
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UFile
import org.jetbrains.uast.asRecursiveLogString
import org.jetbrains.uast.toUElement
import org.jetbrains.uast.tryResolveNamed
<#list classesScope>
<#items as clazz>
</#items>
import org.jetbrains.uast.getContainingUClass
</#list>
<#list packagesScope>
<#items as packagezz>
</#items>
import org.jetbrains.uast.getContainingUFile
</#list>

class ${detectorName} : Detector(), SourceCodeScanner {

    companion object {

        private const val ID = "ImportsOrder"
        private const val BRIEF = "You have incorrect imports order"
        private const val EXPLANATION = "Imports should have next order: "
        private val SEVERITY = Severity.WARNING

        val ISSUE = Issue.create(
            id = ID,
            briefDescription = BRIEF,
            explanation = EXPLANATION,
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 7,
            severity = SEVERITY,
            implementation = Implementation(
                ${detectorName}::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> {
        return listOf(UFile::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitFile(node: UFile) {
                <#list packagesScope>
                if (
                    <#items as packageItem>
                    node.getContainingUFile()?.packageName?.contains("${packageItem}") == false <#sep>||</#sep>
                    </#items>
                ) {
                    return
                }
                </#list>
                <#list classesScope>
                if (
                    <#items as classItem>
                    node.getContainingUClass()?.qualifiedName != "${classItem}" <#sep>||</#sep>
                    </#items>
                ) {
                    return
                }
                </#list>
                val importsClasses = node.imports.mapNotNull {
                    (it.importReference?.javaPsi as? PsiClass)?.qualifiedName
                }
                val importsFirstPackages = importsClasses.mapNotNull {
                    it.split(".").firstOrNull()
                }
                val orderMap = linkedMapOf(
                    "android" to false,
                    "third_parties" to false,
                    "java_x" to false,
                    "projects" to false
                )
                importsFirstPackages.forEach {
                    if (it.startsWith("android")) {
                        orderMap["android"] = true
                        if (
                            orderMap["third_parties"] == true
                            || orderMap["java_x"] == true
                            || orderMap["projects"] == true
                        ) {
                            context.report(
                                ISSUE,
                                context.getLocation(node),
                                BRIEF
                            )
                        }
                    }
                    if (it.startsWith("com")
                        || it.startsWith("org")
                        || it.startsWith("junit")
                        || it.startsWith("net")
                        || it.startsWith("io")
                        || it.startsWith("kotlin")
                        || it.startsWith("java")
                    ) {
                        orderMap["third_parties"] = true
                        if (orderMap["java_x"] == true || orderMap["projects"] == true) {
                            context.report(
                                ISSUE,
                                context.getLocation(node),
                                BRIEF
                            )
                        }
                    }
                    if (it.startsWith("java")
                        || it.startsWith("javax")
                    ) {
                        orderMap["java_x"] = true
                        if (orderMap["projects"] == true) {
                            context.report(
                                ISSUE,
                                context.getLocation(node),
                                BRIEF
                            )
                        }
                    }
                    orderMap["projects"] = true
                }
            }
        }
    }
}
