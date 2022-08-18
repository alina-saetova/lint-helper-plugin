@file:Suppress("UnstableApiUsage")

package com.example.lint_rules.detector

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import com.android.tools.lint.detector.api.XmlScanner
import com.android.tools.lint.detector.api.XmlScannerConstants
import org.w3c.dom.Attr

class ${detectorName} : Detector(), XmlScanner {

    companion object {
        private const val ID = "ResourceFileName"
        private const val BRIEF = "Resource file name should be in lowercase with delimiter '_'"
        private const val EXPLANATION = "You should change resource file name"

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
        return true
    }

    override fun getApplicableAttributes(): Collection<String>? {
        return XmlScannerConstants.ALL
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if (
            attribute.value.startsWith("@drawable/")
            || attribute.value.startsWith("@layout/")
            || attribute.value.startsWith("@menu/")
            || attribute.value.startsWith("@font/")
            || attribute.value.startsWith("@anim/")
            || attribute.value.startsWith("@animator/")
            || attribute.value.startsWith("@mipmap/")
            || attribute.value.startsWith("@navigation/")
            || attribute.value.startsWith("@raw/")
            || attribute.value.startsWith("@xml/")
            || attribute.value.startsWith("@transition/")
            || attribute.value.startsWith("@values/")
            || attribute.value.startsWith("@color/")
        ) {
            val resourceFileName = attribute.value.split("/").last()
            if (resourceFileName.isLowerCaseWithLimiter().not()) {
                context.report(
                    ISSUE,
                    context.getValueLocation(attribute),
                    BRIEF
                )
            }
        }
    }

    private fun String.isLowerCaseWithLimiter(): Boolean {
        val cleanValue = replace("_", "")
        return cleanValue.all {
            it.isLetter() && it.isUpperCase()
        }
    }
}
