package ru.itis.saetova.wizard.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.panel
import ru.itis.saetova.wizard.base.WizardStepState
import ru.itis.saetova.wizard.base.WizardStepUIBuilder
import ru.itis.saetova.wizard.state.RuleParamsStepState
import javax.swing.JComponent
import javax.swing.JRadioButton

class HardcodedResourcesUIBuilder(
    val project: Project
) : WizardStepUIBuilder {

    companion object {
        const val CHECKBOX_1_TEXT = "Allow 0dp values in dimens"
        const val CHECKBOX_2_TEXT = "Forbid dimens hardcoded resources"
        const val WINDOW_WIDTH = 400
        const val WINDOW_HEIGHT = 100
    }

    private var hardcodedTypes = mutableListOf<String>()
    private var allow0dpInDimens = false

    override fun getStepState(): WizardStepState {
        return RuleParamsStepState(
            "HardcodedResources",
            hashMapOf(
                "hardcodedTypes" to hardcodedTypes,
                "allow0dpInDimens" to allow0dpInDimens.toString(),
            )
        )
    }

    override fun build(): JComponent {
        return panel {
            row {
                buttonGroup {
                    JRadioButton("Forbid hardcoded colors", true).also {
                        it.addChangeListener {
                            hardcodedTypes.add("color")
                        }
                    }.invoke()
                    JRadioButton("Forbid hardcoded attr", false).also {
                        it.addChangeListener {
                            hardcodedTypes.add("attr")
                        }
                    }.invoke()
                }
            }
            row {
                checkBox(
                    text = CHECKBOX_1_TEXT,
                    isSelected = allow0dpInDimens
                ).component.also {
                    it.addActionListener { _ ->
                        allow0dpInDimens = it.isSelected
                    }
                }
            }
            row {
                checkBox(
                    text = CHECKBOX_2_TEXT,
                    isSelected = false
                ).component.also {
                    it.addActionListener { _ ->
                        if (it.isSelected) {
                            hardcodedTypes.add("dimens")
                        } else {
                            hardcodedTypes.remove("dimens")
                        }
                    }
                }
            }
        }.withPreferredSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    }
}
