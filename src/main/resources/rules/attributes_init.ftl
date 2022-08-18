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
import com.android.tools.lint.detector.api.XmlScanner
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

class ${detectorName} : Detector(), XmlScanner {

    companion object {

        private const val paramsCount = ${paramsCount}
        private const val ID = "AttributesInit"
        private const val BRIEF = "You must initialize all custom attributes in this view"
        private const val EXPLANATION = "Custom attributes are required"
        private const val PRIORITY = 7
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

    override fun getApplicableAttributes(): List<String> = XmlScannerConstants.ALL

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.LAYOUT
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
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
        val viewQualifiedName = attribute.ownerElement?.nodeName ?: return
        if (viewQualifiedName == requiredViewQualName) {
            val viewAttributes = attribute.ownerElement?.attributes ?: return
            var isRequiredAttrExisted = false
            for (i in 0..viewAttributes.length) {
                val attr = viewAttributes.item(i)
                if (attr.nodeName == requiredAttributeName) {
                    isRequiredAttrExisted = true
                    break
                }
            }
            if (isRequiredAttrExisted.not()) {
                context.report(
                    ISSUE,
                    context.getElementLocation(attribute.ownerElement),
                    BRIEF,
                    createFix()
                )
            }
        }
    }

    private fun createFix(): LintFix {
        return fix()
            .set()
            .namespace(requiredNamespace)
            .attribute(requiredAttributeName)
            .value("TODO")
            .build()
    }
}