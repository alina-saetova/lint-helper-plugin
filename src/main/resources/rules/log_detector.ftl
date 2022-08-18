@file:Suppress("UnstableApiUsage")

package ${packageName}

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
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

class AndroidLogDetector : Detector(), SourceCodeScanner {

    companion object {

        val ISSUE: Issue = Issue
            .create(
                id = "AndroidLogDetector",
                briefDescription = "The android Log should not be used",
                explanation = """
                For amazing showcasing purposes we should not use the Android Log. We should the
                AmazingLog instead.
            """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 9,
                severity = Severity.ERROR,
                androidSpecific = true,
                implementation = Implementation(
                    AndroidLogDetector::class.java,
                    Scope.JAVA_FILE_SCOPE
                )
            )
    }

    override fun getApplicableMethodNames(): List<String> =
        listOf("tag", "format", "v", "d", "i", "w", "e", "wtf")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)
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
        val evaluator = context.evaluator
        if (evaluator.isMemberInClass(method, "android.util.Log")) {
            reportUsage(context, node)
        }
    }

    private fun reportUsage(context: JavaContext, node: UCallExpression) {
        context.report(
            issue = ISSUE,
            scope = node,
            location = context.getCallLocation(
                call = node,
                includeReceiver = true,
                includeArguments = true
            ),
            message = "android.util.Log usage is forbidden."
        )
    }
}
