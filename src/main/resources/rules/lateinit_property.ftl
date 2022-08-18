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
import org.jetbrains.uast.UBinaryExpression
import org.jetbrains.uast.UDeclaration
import org.jetbrains.uast.UElement
import org.jetbrains.uast.getContainingUClass
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

        private const val ID = "LateinitProperty"
        private const val BRIEF = "Lateinit property has not been initialized"
        private const val EXPLANATION =
            "You have to init lateinit property otherwise you will get UninitializedPropertyAccessException"
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
        return listOf(UDeclaration::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitDeclaration(node: UDeclaration) {
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
                if (node.text.startsWith("lateinit")) {
                    val propertyName = node.text.split(".")[2]
                    val isInited = node.getContainingUClass()?.uastDeclarations?.any {
                        (node as? UBinaryExpression)?.leftOperand?.javaPsi?.text == propertyName
                    }
                    if (isInited == false) {
                        context.report(
                            ISSUE,
                            context.getLocation(node as UElement),
                            node.text
                        )
                    }
                }
            }
        }
    }
}
