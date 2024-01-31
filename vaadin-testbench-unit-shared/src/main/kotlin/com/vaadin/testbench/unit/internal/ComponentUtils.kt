/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.internal

import java.lang.reflect.Method
import kotlin.streams.toList
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.ComponentUtil
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.HasElement
import com.vaadin.flow.component.HasOrderedComponents
import com.vaadin.flow.component.HasPlaceholder
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.HasText
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.checkbox.CheckboxGroup
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Input
import com.vaadin.flow.component.listbox.ListBoxBase
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.sidenav.SideNav
import com.vaadin.flow.component.sidenav.SideNavItem
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.HasDataProvider
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.dom.DomEventListener
import com.vaadin.flow.dom.DomListenerRegistration
import com.vaadin.flow.router.Location
import com.vaadin.testbench.unit.component.dataProvider

/**
 * Fires given event on the component.
 */
fun Component.fireEvent(event: ComponentEvent<*>) {
    ComponentUtil.fireEvent(this, event)
}

/**
 * Adds [com.vaadin.flow.component.button.Button.click] functionality to all [ClickNotifier]s. This function directly calls
 * all click listeners, thus it avoids the roundtrip to client and back. It even works with browserless testing.
 * @param fromClient see [ComponentEvent.isFromClient], defaults to true.
 * @param button see [ClickEvent.getButton], defaults to 0.
 * @param clickCount see [ClickEvent.getClickCount], defaults to 1.
 */
fun <T: ClickNotifier<*>> T.serverClick(
    fromClient: Boolean = true,
    button: Int = 0,
    clickCount: Int = 1,
    shiftKey: Boolean = false,
    ctrlKey: Boolean = false,
    altKey: Boolean = false,
    metaKey: Boolean = false
) {
    (this as Component).fireEvent(ClickEvent<Component>(this,
        fromClient, -1, -1, -1, -1, clickCount, button, ctrlKey, shiftKey, altKey, metaKey))
}

/**
 * Sets the alignment of the text in the component. One of `center`, `left`, `right`, `justify`.
 */
var Component.textAlign: String?
    get() = element.style.get("textAlign")
    set(value) { element.style.set("textAlign", value) }

/**
 * Sets or removes the `title` attribute on component's element.
 */
var Component.tooltip: String?
    get() = element.getAttribute("title")
    set(value) { element.setOrRemoveAttribute("title", value) }

/**
 * Adds the right-click (context-menu) [listener] to the component. Also causes the right-click browser
 * menu not to be shown on this component (see [preventDefault]).
 */
fun Component.addContextMenuListener(listener: DomEventListener): DomListenerRegistration =
        element.addEventListener("contextmenu", listener)
                .preventDefault()

/**
 * Makes the client-side listener call [Event.preventDefault()](https://developer.mozilla.org/en-US/docs/Web/API/Event/preventDefault)
 * on the event.
 *
 * @return this
 */
fun DomListenerRegistration.preventDefault(): DomListenerRegistration = addEventData("event.preventDefault()")

/**
 * Removes the component from its parent. Does nothing if the component is not attached to a parent.
 */
fun Component.removeFromParent() {
    (parent.orElse(null) as? HasComponents)?.remove(this)
}

/**
 * Finds component's parent, parent's parent (etc) which satisfies given [predicate].
 * Returns null if there is no such parent.
 */
fun Component.findAncestor(predicate: (Component) -> Boolean): Component? =
        findAncestorOrSelf { it != this && predicate(it) }

/**
 * Finds component, component's parent, parent's parent (etc) which satisfies given [predicate].
 * Returns null if no component on the ancestor-or-self axis satisfies.
 */
tailrec fun Component.findAncestorOrSelf(predicate: (Component) -> Boolean): Component? {
    if (predicate(this)) {
        return this
    }
    val p: Component = parent.orElse(null) ?: return null
    return p.findAncestorOrSelf(predicate)
}

/**
 * Checks if this component is nested in [potentialAncestor].
 */
fun Component.isNestedIn(potentialAncestor: Component): Boolean =
        findAncestor { it == potentialAncestor } != null

/**
 * Checks whether this component is currently attached to a [UI].
 *
 * Returns true for attached components even if the UI itself is closed.
 */
fun Component.isAttached(): Boolean {
    // see https://github.com/vaadin/flow/issues/7911
    return element.node.isAttached
}

/**
 * Returns the data provider currently set to this Component.
 *
 * Works both with Vaadin 16 and Vaadin 17: Vaadin 17 components no longer implement HasItems.
 */
val Component.dataProvider: DataProvider<*, *>? get() = when (this) {
    // until https://github.com/vaadin/flow/issues/6296 is resolved
    is HasDataProvider<*> -> this.dataProvider
    is Grid<*> -> this.dataProvider
    is Select<*> -> this.dataProvider
    is ListBoxBase<*, *, *> -> _Grid_getDataProvider.invoke(this) as DataProvider<*, *>
    is RadioButtonGroup<*> -> _RadioButtonGroup_getDataProvider.invoke(this) as DataProvider<*, *>
    is CheckboxGroup<*> -> _CheckboxGroup_getDataProvider.invoke(this) as DataProvider<*, *>
    is ComboBox<*> -> this.dataProvider
    else -> null
}

/**
 * Inserts this component as a child, right before an [existing] one.
 *
 * In case the specified component has already been added to another parent,
 * it will be removed from there and added to this one.
 */
fun HasOrderedComponents.insertBefore(newComponent: Component, existing: Component) {
    val parent: Component = requireNotNull(existing.parent.orElse(null)) { "$existing has no parent" }
    require(parent == this) { "$existing is not nested in $this" }
    addComponentAtIndex(indexOf(existing), newComponent)
}

/**
 * Return the location of the currently shown view. The function will report the current (old)
 * view in [com.vaadin.flow.router.BeforeLeaveEvent] and [com.vaadin.flow.router.BeforeEnterEvent].
 */
val UI.currentViewLocation: Location get() = internals.activeViewLocation

/**
 * True when the component has any children. Alias for [hasChildren].
 *
 * Deprecated: poorly named. A `form.isNotEmpty` may be ambiguous - it may refer to
 * whether the form has any children, or whether the form value is not empty, or something else.
 */
//@Deprecated("use hasChildren", replaceWith = ReplaceWith("hasChildren"))
//val HasComponents.isNotEmpty: Boolean get() = hasChildren

/**
 * True when the component has any children.
 */
val HasComponents.hasChildren: Boolean get() = (this as Component).children.findFirst().isPresent

/**
 * True when the component has no children.
 *
 * Deprecated: poorly named. A `form.isEmpty` may be ambiguous - it may refer to
 * whether the form has any children, or whether the form value is empty, or something else.
 */
//@Deprecated("use !hasChildren")
//val HasComponents.isEmpty: Boolean get() = !hasChildren

/**
 * Splits [classNames] by whitespaces to obtain individual class names, then
 * calls [HasStyle.addClassName] on each class name. Does nothing if the string
 * is blank.
 */
fun HasStyle.addClassNames2(classNames: String) {
    // workaround for https://github.com/vaadin/flow/issues/11709
    classNames.splitByWhitespaces().forEach { addClassName(it) }
}

/**
 * Splits [classNames] by whitespaces to obtain individual class names, then
 * calls [addClassNames2] on each class name. Does nothing if the string
 * is blank.
 */
fun HasStyle.addClassNames2(vararg classNames: String) {
    // workaround for https://github.com/vaadin/flow/issues/11709
    classNames.forEach { addClassNames2(it) }
}

/**
 * Splits [classNames] by whitespaces to obtain individual class names, then
 * calls [HasStyle.removeClassName] on each class name. Does nothing if the string
 * is blank.
 */
fun HasStyle.removeClassNames2(classNames: String) {
    // workaround for https://github.com/vaadin/flow/issues/11709
    classNames.splitByWhitespaces().forEach { removeClassName(it) }
}

/**
 * Splits [classNames] by whitespaces to obtain individual class names, then
 * calls [removeClassNames2] on each class name. Does nothing if the string
 * is blank.
 */
fun HasStyle.removeClassNames2(vararg classNames: String) {
    // workaround for https://github.com/vaadin/flow/issues/11709
    classNames.forEach { removeClassNames2(it) }
}

/**
 * Splits [classNames] by whitespaces to obtain individual class names, then
 * clears the class names and calls [addClassNames2] on each class name. Does nothing if the string
 * is blank.
 */
fun HasStyle.setClassNames2(classNames: String) {
    // workaround for https://github.com/vaadin/flow/issues/11709
    style.clear()
    addClassNames2(classNames)
}

/**
 * Splits [classNames] by whitespaces to obtain individual class names, then
 * clears the class names and calls [addClassNames2] on each class name. Does nothing if the string
 * is blank.
 */
fun HasStyle.setClassNames2(vararg classNames: String) {
    // workaround for https://github.com/vaadin/flow/issues/11709
    style.clear()
    addClassNames2(*classNames)
}

/**
 * A component placeholder, usually shown when there's no value selected.
 * Not all components support a placeholder; those that don't will return null.
 */
var Component.placeholder: String?
    // modify when this is fixed: https://github.com/vaadin/flow/issues/4068
    get() = when (this) {
        is TextField -> placeholder
        is TextArea -> placeholder
        is PasswordField -> placeholder
        is ComboBox<*> -> this.placeholder  // https://youtrack.jetbrains.com/issue/KT-24275
        is DatePicker -> placeholder
        is HasPlaceholder -> placeholder
        else -> null
    }
    set(value) {
        when (this) {
            is TextField -> placeholder = value
            is TextArea -> placeholder = value
            is PasswordField -> placeholder = value
            is ComboBox<*> -> this.placeholder = value
            is DatePicker -> placeholder = value
            is HasPlaceholder -> placeholder = value
            else -> throw IllegalStateException("${javaClass.simpleName} doesn't support setting placeholder")
        }
    }

/**
 * Concatenates texts from all elements placed in the `label` slot. This effectively
 * returns whatever was provided in the String label via [FormLayout.addFormItem].
 */
val FormLayout.FormItem.label: String get() {
    val captions: List<Component> = children.toList().filter { it.element.getAttribute("slot") == "label" }
    return captions.joinToString("") { (it as? HasText)?.text ?: "" }
}

/**
 * The `HasLabel` interface has been introduced in Vaadin 21 but is missing in Vaadin 14.
 * Use reflection.
 */
private val _HasLabel: Class<*>? = findClass("com.vaadin.flow.component.HasLabel")
private val _HasLabel_getLabel: Method? = _HasLabel?.getDeclaredMethod("getLabel")
private val _HasLabel_setLabel: Method? = _HasLabel?.getDeclaredMethod("setLabel", String::class.java)

/**
 * Determines the component's `label` (usually it's the HTML element's `label` property, but it's [Checkbox.getLabel] for checkbox).
 * Intended to be used for fields such as [TextField].

 * *For `FormItem`:* Concatenates texts from all elements placed in the `label` slot. This effectively
 * returns whatever was provided in the String label via [FormLayout.addFormItem].
 *
 * [Button.caption] is displayed directly on the component
 * while label is displayed next to the component in a layout (e.g. a [TextField] nested in a form layout).
 *
 * Vote for [issue #3241](https://github.com/vaadin/flow/issues/3241).
 *
 * **WARNING:** the label is displayed by the component itself, rather than by the parent layout.
 * If a component doesn't contain necessary machinery
 * to display a label, setting this property will have no visual effect.
 * For example, setting a label to a [FormLayout]
 * nested within a `VerticalLayout`
 * will show nothing since [FormLayout] doesn't display a label itself.
 * See [LabelWrapper] for a list of possible solutions.
 */
var Component.label: String
    get() = when {
        _HasLabel != null && _HasLabel.isInstance(this) -> _HasLabel_getLabel!!.invoke(this) as String? ?: ""
        this is Checkbox -> label ?: ""
        this is FormLayout.FormItem -> this.label
        this is SideNav -> label ?: ""
        this is SideNavItem -> label ?: ""
        else -> element.getProperty("label") ?: ""
    }
    set(value) {
        when {
            _HasLabel != null && _HasLabel.isInstance(this) -> _HasLabel_setLabel!!.invoke(this, value)
            this is Checkbox -> label = value
            this is FormLayout.FormItem -> throw IllegalArgumentException("Setting the caption of FormItem is currently unsupported")
            this is SideNav -> label = value
            this is SideNavItem -> label = value
            else -> element.setProperty("label", value.ifBlank { null })
        }
    }

/**
 * The Component's caption: [Button.getText] for [Button], [label] for fields such as [TextField].
 *
 * Caption is generally displayed directly on the component (e.g. the Button text),
 * while [label] is displayed next to the component in a layout (e.g. a [TextField] nested in a form layout).
 *
 * **Deprecated:** this property was intended to unify captions and labels, but only managed to
 * create confusion between the two concepts. Also, there's only a [Button] which
 * has the notion of a caption. Will be removed with no replacement.
 */
@Deprecated("don't use")
var Component.caption: String
    get() = when (this) {
        is Button -> caption
        else -> label
    }
    set(value) {
        when (this) {
            is Button -> caption = value
            else -> label = value
        }
    }

/**
 * The Button's caption. Alias for [Button.getText].
 *
 * Caption is generally displayed directly on the component (e.g. the Button text),
 * while [label] is displayed next to the component in a layout (e.g. a [TextField] nested in a form layout).
 */
var Button.caption: String
    get() = text
    set(value) {
        text = value
    }

internal fun HasElement.getChildComponentInSlot(slotName: String): Component? =
    element.getChildrenInSlot(slotName).firstOrNull()?.component?.get()

internal fun HasElement.setChildComponentToSlot(slotName: String, component: Component?) {
    element.clearSlot(slotName)
    if (component != null) {
        component.element.setAttribute("slot", slotName)
        element.appendChild(component.element)
    }
}

internal fun isPolymerTemplate(component: Component): Boolean {
    return polymerTemplateClass != null
            && polymerTemplateClass.isAssignableFrom(component.javaClass);
}

private val _Grid_getDataProvider: Method =
    Grid::class.java.getDeclaredMethod("getDataProvider").apply { isAccessible = true }
private val _CheckboxGroup_getDataProvider: Method =
    CheckboxGroup::class.java.getDeclaredMethod("getDataProvider").apply { isAccessible = true }
private val _RadioButtonGroup_getDataProvider: Method =
    RadioButtonGroup::class.java.getDeclaredMethod("getDataProvider").apply { isAccessible = true }
