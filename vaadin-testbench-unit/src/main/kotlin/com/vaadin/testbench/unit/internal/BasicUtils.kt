/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
@file:Suppress("FunctionName")

package com.vaadin.testbench.unit.internal

import com.vaadin.flow.component.BlurNotifier
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.ComponentUtil
import com.vaadin.flow.component.FocusNotifier
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.HasText
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.dom.DomEvent
import com.vaadin.flow.router.InternalServerError
import com.vaadin.flow.server.VaadinSession
import elemental.json.Json
import elemental.json.JsonObject

/**
 * Allows us to fire any Vaadin event on any Vaadin component.
 * @receiver the component, not null.
 * @param event the event, not null.
 */
fun Component._fireEvent(event: ComponentEvent<*>) {
    ComponentUtil.fireEvent(this, event)
}

/**
 * Fires a DOM event on this component.
 * @param eventType the event type, e.g. "click"
 * @param eventData optional event data, defaults to an empty object.
 */
@JvmOverloads
fun Component._fireDomEvent(eventType: String, eventData: JsonObject = Json.createObject()) {
    element._fireDomEvent(DomEvent(element, eventType, eventData))
}

/**
 * The same as [Component.getId] but without Optional.
 *
 * Workaround for https://github.com/vaadin/flow/issues/664
 */
var Component.id_: String?
    get() = id.orElse(null)
    set(value) {
        setId(value)
    }

/**
 * Checks whether the component is visible (usually [Component.isVisible] but for [Text]
 * the text must be non-empty).
 */
val Component._isVisible: Boolean
    get() = when (this) {
        is Text -> !text.isNullOrBlank()   // workaround for https://github.com/vaadin/flow/issues/3201
        else -> isVisible
    }

/**
 * Returns direct text contents (it doesn't peek into the child elements).
 */
val Component._text: String?
    get() = when (this) {
        is HasText -> text
        else -> null
    }

/**
 * Checks that a component is actually editable by the user:
 * * The component must be effectively visible: it itself must be visible, its parent must be visible and all of its ascendants must be visible.
 *   For the purpose of testing individual components not attached to the [UI], a component may be considered visible even though it's not
 *   currently nested in a [UI].
 * * The component must be effectively enabled: it itself must be enabled, its parent must be enabled and all of its ascendants must be enabled.
 * * If the component is [HasValue], it must not be [HasValue.isReadOnly].
 * @throws IllegalStateException if any of the above doesn't hold.
 */
fun Component.checkEditableByUser() {
    check(isEffectivelyVisible()) { "The ${toPrettyString()} is not effectively visible - either it is hidden, or its ascendant is hidden" }
    val parentNullOrEnabled = !parent.isPresent || parent.get().isEffectivelyEnabled()
    if (parentNullOrEnabled) {
        check(element.isEnabled) { "The ${toPrettyString()} is not enabled" }
    }
    check(isEffectivelyEnabled()) { "The ${toPrettyString()} is nested in a disabled component" }
    if (this is HasValue<*, *>) {
        @Suppress("UNCHECKED_CAST")
        val hasValue = this as HasValue<HasValue.ValueChangeEvent<Any?>, Any?>
        check(!hasValue.isReadOnly) { "The ${toPrettyString()} is read-only" }
    }
    check(isAttached) { " The ${toPrettyString()} is not attached" }
}

/**
 * Fails if the component is editable. See [checkEditableByUser] for more details.
 * @throws AssertionError if the component is editable.
 */
fun Component.expectNotEditableByUser() {
    try {
        checkEditableByUser()
        throw AssertionError("The ${toPrettyString()} is editable")
    } catch (ex: IllegalStateException) {
        // okay
    }
}

internal fun Component.isEffectivelyVisible(): Boolean = _isVisible && (!parent.isPresent || parent.get().isEffectivelyVisible())

/**
 * Computes whether this component and all of its parents are enabled.
 *
 * Recursively checks that all ancestors are also enabled (the "implicitly disabled" effect, see [HasEnabled.isEnabled]
 * javadoc for more details).
 *
 * Also check that the component is not inert due to there being a modal component.
 *
 * @return false if this component or any of its parent is disabled or is inert.
 */
fun Component.isEffectivelyEnabled(): Boolean = element.isEnabled && !element.node.isInert

/**
 * Checks whether this component matches given spec. All rules are matched except the [count] rule. The
 * rules are matched against given component only (not against its children).
 */
fun Component.matches(spec: SearchSpec<Component>.() -> Unit): Boolean =
        SearchSpec(Component::class.java).apply { spec() }.toPredicate().invoke(this)

/**
 * Fires [FocusNotifier.FocusEvent] on the component, but only if it's editable.
 */
fun <T> T._focus() where T : Focusable<*>, T : Component {
    checkEditableByUser()
    _fireEvent(FocusNotifier.FocusEvent<T>(this, true))
}

/**
 * Fires [BlurNotifier.BlurEvent] on the component, but only if it's editable.
 */
fun <T> T._blur() where T : Focusable<*>, T : Component {
    checkEditableByUser()
    _fireEvent(BlurNotifier.BlurEvent<T>(this, true))
}

/**
 * Closes the UI and simulates the end of the request. The [UI.close] is called,
 * but also the session is set to null which fires the detach listeners and makes
 * the UI and all of its components detached.
 */
fun UI._close() {
    close()
    // Mock closing of UI after request handled.
    VaadinSession.getCurrent().removeUI(this)
}

/**
 * Returns child components which were added to this component via
 * [com.vaadin.flow.dom.Element.appendVirtualChild].
 */
fun Component._getVirtualChildren(): List<Component> =
        element.getVirtualChildren().map { it._findComponents() }.flatten()

internal val InternalServerError.errorMessage: String get() = element.text

val _saneFetchLimit: Int get() =
// don't use high value otherwise Vaadin 19+ will calculate negative limit and will pass it to SizeVerifier,
    // failing instantly.
    Int.MAX_VALUE / 1000
