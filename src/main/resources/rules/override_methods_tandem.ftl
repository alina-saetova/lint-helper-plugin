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
import com.example.lint_rules.detector.ComponentsNamingDetector
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
        private val requiredOverrides = listOf("onTouchEvent", "performClick")
        private const val ID = "OverrideTandem"
        private const val BRIEF = "Method  should be overrided together"

        val ISSUE = Issue.create(
            id = ID,
            briefDescription = BRIEF,
            explanation = BRIEF,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
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
                val overridedMethodFound = requiredOverrides.any {
                    node.findMethodsByName(it).isNotEmpty()
                }

                if (overridedMethodFound.not()) {
                    return
                }

                val allMethodsOverrided = requiredOverrides.all {
                    node.findMethodsByName(it).isNotEmpty()
                }
                if (allMethodsOverrided.not()) {
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
