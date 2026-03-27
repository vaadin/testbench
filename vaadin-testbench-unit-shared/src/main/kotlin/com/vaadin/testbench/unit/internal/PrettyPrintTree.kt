/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.internal


import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.kotlinFunction
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasValidation
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.icon.Icon
import com.vaadin.testbench.unit.internal.PrettyPrintTree.Companion.ofVaadin


/**
 * If true, [PrettyPrintTree] will use `\--` instead of `└──` which tend to render on some terminals as `???`.
 */
var prettyPrintUseAscii: Boolean = false

/**
 * Utility class to create a pretty-printed ASCII tree of arbitrary nodes that can be printed to the console.
 * You can build the tree out of any tree structure, just fill in this node [name] and its [children].
 *
 * To create a pretty tree dump of a Vaadin component, just use [ofVaadin].
 */
@Deprecated("Replace the vaadin-testbench-unit dependency with browserless-test-junit6 and use the corresponding class from the com.vaadin.browserless package instead. This class will be removed in a future version.")
class PrettyPrintTree(val name: String, val children: MutableList<PrettyPrintTree>) {

    private val pipe = if (!prettyPrintUseAscii) '│' else '|'
    private val branchTail = if (!prettyPrintUseAscii) "└── " else "\\-- "
    private val branch = if (!prettyPrintUseAscii) "├── " else "|-- "

    fun print(): String {
        val sb = StringBuilder()
        print(sb, "", true)
        return sb.toString()
    }

    private fun print(sb: StringBuilder, prefix: String, isTail: Boolean) {
        sb.append(prefix + (if (isTail) branchTail else branch) + name + "\n")
        for (i in 0 until children.size - 1) {
            children[i].print(sb, prefix + if (isTail) "    " else "$pipe   ", false)
        }
        if (children.size > 0) {
            children[children.size - 1]
                    .print(sb, prefix + if (isTail) "    " else "$pipe   ", true)
        }
    }

    companion object {

        fun ofVaadin(root: Component): PrettyPrintTree {
            val result = PrettyPrintTree(root.toPrettyString(), mutableListOf())
            for (child: Component in testingLifecycleHook.getAllChildren(root)) {
                result.children.add(ofVaadin(child))
            }
            return result
        }
    }
}

fun Component.toPrettyTree(): String = PrettyPrintTree.ofVaadin(this).print()

/**
 * Returns the most basic properties of the component, formatted as a concise string:
 * * The component class
 * * The [Component.getId]
 * * Whether the component is [Component.isVisible]
 * * Whether it is a [HasValue] that is read-only
 * * the styles
 * * The [Component.label] and text
 * * The [HasValue.getValue]
 */
@Suppress("UNCHECKED_CAST")
fun Component.toPrettyString(): String {
    val list = LinkedList<String>()
    if (id.isPresent) {
        list.add("#${id.get()}")
    }
    if (!_isVisible) {
        list.add("INVIS")
    }
    if (this is HasValue<*, *> && (this as HasValue<HasValue.ValueChangeEvent<Any?>, Any?>).isReadOnly) {
        list.add("RO")
    }
    if (!element.isEnabled) {
        list.add("DISABLED")
    }
    if (label.isNotBlank()) {
        list.add("label='$label'")
    }
    if (label != caption && caption.isNotBlank()) {
        list.add("caption='$caption'")
    }
    if (!_text.isNullOrBlank() && _text != caption) {
        list.add("text='$_text'")
    }
    if (this is HasValue<*, *>) {
        list.add("value='${(this as HasValue<HasValue.ValueChangeEvent<Any?>, Any?>).value}'")
    }
    if (this is HasValidation) {
        if (this.isInvalid) {
            list.add("INVALID")
        }
        if (!this.errorMessage.isNullOrBlank()) {
            list.add("errorMessage='$errorMessage'")
        }
    }
    /* TODO: uncomment when importing Grid stuff
    if (this is Grid.Column<*>) {
        if (this.header2.isNotBlank()) {
            list.add("header='${this.header2}'")
        }
        if (!this.key.isNullOrBlank()) {
            list.add("key='${this.key}'")
        }
    }
     */
    // TODO: add a system property to allow verbose pretty print with ignored attributes
    val ignoredAttr = mutableListOf("value", "invalid", "openOn", "label", "errorMessage", "innerHTML", "i18n","error", "stackTrace")
    this.element.propertyNames.forEach {
        val propertyValue = this.element.getProperty(it)
        if(propertyValue != null && !ignoredAttr.contains(it) && propertyValue.isNotEmpty() && !it.startsWith("_")) {
            list.add("${it}='${propertyValue}'")
        }
    }
    // Any component with href should output it not only Anchor
    val hrefCallable = try {
        val functionOrProperty = this.javaClass.kotlin.members.find { it.name == "href" }
        functionOrProperty?.isAccessible = true
        functionOrProperty
    } catch (t: TypeNotPresentException) {
        // Some components have methods referencing Spring classes that may not
        // be present for all project. Kotlin member introspection seems to
        // immediately scan for all metadata (parameters, generics, ...) making
        // this method fail. As a fallback we inspect with Java reflection API
        // that seems to be more lazy
        val method = this.javaClass.methods.find { it.name == "href" }
        method?.kotlinFunction
    }
    hrefCallable?.call(this)?.let {
        list.add("href='$it'")
    }
    if (this is Button && icon is Icon) {
        list.add("icon='${(icon as Icon).element.getAttribute("icon")}'")
    }
    if (this is Html) {
        val outerHtml: String = this.element.outerHTML.trim().replace(Regex("\\s+"), " ")
        list.add(outerHtml.ellipsize(100))
    }
    if (this is Grid<*> && this.beanType != null) {
        list.add("<${this.beanType.simpleName}>")
    }
    if (this.dataProvider != null) {
        list.add("dataprovider='${this.dataProvider}'")
    }
    element.attributeNames
        .filter { !dontDumpAttributes.contains(it) }
        .sorted() // the attributes may come in arbitrary order; make sure to sort them, in order to have predictable order and repeatable tests.
        .forEach { attributeName ->
            val value = element.getAttribute(attributeName)
            if (!value.isNullOrBlank()) {
                list.add("@$attributeName='$value'")
            }
        }
    if (this !is Html && !element.getProperty("innerHTML").isNullOrBlank()) {
        val innerHTML =
            element.getProperty("innerHTML").trim().replace(Regex("\\s+"), " ")
        list.add("innerHTML='$innerHTML'")
    }
    if (this.javaClass.hasCustomToString()) {
        // by default Vaadin components do not introduce toString() at all;
        // toString() therefore defaults to Object's toString() which is useless. However,
        // if a component does introduce a toString() then use it - it could provide
        // valuable information.
        list.add(this.toString())
    }
    prettyStringHook(this, list)
    var name: String = javaClass.simpleName
    if (name.isEmpty()) {
        // anonymous classes
        name = javaClass.name
    }
    return name + list
}

/**
 * Invoked by [toPrettyString] to add additional properties for your custom component.
 * Add additional properties to the `list` provided, e.g. `list.add("icon='$icon'")`.
 *
 * By default does nothing.
 */
var prettyStringHook: (component: Component, list: LinkedList<String>) -> Unit = { _, _ -> }

/**
 * Never dump these attributes in [toPrettyString]. By default these attributes are ignored:
 *
 * * `disabled` - dumped separately as "DISABLED" string.
 * * `id` - dumped as Component.id
 * * `href` - there's special processing for [Anchor._href].
 */
var dontDumpAttributes: MutableSet<String> = mutableSetOf("disabled", "id", "href")
