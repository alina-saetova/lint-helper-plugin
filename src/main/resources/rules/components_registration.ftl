@file:Suppress("UnstableApiUsage")

package com.example.lint_rules.detector

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UClass
import org.w3c.dom.Node
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

        private const val ID = "ComponentsPermission"
        private const val BRIEF = "You did not register android component in manifest"
        private const val EXPLANATION =
            "You should do it because without permission this won't work"
        private val SEVERITY = Severity.ERROR

        val ISSUE = Issue.create(
            id = ID,
            briefDescription = BRIEF,
            explanation = EXPLANATION,
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 10,
            severity = SEVERITY,
            implementation = Implementation(
                ${detectorName}::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun applicableSuperClasses(): List<String> {
        return listOf(
            "android.app.Activity",
            "android.app.Service",
            "android.content.BroadcastReceiver",
            "android.content.ContentProvider",
        )
    }

    override fun visitClass(context: JavaContext, declaration: UClass) {
        <#list packagesScope>
        if (
            <#items as packageItem>
            declaration.getContainingUFile()?.packageName?.contains("${packageItem}") == false <#sep>||</#sep>
            </#items>
        ) {
            return
        }
        </#list>
        <#list classesScope>
        if (
            <#items as classItem>
            declaration.getContainingUClass()?.qualifiedName != "${classItem}" <#sep>||</#sep>
            </#items>
        ) {
            return
        }
        </#list>
        val manifestChildren = context.mainProject.mergedManifest.firstChild.childNodes
        var applicationNode: Node? = null

        for (i in 0..manifestChildren.length) {
            if (manifestChildren.item(i)?.nodeName == "application") {
                applicationNode = manifestChildren.item(i)
            }
        }
        if (applicationNode == null) return

        val applicationChildren = applicationNode.childNodes
        val activities = mutableListOf<Node>()
        val services = mutableListOf<Node>()
        val receivers = mutableListOf<Node>()
        val providers = mutableListOf<Node>()

        for (i in 0..applicationChildren.length) {
            when (applicationChildren.item(i)?.nodeName) {
                "activity" -> activities.add(applicationChildren.item(i))
                "service" -> services.add(applicationChildren.item(i))
                "receiver" -> receivers.add(applicationChildren.item(i))
                "provider" -> providers.add(applicationChildren.item(i))
            }
        }

        val activitiesNames = activities.getComponentsNamesFromAttributes()
        val servicesNames = services.getComponentsNamesFromAttributes()
        val receiversNames = receivers.getComponentsNamesFromAttributes()
        val providersNames = providers.getComponentsNamesFromAttributes()

        val superClasses = declaration.supers
        val qualNames = superClasses.map { it.qualifiedName }
        val isContainingActivity =
            qualNames.contains("android.app.Activity")
        val isContainingService = qualNames.contains("android.app.Service")
        val isContainingReceiver = qualNames.contains("android.content.BroadcastReceiver")
        val isContainingProvider = qualNames.contains("android.content.ContentProvider")

        val needReport = when {
            isContainingActivity -> !activitiesNames.contains(declaration.qualifiedName)
            isContainingService -> !servicesNames.contains(declaration.qualifiedName)
            isContainingReceiver -> !receiversNames.contains(declaration.qualifiedName)
            isContainingProvider -> !providersNames.contains(declaration.qualifiedName)
            else -> false
        }
        if (needReport) {
            context.report(
                ISSUE,
                context.getNameLocation(
                    declaration
                ),
                BRIEF
            )
        }
    }

    private fun List<Node>.getComponentsNamesFromAttributes(): List<String> {
        val names = mutableListOf<String>()
        forEach { node ->
            for (i in 0..node.attributes.length) {
                if (node.attributes.item(i)?.nodeName?.contains("name") == true) {
                    names.add(node.attributes.item(i).nodeValue)
                }
            }
        }
        return names
    }
}
