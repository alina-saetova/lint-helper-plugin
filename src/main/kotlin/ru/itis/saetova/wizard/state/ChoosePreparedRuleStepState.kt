package ru.itis.saetova.wizard.state

import ru.itis.saetova.wizard.base.WizardStepState

data class ChoosePreparedRuleStepState(
    val rule: Rule
) : WizardStepState {

    enum class Rule(
        val presentationName: String,
        val detectorName: String,
        val templateName: String,
    ) {

        VIEW_ID_DUPLICATION(
            presentationName = "Track View ids duplication in xml files",
            detectorName = "ViewIdDuplication",
            templateName = "view_id_duplication.ftl"
        ),
        LIVEDATA_MUTABILITY(
            presentationName = "Forbid mutability of Livedata in ViewModels",
            detectorName = "LiveDataMutability",
            templateName = "livedata_mutability.ftl"
        ),
        VIEW_ID_TYPE_SUFFIX(
            presentationName = "View ids should have suffix of its type",
            detectorName = "ViewIdTypeSuffix",
            templateName = "view_id_type_suffix.ftl"
        ),
        VIEW_ID_CAMEL_CASE(
            presentationName = "View ids should be in camelCase",
            detectorName = "ViewIdCamelCase",
            templateName = "view_id_camel_case.ftl"
        ),
        NAMED_ARGUMENTS(
            presentationName = "Use named arguments",
            detectorName = "NamedArguments",
            templateName = "named_arguments.ftl"
        ),
        SERIALIZED_NAME(
            presentationName = "Retrofit models must have @SerializedName on their fields",
            detectorName = "SerializedName",
            templateName = "serialized_name.ftl"
        ),
        SERIALIZABLE_PARCELABLE(
            presentationName = "Use Parcelable instead of Serializable mechanism",
            detectorName = "SerializableParcelable",
            templateName = "serializable_parcelable.ftl"
        ),
        ANDROID_LOG(
            presentationName = "Restrict Android Log usage",
            detectorName = "AndroidLog",
            templateName = "log_detector.ftl"
        ),
        COMPONENTS_NAME(
            presentationName = "Android Components classes should have components suffix",
            detectorName = "components_name",
            templateName = "components_name.ftl"
        ),
        FILE_RESOURCES_SNAKE_CASE(
            presentationName = "Files of android resources should be in snake_case",
            detectorName = "FilesResourcesSnakeCase",
            templateName = "files_resources_snake_case.ftl"
        ),
        PACKAGE_NAME_LOWER_CASE(
            presentationName = "Package names should be in lower case w/o underscores",
            detectorName = "PackageNameLowerCase",
            templateName = "package_name_lower_case.ftl"
        ),
        CONSTANTS_UPPER_CASE(
            presentationName = "Name of constant values should be in UPPER_SNAKE_CASE",
            detectorName = "ConstantsUpperCase",
            templateName = "constants_upper_case.ftl"
        ),
        WILDCARD_IMPORTS(
            presentationName = "Forbid using imports with wildcard",
            detectorName = "WildcardImports",
            templateName = "wildcard_imports.ftl"
        ),
        LATEINIT(
            presentationName = "Make required initialization of lateinit properties",
            detectorName = "Lateinit",
            templateName = "lateinit.ftl"
        ),
        UNSTABLE_LIBS(
            presentationName = "Check libraries version on stability",
            detectorName = "UnstableLibs",
            templateName = "unstable_libs.ftl"
        ),
        CHECK_ANDROID_COMPONENTS(
            presentationName = "Check Android Components registry and permissions in manifest",
            detectorName = "CheckAndroidComponents",
            templateName = "check_android_components.ftl"
        ),
        ;

        companion object {

            fun getPresentationModels(): Array<String> {
                return values().map { it.presentationName }.toTypedArray()
            }

            fun getDataModel(value: String): Rule {
                return values().find { it.presentationName == value }!!
            }
        }
    }
}