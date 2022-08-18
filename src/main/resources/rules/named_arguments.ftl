@file:Suppress("UnstableApiUsage")

package ${packageName}

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.TextFormat
import com.android.tools.lint.detector.api.isJava
import com.example.lint_rules.utils.createImplementation
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.KtValueArgumentName
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
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

class ${detectorName} : Detector(), SourceCodeScanner {

    companion object {
        val ISSUE: Issue = Issue.create(
            "MustUseNamedParams",
            "Calls to @MustUseNamedParams-annotated methods must name all parameters.",
            "Calls to @MustUseNamedParams-annotated methods must name all parameters.",
            Category.CORRECTNESS,
            9,
            Severity.WARNING,
            createImplementation<${detectorName}>()
        )
    }

    override fun getApplicableUastTypes() = listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {

        return object : UElementHandler() {

            override fun visitCallExpression(node: UCallExpression) {
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
                val method = node.resolve() ?: return

                if (isJava(method.language)) return

                if (method.hasAnnotation("MustUseNamedParams")) {
                    val areAllNamed = node.sourcePsi
                        ?.getChildOfType<KtValueArgumentList>()
                        ?.children?.filterIsInstance<KtValueArgument>()
                        ?.all { it.getChildOfType<KtValueArgumentName>() != null }

                    if (areAllNamed == false) {
                        context.report(
                            ISSUE,
                            node,
                            context.getLocation(node),
                            ISSUE.getBriefDescription(TextFormat.TEXT)
                        )
                    }
                }
            }
        }
    }
}