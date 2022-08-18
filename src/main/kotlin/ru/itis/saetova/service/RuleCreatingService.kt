package ru.itis.saetova.service

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiRecursiveVisitor
import org.jetbrains.kotlin.idea.util.projectStructure.allModules
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.resolve.ImportPath
import org.jetbrains.plugins.groovy.lang.psi.GroovyRecursiveElementVisitor
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement
import ru.itis.saetova.utils.APPLICATION_ID_KEYWORD
import ru.itis.saetova.utils.DOT_SYMBOL
import ru.itis.saetova.utils.DOT_SYMBOL_
import ru.itis.saetova.utils.DOUBLE_QUOTE_SYMBOL
import ru.itis.saetova.utils.LINT_CHECKS_PROJECT
import ru.itis.saetova.utils.LINT_REGISTRY_ATTR_NAME
import ru.itis.saetova.utils.SRC_JAVA_DIRS
import ru.itis.saetova.utils.getAppModule
import ru.itis.saetova.utils.getBuildGradleFilePsiFile
import ru.itis.saetova.utils.getName
import ru.itis.saetova.utils.getPsiFileByName
import java.io.File

@Service
class RuleCreatingService(
    private val project: Project
) {

    companion object {
        fun getInstance(project: Project): RuleCreatingService = project.service()
    }

    private lateinit var lintModuleName: String

    fun createTemplate(ruleName: String, scannerToScope: Pair<String, String>) {
        val detectorName = "${ruleName}Detector"
        val detectorIssueClassName = "${ruleName}Detector.ISSUE"
        val (detectorPath, detectorImport) = pasteIssueToRegistryFile(detectorName, detectorIssueClassName)

        GeneratorService.getInstance(project).generateDetector(
            detectorPath,
            detectorImport,
            scannerToScope.first,
            scannerToScope.second,
            detectorName
        )
    }

    fun createRuleWithParams(
        ruleName: String,
        detectorTemplateName: String,
        parameters: Map<String, Any?> = emptyMap(),
        packagesScope: List<String> = emptyList(),
        classesScope: List<String> = emptyList(),
    ) {
        val detectorName = "${ruleName}Detector"
        val detectorIssueClassName = "${ruleName}Detector.ISSUE"
        val (detectorPath, detectorImport) = pasteIssueToRegistryFile(detectorName, detectorIssueClassName)

        GeneratorService.getInstance(project).generateDetectorFromTemplate(
            detectorTemplateName = detectorTemplateName,
            detectorClassName = detectorName,
            detectorPath = detectorPath,
            packageName =  detectorImport,
            parameters = parameters,
            packagesScope = packagesScope,
            classesScope = classesScope
        )
    }

    fun createPreparedRule(
        ruleName: String,
        detectorTemplateName: String,
        packagesScope: List<String> = emptyList(),
        classesScope: List<String> = emptyList(),
    ) {
        val detectorName = "${ruleName}Detector"
        val detectorIssueClassName = "${ruleName}Detector.ISSUE"
        val (detectorPath, detectorImport) = pasteIssueToRegistryFile(detectorName, detectorIssueClassName)

        GeneratorService.getInstance(project).generateDetectorFromTemplate(
            detectorTemplateName = detectorTemplateName,
            detectorClassName = detectorName,
            detectorPath = detectorPath,
            packageName =  detectorImport,
            parameters = emptyMap(),
            packagesScope = packagesScope,
            classesScope = classesScope
        )
    }

    private fun pasteIssueToRegistryFile(
        detectorName: String,
        detectorIssueClassName: String,
    ): Pair<String, String> {
        val issueRegistryFile = findIssueRegistryFile()
        var issues = arrayOf<String>()
        issueRegistryFile?.accept(object : KtTreeVisitorVoid(), PsiRecursiveVisitor {

            override fun visitProperty(property: KtProperty) {
                val type = property.type() ?: return
                val argumentsType = type.arguments.singleOrNull()?.type ?: return
                if (type.getName()?.asString() == "List" && argumentsType.getName()?.asString() == "Issue") {
                    val factory = KtPsiFactory(property)
                    issues = property.text.split("listOf(").last()
                        .split(",")
                        .filter { it.contains(".ISSUE") }
                        .map { it.trim() }.toTypedArray()
                    val pastedIssues = listOf(*issues, detectorIssueClassName).joinToString(",\n\t\t\t")
                    val p = factory.createProperty( """
                        override val issues: List<Issue>
                            get() = listOf(
                                $pastedIssues,
                            )
                    """
                    )
                    WriteCommandAction.runWriteCommandAction(project) {
                        property.replace(p)
                    }
                }
            }
        })

        var detectorsPackage: String? = null
        if (issues.isNotEmpty()) {
            val detectorIssue = issues.map { it.removeSuffix(".ISSUE") }.first()
            var detectorImport = ""

            issueRegistryFile?.accept(object : KtTreeVisitorVoid(), PsiRecursiveVisitor {

                override fun visitImportList(importList: KtImportList) {
                    super.visitImportList(importList)
                    detectorImport = importList.imports
                        .mapNotNull { it.importPath?.fqName?.asString() }
                        .find { it.contains(detectorIssue) }!!
                }
            })
            detectorsPackage = detectorImport.split(".").dropLast(1).joinToString(".")
        }
        val (detectorPath, detectorImport) = getPathAndImportForDetector(detectorsPackage)

        issueRegistryFile?.accept(object : KtTreeVisitorVoid(), PsiRecursiveVisitor {
            override fun visitImportList(importList: KtImportList) {
                super.visitImportList(importList)
                val factory = KtPsiFactory(importList)
                val import = factory.createImportDirective(ImportPath.fromString("$detectorImport.$detectorName"))
                WriteCommandAction.runWriteCommandAction(project) {
                    importList.add(import)
                }
            }
        })
        return detectorPath to detectorImport
    }

    private fun findIssueRegistryFile(): KtFile? {
        val appGradleFile = project.getAppModule().getBuildGradleFilePsiFile() ?: return null

        appGradleFile.accept(object : GroovyRecursiveElementVisitor() {

            override fun visitApplicationStatement(applicationStatement: GrApplicationStatement) {
                super.visitApplicationStatement(applicationStatement)
                applicationStatement.parent.children.find { it.text.contains(LINT_CHECKS_PROJECT) }?.let {
                    lintModuleName = it.text.removePrefix("$LINT_CHECKS_PROJECT(\":").removeSuffix("\")")
                }
            }
        })
        val lintModule = project.allModules().find { it.name.split(DOT_SYMBOL_).any { it == lintModuleName } } ?: return null

        val lintModuleGradleFile = lintModule.getBuildGradleFilePsiFile() ?: return null

        var issueRegistryFileName = ""
        lintModuleGradleFile.accept(object : GroovyRecursiveElementVisitor() {

            override fun visitNamedArgument(argument: GrNamedArgument) {
                super.visitNamedArgument(argument)
                if (argument.text.contains(LINT_REGISTRY_ATTR_NAME)) {
                    issueRegistryFileName = argument.text
                        .removePrefix("\"$LINT_REGISTRY_ATTR_NAME\": \"")
                        .removeSuffix(DOUBLE_QUOTE_SYMBOL)
                        .split(DOT_SYMBOL_)
                        .last()
                }
            }
        })

        return lintModule.getPsiFileByName("$issueRegistryFileName.kt")
    }

    private fun getPathAndImportForDetector(detectorsPackage: String?): Pair<String, String> {
        var applicationId = ""
        val buildGradlePsiFile = project.getAppModule().getBuildGradleFilePsiFile()
        buildGradlePsiFile?.accept(object : GroovyRecursiveElementVisitor() {

            override fun visitApplicationStatement(applicationStatement: GrApplicationStatement) {
                super.visitApplicationStatement(applicationStatement)
                if (applicationId.isNotEmpty()) return
                if (applicationStatement.text.contains(APPLICATION_ID_KEYWORD)) {
                    applicationId = applicationStatement.text
                        .removePrefix("$APPLICATION_ID_KEYWORD $DOUBLE_QUOTE_SYMBOL")
                        .dropLastWhile { it != DOT_SYMBOL }
                        .dropLast(1)
                }
            }
        })

        val projectPath = project.basePath!!
        val packages = applicationId.split(DOT_SYMBOL)
        var srcDirsFromPackages = ""
        packages.forEach {
            srcDirsFromPackages += "$it/"
        }
        val lintModuleNamePath = lintModuleName.replace('-', '_')
        val rootPath = "${projectPath}/$lintModuleName/$SRC_JAVA_DIRS/$srcDirsFromPackages$lintModuleNamePath"

        val detectorPath = if (detectorsPackage == null) {
            "$rootPath/detector/"
        } else {
            "$rootPath/${detectorsPackage.split(".").last()}/"
        }
        File(detectorPath).run {
            if (!exists()) mkdirs()
        }
        val detectorImport = detectorsPackage ?: "$applicationId.$lintModuleNamePath.detector"
        return detectorPath to detectorImport
    }
}