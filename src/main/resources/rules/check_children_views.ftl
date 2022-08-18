@file:Suppress("UnstableApiUsage")

package com.example.lint_rules.detector

import com.android.SdkConstants
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.LayoutDetector
import com.android.tools.lint.detector.api.Scope.Companion.RESOURCE_FILE_SCOPE
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import com.android.utils.XmlUtils
import org.w3c.dom.Element
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

class ${detectorName} : LayoutDetector() {

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.LAYOUT
    }


    override fun getApplicableElements(): Collection<String> {
        return listOf(checkedViewName)
    }

    override fun visitElement(context: XmlContext, element: Element) {
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
        if (element.nodeName == checkedViewName) {
            val hasChildren = element.childNodes.length > 0
            if (hasChildren && mustHaveChildren) {
                context.report(
                    ISSUE,
                    context.getLocation(element),
                    BRIEF
                )
                return
            }
            val children = element.childNodes
            var hasRequiredChildren = true
            for (i in 0..children.length) {
                val child = children.item(i)
                val childName = child.nodeName
                if (childrenViewTypes.find { it == childName } == null) {
                    hasRequiredChildren = false
                    break
                }
            }
            if (hasRequiredChildren.not()) {
                context.report(
                    ISSUE,
                    context.getLocation(element),
                    BRIEF
                )
                return
            }
            if (mustHaveChildrenContainersHaveChildren) {
                for (i in 0..children.length) {
                    val child = children.item(i)
                    if (child.childNodes.length > 0) {
                        context.report(
                            ISSUE,
                            context.getLocation(element),
                            BRIEF
                        )
                        return
                    }
                }
            }
        }
    }

    companion object {
        private const val checkedViewName = "LinearLayout"
        private const val mustHaveChildren = true
        private val childrenViewTypes = listOf("TextView", "ImageView", "EditText")
        private const val mustHaveChildrenContainers = false
        private const val mustHaveChildrenContainersHaveChildren = false
        private const val ID = "CheckViewChildren"
        private const val BRIEF = "Check view children rules"

        val ISSUE = Issue.create(
            id = ID,
            briefDescription = BRIEF,
            explanation = BRIEF,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                ${detectorName}::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )
    }
}
