package com.vaadin.testbench.unit.internal
/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import com.vaadin.flow.component.Component
import com.vaadin.flow.data.renderer.BasicRenderer
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.renderer.Renderer
import com.vaadin.flow.data.renderer.TemplateRenderer
import com.vaadin.flow.data.renderer.TextRenderer
import com.vaadin.flow.function.ValueProvider
import org.jsoup.Jsoup
import java.lang.reflect.Field
import java.lang.reflect.Method

private val _BasicRenderer_getFormattedValue: Method by lazy(LazyThreadSafetyMode.PUBLICATION) {
    val getFormattedValueM: Method = BasicRenderer::class.java.declaredMethods
        .first { it.name == "getFormattedValue" }
    getFormattedValueM.isAccessible = true
    getFormattedValueM
}

/**
 * Returns the output of this renderer for given [rowObject] formatted as close as possible
 * to the client-side output.
 */
fun <T> Renderer<T>._getPresentationValue(rowObject: T): String? = when {
    this is TemplateRenderer<T> -> {
        val renderedTemplateHtml: String = this.renderTemplate(rowObject)
        Jsoup.parse(renderedTemplateHtml).textRecursively
    }
    this is BasicRenderer<T, *> -> {
        val value: Any? = this.valueProvider.apply(rowObject)
        _BasicRenderer_getFormattedValue.invoke(this, value) as String?
    }
    this is TextRenderer<T> -> {
        renderText(rowObject)
    }
    this is ComponentRenderer<*, T> -> {
        val component: Component = createComponent(rowObject)
        component.toPrettyString()
    }
    this::class.simpleName == "LitRenderer" -> {
        // LitRenderer re-declares private members
        val templateProperty = this::class.java.getDeclaredField("templateExpression")
        templateProperty.isAccessible = true
        val templateExpression = templateProperty.get(this) as String

        val valueProvidersProperty = this::class.java.getDeclaredField("valueProviders")
        valueProvidersProperty.isAccessible = true
        val valueProviders = valueProvidersProperty.get(this) as Map<String, ValueProvider<T, *>>

        val renderedLitTemplateHtml: String = renderLitTemplate(templateExpression, valueProviders, rowObject)
        Jsoup.parse(renderedLitTemplateHtml).textRecursively
    }
    else -> null
}

/**
 * Renders the template for given [item]
 */
fun <T> TemplateRenderer<T>.renderTemplate(item: T): String {
    var template: String = this.template
    this.valueProviders.forEach { (k: String, v: ValueProvider<T, *>) ->
        if (template.contains("[[item.$k]]")) {
            template = template.replace("[[item.$k]]", v.apply(item).toString())
        }
    }
    return template
}

fun <T> renderLitTemplate(template: String, valueProviders: Map<String, ValueProvider<T, *>>, item: T): String {
    var renderedTemplate = template;
    valueProviders.forEach { (k: String, v: ValueProvider<T, *>) ->
        if (renderedTemplate.contains("\${item.$k}")) {
            renderedTemplate = renderedTemplate.replace("\${item.$k}", v.apply(item).toString())
        }
    }
    return renderedTemplate
}

/**
 * Returns the text rendered for given [item].
 */
@Suppress("UNCHECKED_CAST")
fun <T> TextRenderer<T>.renderText(item: T): String =
    createComponent(item).element.text

private val _BasicRenderer_valueProvider: Field by lazy(LazyThreadSafetyMode.PUBLICATION) {
    val javaField: Field = BasicRenderer::class.java.getDeclaredField("valueProvider")
    javaField.isAccessible = true
    javaField
}

/**
 * Returns the [ValueProvider] set to [BasicRenderer].
 */
@Suppress("UNCHECKED_CAST", "ConflictingExtensionProperty")
val <T, V> BasicRenderer<T, V>.valueProvider: ValueProvider<T, V>
    get() = _BasicRenderer_valueProvider.get(this) as ValueProvider<T, V>

private val _Renderer_template: Field by lazy(LazyThreadSafetyMode.PUBLICATION) {
    val templateF: Field = Renderer::class.java.getDeclaredField("template")
    templateF.isAccessible = true
    templateF
}

/**
 * Returns the Polymer Template set to the [Renderer].
 */
val Renderer<*>.template: String
    get() {
        val template: String? = _Renderer_template.get(this) as String?
        return template ?: ""
    }
