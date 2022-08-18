@file:Suppress("UnstableApiUsage")

package com.example.lint_rules.detector

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
import com.intellij.lang.jvm.JvmModifier
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UVariable
import org.jetbrains.uast.kotlin.KotlinConstructorUMethod
import java.util.*
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

        private const val ID = "ConstNaming"
        private const val BRIEF = "Constants must be in UPPER_SNAKE_CASE"
        private const val EXPLANATION = "See code style guides"
        private val SEVERITY = Severity.WARNING

        val ISSUE = Issue.create(
            id = ID,
            briefDescription = BRIEF,
            explanation = EXPLANATION,
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 5,
            severity = SEVERITY,
            implementation = Implementation(
                ${detectorName}::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> {
        return listOf(
            UVariable::class.java
        )
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
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
                if (node.uastParent is KotlinConstructorUMethod ||
                    node.sourcePsi is KtLambdaExpression ||
                    node.isCompiledVariable()
                ) {
                    return
                }
                val variableName = node.name
                if (
                    node.isConstValVariable() &&
                    variableName?.isInUpperCase() == false
                ) {
                    context.report(
                        ISSUE,
                        context.getLocation(node as UElement),
                        BRIEF,
                        createFix(variableName)
                    )
                }
            }
        }
    }

    private fun createFix(oldName: String): LintFix {
        val nameFixed = oldName.toUpperCase(Locale.getDefault())
        return fix()
            .replace()
            .text(oldName)
            .with(nameFixed)
            .build()
    }

    private fun UVariable.isConstValVariable(): Boolean {
        return hasModifier(JvmModifier.FINAL) && hasModifier(JvmModifier.STATIC)
    }

    private fun UVariable.isCompiledVariable(): Boolean {
        val name = name ?: return true
        return (name.startsWith("var")
                && name.toCharArray().any { it.isDigit() }) ||
                name.contains("$")
    }

    private fun String.isInUpperCase(): Boolean {
        return this.all {
            it.isUpperCase() && it.isLetter()
                    || it == '_'
        }
    }
}
