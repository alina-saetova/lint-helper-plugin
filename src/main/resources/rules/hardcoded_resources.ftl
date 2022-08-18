@file:Suppress("UnstableApiUsage")

package com.example.lint_rules.detector

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.TextFormat
import com.android.tools.lint.detector.api.XmlContext
import com.android.tools.lint.detector.api.XmlScanner
import com.android.tools.lint.detector.api.XmlScannerConstants
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

    enum class ColorResType {
        ATTR,
        COLOR,
    }

    companion object {
        private const val checkColorRes = true
        private val checkedColorType = ColorResType.COLOR
        private const val checkFontSize = true
        private const val checkDimens = true
        private const val checkDimens0dp = true

        private const val ID = "HardcodedResources"
        private const val BRIEF = "You used hardcoded resources here"
        private const val EXPLANATION =
            "Don't hardcode resources, create specified resource and use it here"

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
                || folderType == ResourceFolderType.COLOR
                || folderType == ResourceFolderType.DRAWABLE
    }

    override fun getApplicableAttributes(): Collection<String> {
        return XmlScannerConstants.ALL
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if (checkColorRes && attribute.name.endsWith("color", ignoreCase = true)) {
            if (attribute.value.startsWith("#")) {
                context.report(
                    ISSUE,
                    context.getLocation(attribute),
                    "Hardcoded color is detected"
                )
            } else if (checkedColorType == ColorResType.ATTR
                && attribute.value.startsWith("@color/")
            ) {
                context.report(
                    ISSUE,
                    context.getLocation(attribute),
                    "Use color attribute instead of color resource"
                )
            } else if (checkedColorType == ColorResType.COLOR
                && attribute.value.startsWith("@attr/")
            ) {
                context.report(
                    ISSUE,
                    context.getLocation(attribute),
                    "Use color resource instead of color attribute"
                )
            }
            return
        }
        if (checkDimens && attribute.value.endsWith("dp")) {
            if (attribute.value == "0dp") {
                if (checkDimens0dp) {
                    context.report(
                        ISSUE,
                        context.getLocation(attribute),
                        "Don't use 0dp dimens"
                    )
                }
            } else {
                context.report(
                    ISSUE,
                    context.getLocation(attribute),
                    "Create dimen resource with this value and use it here"
                )
            }
            return
        }
        if (checkFontSize && attribute.value.endsWith("sp")) {
            context.report(
                ISSUE,
                context.getLocation(attribute),
                "Create resource with this value and use it here"
            )
        }
    }
}
