package ru.itis.saetova.wizard.state

import ru.itis.saetova.wizard.base.WizardStepState

data class ChooseRuleStepState(
    val rule: Rule
) : WizardStepState {

    enum class Rule(val presentationName: String, val templateName: String) {

        REQUIRED_METHOD_CALLING(
            presentationName = "Make method calling as required",
            templateName = "required_method_calling_detector.ftl"
        ),
        ATTRIBUTES_INIT(
            presentationName = "Required initialization of defined View attributes",
            templateName = "attributes_init.ftl"
        ),
        REQUIRES_API_LEVEL(
            presentationName = "Specify required API level of components",
            templateName = "requires_api_level.ftl"
        ),
        HARDCODED_RESOURCES(
            presentationName = "Forbid hardcoded resources in xml",
            templateName = "hardcoded_resources.ftl"
        ),
        CHECK_CHILDREN_VIEWS(
            presentationName = "Specify types of children views in containers",
            templateName = "check_children_views.ftl"
        ),
        DEPRECATED_SOURCE(
            presentationName = "Treat specified library sources as deprecated",
            templateName = "deprecated_source.ftl"
        ),
        COMPONENTS_NAME_LENGTH(
            presentationName = "Specify length of class, method or variable name",
            templateName = "components_name_length.ftl"
        ),
        METHOD_PARAMS_COUNT(
            presentationName = "Specify number of method parameters",
            templateName = "method_params_detector.ftl"
        ),
        CHECK_OBJECTS_INIT(
            presentationName = "Allow defined classes object init in specified place",
            templateName = "check_objects_init.ftl"
        ),
        REQUIRED_ANNOTATION_ON_METHOD(
            presentationName = "Make annotation required on method",
            templateName = "required_annotation_method_detector.ftl"
        ),
        REQUIRED_ANNOTATION_ON_CLASS(
            presentationName = "Make annotation required on class",
            templateName = "required_annotation_on_class_detector.ftl"
        ),
        RETURN_VALUE_IS_USED(
            presentationName = "Check that return value of specified method is used",
            templateName = "return_value_is_used.ftl"
        ),
        METHOD_WITH_ANN_MUST_BE_CALLED(
            presentationName = "Method must be annotated with specified annotation",
            templateName = "method_with_ann_must_be_called.ftl"
        ),
        OVERRIDE_METHODS_TANDEM(
            presentationName = "Specify which override methods should be in tandem",
            templateName = "override_methods_tandem.ftl"
        ),
        CLASS_MEMBERS_ORDER(
            presentationName = "Specify class members modificators order",
            templateName = "class_members_order.ftl"
        )
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