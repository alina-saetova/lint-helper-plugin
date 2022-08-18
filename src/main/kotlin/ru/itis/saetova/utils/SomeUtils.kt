package ru.itis.saetova.utils

import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType

fun KtProperty.isTransient() = annotationEntries.find { it.isTransientAnnotation() } != null

fun KtAnnotationEntry.isTransientAnnotation(): Boolean =
    typeReference?.analyze(BodyResolveMode.PARTIAL)?.get(BindingContext.TYPE, typeReference)?.fqNameEquals("kotlin.jvm.Transient") ?: false

fun KotlinType.fqNameEquals(fqName: String) = constructor.declarationDescriptor?.fqNameSafe?.asString() == fqName


fun KotlinType.getName() = constructor.declarationDescriptor?.name

inline fun <reified R> Iterable<*>.findInstance(): R {
    return filterIsInstance<R>().first()
}

inline fun <reified R> Iterable<*>.findInstanceOrNull(): R? {
    return filterIsInstance<R>().firstOrNull()
}