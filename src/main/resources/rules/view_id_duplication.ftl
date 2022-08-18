@file:Suppress("UnstableApiUsage")

package ${packageName}

import com.android.SdkConstants
import com.android.ide.common.rendering.api.ResourceNamespace
import com.android.ide.common.resources.ResourceItem
import com.android.resources.ResourceFolderType
import com.android.resources.ResourceType
import com.android.tools.lint.checks.RequiredAttributeDetector
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.LayoutDetector
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Scope.Companion.ALL_RESOURCES_SCOPE
import com.android.tools.lint.detector.api.Scope.Companion.RESOURCE_FILE_SCOPE
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import com.android.tools.lint.detector.api.stripIdPrefix
import com.android.utils.XmlUtils
import com.google.common.base.Splitter
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Sets
import org.w3c.dom.Attr
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xmlpull.v1.XmlPullParser
import java.util.*

class ${detectorName} : LayoutDetector() {

    companion object {
        private const val ID = "DuplicateId"
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
                RESOURCE_FILE_SCOPE
            )
        )
    }

    private var idToElements: MutableList<Triple<String, Element, Attr>>? = null

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.LAYOUT
    }

    override fun getApplicableAttributes(): Collection<String> {
        return listOf(SdkConstants.ATTR_ID)
    }

    override fun beforeCheckFile(context: Context) {
        idToElements = mutableListOf()
    }

    override fun afterCheckFile(context: Context) {
        val context = context as XmlContext
        val associates = idToElements?.groupBy { it.first }
        associates?.forEach {
            if (it.value.size > 1) {
                it.value.forEach {
                    context.report(
                        ISSUE,
                        context.getLocation(it.third),
                        BRIEF
                    )
                }
            }
        }
        idToElements = null
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        assert(attribute.name == SdkConstants.ATTR_ID || attribute.localName == SdkConstants.ATTR_ID)
        val id = attribute.value
        idToElements?.add(Triple(id, attribute.ownerElement, attribute))
        if (id == SdkConstants.NEW_ID_PREFIX || id == SdkConstants.ID_PREFIX) {
            val message = "Invalid id: missing value"
            context.report(
                WrongIdDetector.INVALID,
                attribute,
                context.getLocation(attribute),
                message
            )
        } else if ((id.startsWith("@+")
                    && !id.startsWith(SdkConstants.NEW_ID_PREFIX)
                    && !id.startsWith("@+android:id/"))
            || id.startsWith(SdkConstants.NEW_ID_PREFIX) && id.indexOf(
                '/',
                SdkConstants.NEW_ID_PREFIX.length
            ) != -1
        ) {
            val nameStart = if (id.startsWith(SdkConstants.NEW_ID_PREFIX)) {
                SdkConstants.NEW_ID_PREFIX.length
            } else 2
            val suggested = SdkConstants.NEW_ID_PREFIX + id.substring(nameStart).replace('/', '_')
            context.report(
                WrongIdDetector.INVALID,
                attribute,
                context.getLocation(attribute),
                suggested
            )
        }
    }
}