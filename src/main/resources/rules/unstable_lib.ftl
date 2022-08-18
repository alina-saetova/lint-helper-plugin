@file:Suppress("UnstableApiUsage")

package com.example.lint_rules.detector

import com.android.ide.common.repository.GradleCoordinate
import com.android.tools.lint.checks.GradleDetector
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.GradleContext
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity

class ${detectorName} : Detector(), Detector.GradleScanner {

    companion object {
        private const val ID = "UnstableLibraryVersion"
        private const val BRIEF = "Using libraries with unstable version"
        private const val EXPLANATION = "Do not use libraries with unstable version"
        private val SEVERITY = Severity.WARNING

        val ISSUE = Issue.create(
            id = ID,
            briefDescription = BRIEF,
            explanation = EXPLANATION,
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 9,
            severity = SEVERITY,
            implementation = Implementation(
                ${detectorName}::class.java,
                Scope.GRADLE_SCOPE
            )
        )
    }

    override fun checkDslPropertyAssignment(
        context: GradleContext,
        property: String,
        value: String,
        parent: String,
        parentParent: String?,
        propertyCookie: Any,
        valueCookie: Any,
        statementCookie: Any
    ) {
        if (parent == "dependencies") {
            var dependency = GradleContext.getStringLiteralValue(value)
            if (dependency == null) {
                dependency = GradleDetector.getNamedDependency(value)
            }
            var gc = GradleCoordinate.parseCoordinateString(dependency)
            if (dependency != null && gc != null) {
                if (dependency.contains("$")) {
                    gc = resolveCoordinate(context, property, gc)
                }
                checkDependencyOnContainingUnstableVersion(gc, context, statementCookie)
            }
        }
    }

    private fun checkDependencyOnContainingUnstableVersion(
        gc: GradleCoordinate,
        context: GradleContext,
        statementCookie: Any
    ) {
        if (gc.revision.contains("beta", ignoreCase = true) ||
            gc.revision.contains("alpha", ignoreCase = true) ||
            gc.revision.contains("rc", ignoreCase = true)
        ) {
            val location = context.getLocation(statementCookie)
            context.report(ISSUE, location, EXPLANATION)
        }
    }

    private fun resolveCoordinate(
        context: GradleContext,
        property: String,
        gc: GradleCoordinate
    ): GradleCoordinate? {
        assert(gc.revision.contains("$"))
        val project = context.project
        val variant = project.buildVariant
        if (variant != null) {
            val artifact =
                when {
                    property.startsWith("androidTest") -> variant.androidTestArtifact
                    property.startsWith("test") -> variant.testArtifact
                    else -> variant.mainArtifact
                } ?: return null
            for (library in artifact.dependencies.getAll()) {
                val mc = library.resolvedCoordinates
                if (mc.groupId == gc.groupId &&
                    mc.artifactId == gc.artifactId
                ) {
                    val revisions = GradleCoordinate.parseRevisionNumber(mc.version)
                    if (revisions.isNotEmpty()) {
                        return GradleCoordinate(
                            mc.groupId, mc.artifactId, revisions, null
                        )
                    }
                    break
                }
            }
        }
        return null
    }
}
