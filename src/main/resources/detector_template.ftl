@file:Suppress("UnstableApiUsage")

package ${packageName}

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.${scanner}
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity

class ${detectorName} : Detector(), ${scanner} {

    companion object {

        private const val ID = "Enter your id"
        private const val BRIEF = "Enter your brief"
        private const val EXPLANATION = "Enter your explanation"
        private const val PRIORITY = 8 // choose priority
        private val SEVERITY = Severity.WARNING // choose severity

        val ISSUE = Issue.create(
            id = ID,
            briefDescription = BRIEF,
            explanation = EXPLANATION,
            category = Category.CUSTOM_LINT_CHECKS,
            priority = PRIORITY,
            severity = SEVERITY,
            implementation = Implementation(
                ${detectorName}::class.java,
                Scope.${scope}
            )
        )
    }
}