@file:Suppress("UnstableApiUsage")

package com.example.lint_rules.detector.parameterized

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.getMethodName
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UBlockExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UReferenceExpression
import org.jetbrains.uast.UReturnExpression
import org.jetbrains.uast.getContainingUFile
import org.jetbrains.uast.getParentOfType
import org.jetbrains.uast.isUastChildOf
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

        private const val triggerMethod = "createType"
        private const val requiredMethod = "dispose"
        private const val className = "Message"
        private const val BRIEF = "You must call $requiredMethod method on the resulting object"

        val ISSUE: Issue = Issue.create(
            id = "MethodCallingRequired",
            briefDescription = BRIEF,
            explanation = "You must call $requiredMethod",
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                ${detectorName}::class.java,
                JAVA_FILE_SCOPE
            )
        )
            .setAndroidSpecific(true)
    }

    override fun getApplicableMethodNames(): List<String> {
        return listOf(triggerMethod)
    }

    override fun visitMethodCall(
        context: JavaContext,
        node: UCallExpression,
        method: PsiMethod
    ) {
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
        if (method.containingClass?.qualifiedName?.contains(className) == false) {
            return
        }
        val surroundingDeclaration = node.getParentOfType(
            true,
            UClass::class.java,
            UMethod::class.java,
            UBlockExpression::class.java,
            ULambdaExpression::class.java
        ) ?: return
        val parent = node.uastParent
        if (parent is UMethod
            || parent is UReferenceExpression
            && parent.uastParent is UMethod
        ) {
            return
        }
        val finder = ShowFinder(node)
        surroundingDeclaration.accept(finder)
        if (!finder.isShowCalled) {
            context.report(
                ISSUE,
                node,
                context.getCallLocation(node, includeReceiver = true, includeArguments = false),
                BRIEF
            )
        }
    }

    override fun getApplicableConstructorTypes(): List<String> {
        return listOf(className)
    }

    override fun visitConstructor(
        context: JavaContext,
        node: UCallExpression,
        constructor: PsiMethod
    ) {
        val surroundingDeclaration = node.getParentOfType(
            true,
            UClass::class.java,
            UMethod::class.java,
            UBlockExpression::class.java,
            ULambdaExpression::class.java
        ) ?: return
        val parent = node.uastParent
        if (parent is UMethod
            || parent is UReferenceExpression
            && parent.uastParent is UMethod
        ) {
            return
        }
        val finder = ShowFinder(node)
        surroundingDeclaration.accept(finder)
        if (!finder.isShowCalled) {
            context.report(
                ISSUE,
                node,
                context.getCallLocation(node, includeReceiver = true, includeArguments = false),
                BRIEF
            )
        }
    }

    private class ShowFinder(
        private val target: UCallExpression
    ) : AbstractUastVisitor() {

        var isShowCalled = false
            private set

        private var seenTarget = false

        override fun visitCallExpression(node: UCallExpression): Boolean {
            if (node === target
                ||
                node.sourcePsi != null
                &&
                node.sourcePsi === target.psi
            ) {
                seenTarget = true
            } else {
                if ((seenTarget || target == node.receiver)
                    && requiredMethod == getMethodName(node)
                ) {
                    isShowCalled = true
                }
            }
            return super.visitCallExpression(node)
        }

        override fun visitReturnExpression(node: UReturnExpression): Boolean {
            if (target.isUastChildOf(node.returnExpression, false)) {
                isShowCalled = true
            }
            return super.visitReturnExpression(node)
        }
    }
}
