package ru.itis.saetova.service

import com.android.SdkConstants
import com.android.ide.common.util.PathString
import com.android.tools.idea.util.toVirtualFile
import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiManager
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.GroovyRecursiveElementVisitor
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrAssignmentExpression
import ru.itis.saetova.utils.AGP_DEP_NAME_1
import ru.itis.saetova.utils.AGP_DEP_NAME_2
import ru.itis.saetova.utils.ANDROID_APPLICATION_PLUGIN_DEP
import ru.itis.saetova.utils.ANDROID_LIBRARY_DEP
import ru.itis.saetova.utils.ANDROID_LINT_PLUGIN_DEP
import ru.itis.saetova.utils.APPLICATION_ID_KEYWORD
import ru.itis.saetova.utils.BUILD_GRADLE_FILE_NAME
import ru.itis.saetova.utils.CHECK_DEPS_KEYWORD
import ru.itis.saetova.utils.COMPILE_SDK_VERSION_KEYWORD
import ru.itis.saetova.utils.CURLY_BRACKET
import ru.itis.saetova.utils.CUSTOM_LINT_MODULE_NAME
import ru.itis.saetova.utils.CUSTOM_LINT_PACKAGE_DIR_NAME
import ru.itis.saetova.utils.DEPENDENCIES_BLOCK_KEYWORDS
import ru.itis.saetova.utils.DOLLAR_SIGN
import ru.itis.saetova.utils.DOT_SYMBOL
import ru.itis.saetova.utils.DOUBLE_QUOTE_SYMBOL
import ru.itis.saetova.utils.EQUALS_SYMBOL
import ru.itis.saetova.utils.ISSUE_REGISTRY_FILE_NAME
import ru.itis.saetova.utils.LINT_CHECKS_DEP
import ru.itis.saetova.utils.LINT_DEPS_BLOCK_1
import ru.itis.saetova.utils.LINT_DEPS_BLOCK_2
import ru.itis.saetova.utils.LINT_OPTIONS_CLOSURE
import ru.itis.saetova.utils.ISSUE_REGISTRY_TOOLS_FILE_DIR
import ru.itis.saetova.utils.ISSUE_REGISTRY_TOOLS_FILE_NAME
import ru.itis.saetova.utils.KOTLIN_DEP_1
import ru.itis.saetova.utils.KOTLIN_DEP_2
import ru.itis.saetova.utils.LINT_VERSION_VARIABLE
import ru.itis.saetova.utils.NEW_LINE_SYMBOL
import ru.itis.saetova.utils.PREFIX_PLUGIN_DEP
import ru.itis.saetova.utils.RUN_LINT_IN_PROCESS_PROPERTY
import ru.itis.saetova.utils.SINGLE_QUOTE_SYMBOL
import ru.itis.saetova.utils.SRC_JAVA_DIRS
import ru.itis.saetova.utils.SRC_RES_DIRS
import ru.itis.saetova.utils.TAB_SYMBOL
import ru.itis.saetova.utils.getAppModule
import ru.itis.saetova.utils.getAppModuleDeps
import ru.itis.saetova.utils.getBuildGradleFilePsiFile
import ru.itis.saetova.utils.makeLintVersion
import java.io.File

@Service
class LintModuleCreatingService(
    private val project: Project
) {

    companion object {
        fun getInstance(project: Project): LintModuleCreatingService = project.service()
    }

    fun create() {
        val lintVersion = createLintDependencies()
        createModule(lintVersion)
        addLintChecksDep()

        addLintOptionsClosureToAppGradle()
        setDebugLintProperty()
    }

    private fun createLintDependencies(): String? {
        // ищем build.gradle проекта
        val projectPath = project.basePath!!
        val buildGradlePath = "${projectPath}/$BUILD_GRADLE_FILE_NAME"
        val buildGradleVirtualFile = PathString(buildGradlePath).toVirtualFile()
        val buildGradlePsiFile = buildGradleVirtualFile?.let { PsiManager.getInstance(project).findFile(it) } as GroovyFile

        // ищем версию AGP - может быть прописана вручную сразу в зависимости, или вынесена в отдельную переменную
        var versionAgp = ""
        var versionAgpVariableName = ""
        buildGradlePsiFile.accept(object : GroovyRecursiveElementVisitor() {
            override fun visitApplicationStatement(applicationStatement: GrApplicationStatement) {
                super.visitApplicationStatement(applicationStatement)
                if (versionAgp.isNotEmpty() || versionAgpVariableName.isNotEmpty()) return
                if (applicationStatement.text.startsWith(AGP_DEP_NAME_1) ||
                    applicationStatement.text.startsWith(AGP_DEP_NAME_2)
                ) {
                val version = applicationStatement.text
                    .removePrefix(AGP_DEP_NAME_1)
                    .removePrefix(AGP_DEP_NAME_2)
                    .dropLast(1)
                if (version.first() == DOLLAR_SIGN) {
                    versionAgpVariableName = version.drop(1)
                } else {
                    versionAgp = version
                }
                }
            }
        })

        // выясняем, какую версию линта ставить, исходя из версии AGP
        var lintVersion = ""
        if (versionAgp.isNotEmpty()) {
            // если версия AGP была вручную прописана, то просто извлекаем значение и делаем линт версию
            lintVersion = versionAgp.makeLintVersion()
        } else if (versionAgpVariableName.isNotEmpty()) {
            // если версия AGP была в переменной, то ищем эту переменную и извлекаем значение и делаем линт версию
            buildGradlePsiFile.accept(object : GroovyRecursiveElementVisitor() {

                override fun visitAssignmentExpression(expression: GrAssignmentExpression) {
                    super.visitAssignmentExpression(expression)
                    if (expression.text.startsWith(versionAgpVariableName)) {
                        val splitByDoubleQuote = expression.text.split(DOUBLE_QUOTE_SYMBOL)
                        val splitBySingleQuote = expression.text.split(SINGLE_QUOTE_SYMBOL)
                        if (splitByDoubleQuote.size > 1) {
                            lintVersion = splitByDoubleQuote[1].makeLintVersion()
                        } else if (splitBySingleQuote.size > 1) {
                            lintVersion = splitBySingleQuote[1].makeLintVersion()
                        }
                    }
                }
            })
        }

        // если версия AGP хранилась в переменной, значит был блок ext { }, значит создаемт линт депсы в нем
        if (lintVersion.isNotEmpty() && versionAgpVariableName.isNotEmpty()) {
            val lintVersionVariable = "$TAB_SYMBOL$LINT_VERSION_VARIABLE $EQUALS_SYMBOL $SINGLE_QUOTE_SYMBOL$lintVersion$SINGLE_QUOTE_SYMBOL$NEW_LINE_SYMBOL"
            val lintVersionExpression = GroovyPsiElementFactory.getInstance(project).createExpressionFromText(lintVersionVariable)
            val lintDepsBlockExpression1 = GroovyPsiElementFactory.getInstance(project).createExpressionFromText(
                LINT_DEPS_BLOCK_1
            )
            val lintDepsBlockExpression2 = GroovyPsiElementFactory.getInstance(project).createExpressionFromText(
                LINT_DEPS_BLOCK_2
            )

            buildGradlePsiFile.accept(object : GroovyRecursiveElementVisitor() {
                override fun visitAssignmentExpression(expression: GrAssignmentExpression) {
                    super.visitAssignmentExpression(expression)
                    if (expression.text.startsWith(versionAgpVariableName)) {
                        val parent = expression.parent
                        val lastChild = parent.lastChild
                        if (lastChild.text == CURLY_BRACKET) {
                            WriteCommandAction.runWriteCommandAction(project) {
                                parent.addBefore(lintVersionExpression, lastChild)
                                parent.addBefore(lintDepsBlockExpression1, lastChild)
                                parent.addBefore(lintDepsBlockExpression2, lastChild)
                            }
                        }
                    }
                }
            })
        }

        // если версия AGP была прописана вручную, то при создании модуля линта также вручную прописываем версию линт-зависимостей
        return if (lintVersion.isNotEmpty() && versionAgp.isNotEmpty()) {
            lintVersion
        } else {
            null
        }
    }

    private fun createModule(lintVersion: String?) {
        var applicationId = ""
        var kotlinDependency = ""
        // ищем applicationId для создания packageName линт модуля
        val buildGradlePsiFile = project.getAppModule().getBuildGradleFilePsiFile() ?: return
        buildGradlePsiFile.accept(object : GroovyRecursiveElementVisitor() {

            override fun visitApplicationStatement(applicationStatement: GrApplicationStatement) {
                super.visitApplicationStatement(applicationStatement)
                if (applicationId.isNotEmpty() && kotlinDependency.isNotEmpty()) return
                if (applicationStatement.text.contains(APPLICATION_ID_KEYWORD)) {
                    applicationId = applicationStatement.text
                        .removePrefix("$APPLICATION_ID_KEYWORD $DOUBLE_QUOTE_SYMBOL")
                        .dropLastWhile { it != DOT_SYMBOL }
                        .dropLast(1)
                }
                if (applicationStatement.text.contains(KOTLIN_DEP_1) ||
                    applicationStatement.text.contains(KOTLIN_DEP_2)) {
                    kotlinDependency = applicationStatement.text
                }
            }
        })

        val projectPath = project.basePath!!
        val packages = applicationId.split(DOT_SYMBOL)
        var srcDirsFromPackages = ""
        packages.forEach {
            srcDirsFromPackages += "$it/"
        }
        // генерируем пути для папок и файлов модуля
        val srcPath = "${projectPath}/$CUSTOM_LINT_MODULE_NAME/$SRC_JAVA_DIRS/$srcDirsFromPackages$CUSTOM_LINT_PACKAGE_DIR_NAME/"
        val buildGradlePath = "${projectPath}/$CUSTOM_LINT_MODULE_NAME/$BUILD_GRADLE_FILE_NAME"
        val issueRegistryToolsFileDirtPath = "${projectPath}/$CUSTOM_LINT_MODULE_NAME/$SRC_RES_DIRS/$ISSUE_REGISTRY_TOOLS_FILE_DIR/"
        val issueRegistryToolsFilePath = "$issueRegistryToolsFileDirtPath/$ISSUE_REGISTRY_TOOLS_FILE_NAME"
        val issueRegistryPath = "${srcPath}$ISSUE_REGISTRY_FILE_NAME"
        val srcDir = File(srcPath)

        if (!srcDir.exists()) {
            srcDir.mkdirs()
            File(issueRegistryToolsFileDirtPath).mkdirs()
            val packageName = "${applicationId}.$CUSTOM_LINT_PACKAGE_DIR_NAME"
            GeneratorService.getInstance(project).generateLintModule(
                buildGradlePath,
                packageName,
                issueRegistryToolsFilePath,
                issueRegistryPath,
                projectPath,
                lintVersion,
                kotlinDependency
            )
        }
    }

    // добавляем к модулям зависимость вида lintChecks project(":lint-rules")
    private fun addLintChecksDep() {
        val appModule = project.getAppModule()
        addLintChecksDepToModule(appModule)
        project.getAppModuleDeps().forEach {
            addLintChecksDepToModule(it)
        }
    }

    private fun addLintChecksDepToModule(module: Module) {
        val buildGradlePsiFile = module.getBuildGradleFilePsiFile() ?: return

        val lintChecksDep = GroovyPsiElementFactory.getInstance(project).createExpressionFromText(LINT_CHECKS_DEP)
        val androidLintPluginDep = GroovyPsiElementFactory.getInstance(project).createExpressionFromText(
            ANDROID_LINT_PLUGIN_DEP
        )

        buildGradlePsiFile.accept(object : GroovyRecursiveElementVisitor() {

            override fun visitApplicationStatement(applicationStatement: GrApplicationStatement) {
                super.visitApplicationStatement(applicationStatement)
                val parent = applicationStatement.parent

                // находим блок с dependencies { }, добавляем зависимость, если ее еще нет
                if (DEPENDENCIES_BLOCK_KEYWORDS.any { applicationStatement.text.contains(it) } &&
                    parent.children.none { it.text == LINT_CHECKS_DEP }
                ) {
                    val lastChild = parent.lastChild
                    val preLastChild = parent.children[parent.children.size - 3]
                    if (lastChild.text == CURLY_BRACKET && preLastChild.text == applicationStatement.text) {
                        WriteCommandAction.runWriteCommandAction(project) {
                            parent.addBefore(lintChecksDep, lastChild)
                        }
                    }
                }
                // если у модуля нет android зависимостей, то добавляем линтовый plugin
                if (applicationStatement.text.contains(PREFIX_PLUGIN_DEP) &&
                    parent.children.none {
                        it.text == ANDROID_LINT_PLUGIN_DEP ||
                                it.text == ANDROID_APPLICATION_PLUGIN_DEP ||
                                it.text == ANDROID_LIBRARY_DEP
                    }
                ) {
                    val lastChild = parent.lastChild
                    val preLastChild = parent.children[parent.children.size - 3]
                    if (lastChild.text == CURLY_BRACKET && preLastChild.text == applicationStatement.text) {
                        WriteCommandAction.runWriteCommandAction(project) {
                            parent.addBefore(androidLintPluginDep, lastChild)
                        }
                    }
                }
            }
        })
    }

    // добавляем блок с lintOptions в app модуль
    private fun addLintOptionsClosureToAppGradle() {
        var isClosureCreated = false

        val buildGradlePsiFile = project.getAppModule().getBuildGradleFilePsiFile() ?: return
        buildGradlePsiFile.accept(object : GroovyRecursiveElementVisitor() {

            override fun visitApplicationStatement(applicationStatement: GrApplicationStatement) {
                super.visitApplicationStatement(applicationStatement)
                val parent = applicationStatement.parent
                if (isClosureCreated) return
                if (parent.children.any { it.text.contains(CHECK_DEPS_KEYWORD) }) return
                if (applicationStatement.text.contains(COMPILE_SDK_VERSION_KEYWORD)) {
                    val lastChild = parent.lastChild
                    if (lastChild.text == CURLY_BRACKET) {
                        isClosureCreated = true
                        val checkDepsClosure = GroovyPsiElementFactory.getInstance(project).createExpressionFromText(
                            LINT_OPTIONS_CLOSURE
                        )
                        WriteCommandAction.runWriteCommandAction(project) {
                            parent.addBefore(checkDepsClosure, lastChild)
                        }
                    }
                }
            }
        })
    }

    // добавлять проперти для возможности дебажить линт правила
    private fun setDebugLintProperty() {
        WriteCommandAction.runWriteCommandAction(project) {
            val projectProperties = VfsUtil.findFileByIoFile(
                File(FileUtil.toCanonicalPath(project.basePath)),
                true
            )?.let {
                PsiManager.getInstance(project).findFile(
                    it.findOrCreateChildData(this, SdkConstants.FN_GRADLE_PROPERTIES)
                ) as? PropertiesFile
            }
            projectProperties?.let {
                it.findPropertyByKey(RUN_LINT_IN_PROCESS_PROPERTY)?.setValue(true.toString()) ?: it.addProperty(
                    RUN_LINT_IN_PROCESS_PROPERTY,
                    true.toString()
                )
            }
        }
    }
}