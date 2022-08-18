@file:Suppress("UnstableApiUsage")

package com.example.lint_rules.detector.parameterized

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
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
        private const val detectedSuperClass = "android.app.Activity"
        private const val requiredSuperClass = "androidx.appcompat.app.AppCompatActivity"
        private const val ID = "UseSuperClass"
        private const val BRIEF = "Use $requiredSuperClass instead of $detectedSuperClass"
        private const val EXPLANATION = "$detectedSuperClass is old"

        val ISSUE = Issue.create(
            id = ID,
            briefDescription = BRIEF,
            explanation = EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 5,
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
                val superClasses = node.supers
                val qualifiedNames = superClasses.map { it.qualifiedName }
                val isInheritorOf =
                    qualifiedNames.contains(detectedSuperClass)
                if (isInheritorOf) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(node),
                        BRIEF,
                        createFix(),
                    )
                }
            }
        }
    }

    private fun createFix(): LintFix {
        return fix()
            .replace()
            .text(detectedSuperClass)
            .with(requiredSuperClass)
            .build()
    }
}
