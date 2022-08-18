@file:Suppress("UnstableApiUsage")

package com.example.lint_rules.detector.parameterized

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
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UVariable
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
        private const val ID = "NameLength"
        private const val BRIEF = "Incorrect name length"
        private const val EXPLANATION = "Incorrect name length"

        private const val requiredClassNameLength = 16
        private const val requiredMethodNameLength = 20
        private const val requiredVariableNameLength = 12

        val ISSUE = Issue.create(
            id = ID,
            briefDescription = BRIEF,
            explanation = EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                ${detectorName}::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<String<out UElement>> {
        return listOf(
            UClass::class.java,
            UMethod::class.java,
            UVariable::class.java,
        )
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
                if (node.name?.length != requiredClassNameLength) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(node),
                        BRIEF
                    )
                }
            }

            override fun visitMethod(node: UMethod) {
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
                if (node.name.length != requiredMethodNameLength) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(node),
                        BRIEF
                    )
                }
            }

            override fun visitVariable(node: UVariable) {
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
                if (node.name?.length != requiredVariableNameLength) {
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
