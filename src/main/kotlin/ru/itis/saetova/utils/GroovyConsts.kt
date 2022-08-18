package ru.itis.saetova.utils

const val IMPL_KEYWORD = "implementation"
const val API_KEYWORD = "api"
const val TEST_IMPL_KEYWORD = "testImplementation"
const val ANDROID_TEST_IMPL_KEYWORD = "androidTestImplementation"
const val COMPILE_KEYWORD = "compile"
const val TEST_COMPILE_KEYWORD = "testCompile"
const val PROCESSOR_KEYWORD = "annotationProcessor"
val DEPENDENCIES_BLOCK_KEYWORDS = listOf(
    IMPL_KEYWORD, API_KEYWORD, TEST_IMPL_KEYWORD,
    COMPILE_KEYWORD, TEST_COMPILE_KEYWORD, PROCESSOR_KEYWORD,
    ANDROID_TEST_IMPL_KEYWORD
)
const val COMPILE_SDK_VERSION_KEYWORD = "compileSdkVersion"

@Deprecated("support build.gradle.KTS files")
const val BUILD_GRADLE_FILE_NAME = "build.gradle"
const val SETTINGS_GRADLE_FILE_NAME = "settings.gradle"

const val ANDROID_LINT_PLUGIN_DEP = "id \'com.android.lint\'"
const val ANDROID_APPLICATION_PLUGIN_DEP = "id \'com.android.application\'"
const val PREFIX_PLUGIN_DEP = "id \'"
const val ANDROID_LIBRARY_DEP = "id \'com.android.library\'"
const val CHECK_DEPS_KEYWORD = "checkDependencies"
const val INCLUDE_KEYWORD = "include"
const val LINT_OPTIONS_CLOSURE = """
    lintOptions {
        $CHECK_DEPS_KEYWORD true
    }
"""

const val APPLICATION_ID_KEYWORD = "applicationId"

const val RUN_LINT_IN_PROCESS_PROPERTY = "android.experimental.runLintInProcess"

const val AGP_DEP_NAME_1 = "classpath \'com.android.tools.build:gradle:"
const val AGP_DEP_NAME_2 = "classpath \"com.android.tools.build:gradle:"

const val LINT_UP_COUNT = 23
const val LINT_VERSION_VARIABLE = "lintVersion"
const val LINT_DEPS_BLOCK_1 = """
    lintDeps = [
        lint: "com.android.tools.lint:lint-api:${"$"}$LINT_VERSION_VARIABLE",
        lintChecks: "com.android.tools.lint:lint-checks:${"$"}$LINT_VERSION_VARIABLE"
    ]
"""
const val LINT_DEPS_BLOCK_2 = """
    lintTestDeps = [
        lintTests: "com.android.tools.lint:lint-tests:${"$"}$LINT_VERSION_VARIABLE"
    ]
"""

const val CUSTOM_LINT_MODULE_NAME = "lint-rules"
const val CUSTOM_LINT_PACKAGE_DIR_NAME = "lint_rules"
const val SRC_JAVA_DIRS = "src/main/java"
const val SRC_RES_DIRS = "src/main/resources"

const val ISSUE_REGISTRY_NAME = "IssueRegistry"
const val ISSUE_REGISTRY_TOOLS_FILE_DIR = "META-INF/services"
const val ISSUE_REGISTRY_TOOLS_FILE_NAME = "com.android.tools.lint.client.api.$ISSUE_REGISTRY_NAME"
const val ISSUE_REGISTRY_FILE_NAME = "$ISSUE_REGISTRY_NAME.kt"

const val LINT_CHECKS_PROJECT = "lintChecks project"
const val LINT_CHECKS_DEP = "$LINT_CHECKS_PROJECT(\":$CUSTOM_LINT_MODULE_NAME\")"
const val LINT_REGISTRY_ATTR_NAME = "Lint-Registry-v2"

const val KOTLIN_DEP_1 = "implementation \"org.jetbrains.kotlin:kotlin-stdlib"
const val KOTLIN_DEP_2 = "implementation \'org.jetbrains.kotlin:kotlin-stdlib"