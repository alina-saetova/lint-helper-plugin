@file:Suppress("UnstableApiUsage")

package com.example.lint_rules.detector

import com.android.SdkConstants
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import com.android.tools.lint.detector.api.XmlScanner
import org.w3c.dom.Attr
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
        private const val ID = "ViewIdWithType"
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
                Scope.RESOURCE_FILE_SCOPE
            )
        )
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.LAYOUT
    }

    override fun getApplicableAttributes(): Collection<String> {
        return listOf(SdkConstants.ATTR_ID)
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        val value = attribute.value
        val cleanValue = value
            .removePrefix("@+id/")
            .removePrefix("@id/")

        val viewQualifiedName = attribute.ownerElement?.nodeName
        if (viewQualifiedName == null) {
            return
        }
        val viewShortName = viewQualifiedName.split(".").last()
        if (cleanValue.endsWith(viewShortName).not()) {
            context.report(
                ISSUE,
                context.getValueLocation(attribute),
                BRIEF,
                createFix(cleanValue, viewShortName)
            )
        }
    }

    private fun createFix(oldValue: String, viewType: String): LintFix {
        val valueFixed = "@+id/$oldValue$viewType"
        return fix()
            .set()
            .namespace(SdkConstants.ANDROID_URI)
            .attribute(SdkConstants.ATTR_ID)
            .value(valueFixed)
            .build()
    }
}
