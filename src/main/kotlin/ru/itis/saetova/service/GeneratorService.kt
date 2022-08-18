package ru.itis.saetova.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import ru.itis.saetova.utils.CUSTOM_LINT_MODULE_NAME
import ru.itis.saetova.utils.INCLUDE_KEYWORD
import ru.itis.saetova.utils.ISSUE_REGISTRY_NAME
import ru.itis.saetova.utils.SETTINGS_GRADLE_FILE_NAME
import ru.itis.saetova.utils.SINGLE_QUOTE_SYMBOL
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

@Service
class GeneratorService(
    private val project: Project
) {

    companion object {
        private const val TEMPLATES_PATH = "/Users/alina/Documents/GitHub/lint-helper/src/main/resources/"
        private const val BUILD_GRADLE_TEMPLATE_NAME = "build_gradle_template.ftl"
        private const val BUILD_GRADLE_MANUAL_DEPS_TEMPLATE_NAME = "build_gradle_template_manual_deps.ftl"
        private const val ISSUE_REGISTRY_TOOLS_FILE_TEMPLATE_NAME = "issue_registry_tools_file_template.ftl"
        private const val ISSUE_REGISTRY_TEMPLATE_NAME = "issue_registry_template.ftl"
        private const val DETECTOR_TEMPLATE_NAME = "detector_template.ftl"
        private const val CONFIGURATION_ENCODING = "UTF-8"
        private const val ROOT_KEY_LINT_REGISTRY = "lintRegistry"
        private const val ROOT_KEY_LINT_VERSION = "lintVersion"
        private const val ROOT_KEY_PACKAGE_NAME = "packageName"
        private const val ROOT_KEY_KOTLIN_DEPENDENCY = "kotlinDependency"

        private const val ROOT_KEY_SCANNER = "scanner"
        private const val ROOT_KEY_SCOPE = "scope"
        private const val ROOT_KEY_DETECTOR_NAME = "detectorName"
        private const val ROOT_KEY_PACKAGES_SCOPE = "packagesScope"
        private const val ROOT_KEY_CLASSES_SCOPE = "classesScope"

        fun getInstance(project: Project): GeneratorService = project.service()
    }

    private lateinit var freeMarkerConfiguration: Configuration

    init {
        initConfiguration()
    }

    fun generateLintModule(
        buildGradlePath: String,
        packageName: String,
        issueRegistryToolsFilePath: String,
        issueRegistryPath: String,
        projectPath: String,
        lintVersion: String?,
        kotlinDependency: String,
    ) {
        generateBuildGradle(buildGradlePath, packageName, lintVersion, kotlinDependency)
        generateIssueRegistryToolsFile(issueRegistryToolsFilePath, packageName)
        generateIssueRegistryFile(issueRegistryPath, packageName)

        writeToSettingsGradleFile(projectPath)
    }

    fun generateDetector(
        detectorPath: String,
        packageName: String,
        scanner: String,
        scope: String,
        detectorName: String,
        parameters: Map<String, Any?> = emptyMap()
    ) {
        val root: MutableMap<String, Any?> = HashMap()
        root[ROOT_KEY_PACKAGE_NAME] = packageName
        root[ROOT_KEY_SCANNER] = scanner
        root[ROOT_KEY_SCOPE] = scope
        root[ROOT_KEY_DETECTOR_NAME] = detectorName

        parameters.forEach { (key, value) ->
            root[key] = value
        }

        val sourceFile = File("$detectorPath/$detectorName.kt")
        val writer = FileWriter(sourceFile)
        freeMarkerConfiguration.getTemplate(DETECTOR_TEMPLATE_NAME).process(root, writer)
    }

    fun generateDetectorFromTemplate(
        detectorTemplateName: String,
        detectorClassName: String,
        detectorPath: String,
        packageName: String,
        parameters: Map<String, Any?> = emptyMap(),
        packagesScope: List<String> = emptyList(),
        classesScope: List<String> = emptyList(),
    ) {
        val root: MutableMap<String, Any?> = HashMap()
        root[ROOT_KEY_PACKAGE_NAME] = packageName
        root[ROOT_KEY_DETECTOR_NAME] = detectorClassName
        root[ROOT_KEY_PACKAGES_SCOPE] = packagesScope
        root[ROOT_KEY_CLASSES_SCOPE] = classesScope

        parameters.forEach { (key, value) ->
            root[key] = value
        }

        val sourceFile = File("$detectorPath/$detectorClassName.kt")
        val writer = FileWriter(sourceFile)
        val templatePath = "rules/$detectorTemplateName"
        freeMarkerConfiguration.getTemplate(templatePath).process(root, writer)
    }

    private fun generateBuildGradle(
        buildGradlePath: String,
        packageName: String,
        lintVersion: String?,
        kotlinDependency: String
    ) {
        val root: MutableMap<String, Any?> = HashMap()
        val lintRegistryValue = "$packageName.$ISSUE_REGISTRY_NAME"
        root[ROOT_KEY_KOTLIN_DEPENDENCY] = kotlinDependency
        root[ROOT_KEY_LINT_REGISTRY] = lintRegistryValue
        if (lintVersion != null) {
            root[ROOT_KEY_LINT_VERSION] = lintVersion
        }


        val sourceFile = File(buildGradlePath)
        val writer = FileWriter(sourceFile)

        if (lintVersion != null) {
            freeMarkerConfiguration.getTemplate(BUILD_GRADLE_MANUAL_DEPS_TEMPLATE_NAME).process(root, writer)
        } else {
            freeMarkerConfiguration.getTemplate(BUILD_GRADLE_TEMPLATE_NAME).process(root, writer)
        }
    }

    private fun generateIssueRegistryToolsFile(
        issueRegistryToolsFilePath: String,
        packageName: String
    ) {
        val root: MutableMap<String, Any?> = HashMap()
        val lintRegistryValue = "$packageName.$ISSUE_REGISTRY_NAME"
        root[ROOT_KEY_LINT_REGISTRY] = lintRegistryValue


        val sourceFile = File(issueRegistryToolsFilePath)
        val writer = FileWriter(sourceFile)

        freeMarkerConfiguration.getTemplate(ISSUE_REGISTRY_TOOLS_FILE_TEMPLATE_NAME).process(root, writer)
    }

    private fun generateIssueRegistryFile(
        issueRegistryPath: String,
        packageName: String
    ) {
        val root: MutableMap<String, Any?> = HashMap()
        root[ROOT_KEY_PACKAGE_NAME] = packageName


        val sourceFile = File(issueRegistryPath)
        val writer = FileWriter(sourceFile)

        freeMarkerConfiguration.getTemplate(ISSUE_REGISTRY_TEMPLATE_NAME).process(root, writer)
    }

    private fun writeToSettingsGradleFile(projectPath: String) {
        val includeText = "${System.lineSeparator()}$INCLUDE_KEYWORD $SINGLE_QUOTE_SYMBOL:$CUSTOM_LINT_MODULE_NAME$SINGLE_QUOTE_SYMBOL"

        FileOutputStream("$projectPath/$SETTINGS_GRADLE_FILE_NAME", true).use { fos ->
            val buffer = includeText.toByteArray()
            fos.write(buffer, 0, buffer.size)
        }
    }

    private fun initConfiguration() {
        freeMarkerConfiguration = Configuration(Configuration.VERSION_2_3_30).apply {
            setDirectoryForTemplateLoading(File(TEMPLATES_PATH))
            defaultEncoding = CONFIGURATION_ENCODING
            templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
            logTemplateExceptions = false
            wrapUncheckedExceptions = true
            fallbackOnNullLoopVariable = false
        }
    }
}