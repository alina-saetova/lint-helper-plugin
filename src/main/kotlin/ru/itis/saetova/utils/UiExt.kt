package ru.itis.saetova.utils

import com.intellij.ui.layout.PropertyBinding

val dummyTextBinding: PropertyBinding<String> = PropertyBinding({ "" }, {})
val dummyIntBinding: PropertyBinding<Int> = PropertyBinding({ 1 }, {})
val dummyNullableTextBinding: PropertyBinding<String?> = PropertyBinding({ "" }, {})