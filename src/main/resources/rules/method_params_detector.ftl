@file:Suppress("UnstableApiUsage")

package ${packageName}

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.lang.jvm.JvmModifier
import org.jetbrains.uast.UMethod
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

        private const val paramsCount = ${paramsCount}
        private const val ID = "MethodParamsCount"
        private const val BRIEF = "Count of method parameters should be less then "
        private const val EXPLANATION = "Enter your explanation"
        private const val PRIORITY = 6
        private val SEVERITY = Severity.WARNING

        val ISSUE = Issue.create(
            id = ID,
            briefDescription = BRIEF,
            explanation = EXPLANATION,
            category = Category.CUSTOM_LINT_CHECKS,
            priority = PRIORITY,
            severity = SEVERITY,
            implementation = Implementation(
                ${detectorName}::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes() = listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
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
                if (node.isCopyMethod()) {
                    return
                }
                val analyzeConstructors = ${analyzeConstructors}
                val shouldReport = node.uastParameters.size > paramsCount
                if (analyzeConstructors) {
                    if (shouldReport) {
                        context.report(
                            ISSUE,
                            context.getNameLocation(node),
                            BRIEF
                        )
                    }
                } else {
                    if (!node.isConstructor) {
                        if (shouldReport) {
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
    }

    private fun UMethod.isCopyMethod(): Boolean {
        return hasModifier(JvmModifier.FINAL) && name == "copy"
    }
}