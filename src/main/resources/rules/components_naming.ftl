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
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
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

        private const val ID = "ComponentsNaming"
        private const val BRIEF = "Android components inheritors should have corresponding postfix"
        private const val EXPLANATION =
            "In example inheritor of Activity component has 'MainActivity' name"
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
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
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
                val superClasses = node.supers
                val qualNames = superClasses.map { it.qualifiedName }
                val isContainingActivityParent = qualNames.contains("android.app.Activity")
                val isContainingServiceParent = qualNames.contains("android.app.Service")
                val isContainingReceiverParent = qualNames.contains("android.content.BroadcastReceiver")
                val isContainingProviderParent = qualNames.contains("android.content.ContentProvider")
                val hasPostfix: Boolean? = when {
                    isContainingActivityParent -> node.qualifiedName?.endsWith("Activity")
                    isContainingServiceParent -> node.qualifiedName?.endsWith("Service")
                    isContainingReceiverParent -> node.qualifiedName?.endsWith("BroadcastReceiver")
                    isContainingProviderParent -> node.qualifiedName?.endsWith("ContentProvider")
                    else -> true
                }
                if (hasPostfix == false) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(node),
                        BRIEF
                    )
                }
            }
        }
    }
}
