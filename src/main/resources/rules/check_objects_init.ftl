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
import com.android.tools.lint.detector.api.getMethodName
import org.jetbrains.uast.UBlockExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UDeclaration
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UReturnExpression
import org.jetbrains.uast.getParentOfType
import org.jetbrains.uast.isUastChildOf
import org.jetbrains.uast.tryResolveNamed
import org.jetbrains.uast.visitor.AbstractUastVisitor
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
        private const val checkedClassName = "Paint"
        private const val requiredInitClassName = "View"
        private const val ID = "ObjectInitialization"
        private const val BRIEF = ""
        private const val EXPLANATION = ""

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
                if (node.tryResolveNamed()?.name != checkedClassName) {
                    return
                }
                val surroundingDeclaration = node.getParentOfType(
                    true,
                    UClass::class.java,
                    UMethod::class.java,
                    UBlockExpression::class.java,
                    ULambdaExpression::class.java
                ) ?: return
                val visitor = UastVisitor(node)
                surroundingDeclaration.accept(visitor)
                if (visitor.founded.not()) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(node),
                        BRIEF
                    )
                }
            }
        }
    }

    private class UastVisitor(private val target: UDeclaration) : AbstractUastVisitor() {

        var founded = false

        override fun visitElement(node: UElement): Boolean {
            if (node === target || node.sourcePsi != null && node.sourcePsi === target.sourcePsi) {
                if ((node.isPsiValid && target == node.uastParent)
                    && node.tryResolveNamed()?.name == requiredInitClassName
                ) {
                    founded = true
                }
            }
            founded = false
            return super.visitElement(node)
        }
    }
}
