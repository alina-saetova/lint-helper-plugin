@file:Suppress("UnstableApiUsage")

package com.example.lint_rules.detector

import com.android.SdkConstants
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import com.android.tools.lint.detector.api.XmlScanner
import org.w3c.dom.Attr

class ${detectorName} : Detector(), XmlScanner {

    companion object {
        private const val ID = "ViewIdCamelCase"
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
        if (cleanValue.isInCamelCase().not()) {
            context.report(
                ISSUE,
                context.getValueLocation(attribute),
                BRIEF
            )
        }
    }

    private fun String.isInCamelCase(): Boolean {
        if (isEmpty()) return true
        val charArray = toCharArray()
        if (first().isUpperCase() || charArray.any { !it.isLetter() }) {
            return false
        }
        return charArray
            .mapIndexed { index, current ->
                current to charArray.getOrNull(index + 1)
            }
            .none {
                it.first.isUpperCase() &&
                        it.second?.isUpperCase() ?: false
            }
    }
}
