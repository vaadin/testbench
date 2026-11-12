package com.vaadin.testbench.unit.internal

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.ShortcutRegistration
import com.vaadin.flow.dom.DomEventFilter
import com.vaadin.flow.internal.JacksonUtils
import com.vaadin.flow.internal.nodefeature.ElementListenerMap
import com.vaadin.flow.js.JsCall
import tools.jackson.databind.node.ObjectNode

/**
 * A keydown of [key] with [modifiers] held, run through the keydown filters of
 * shortcuts the way the browser's `Vaadin.Flow.shortcut.matchKeydown()` does:
 * the key matches one of the shortcut's keys, every required modifier is held
 * and no forbidden one is.
 *
 * ```
 * SimulatedKeydown(Key.ENTER, setOf(Key.CONTROL)).matches(snippetEntry.call)
 * ```
 */
private class SimulatedKeydown(private val key: Key, modifiers: Set<Key>) :
    ShortcutRegistration.ShortcutFilterJs {
    private val heldModifiers = modifiers.map { it.keys.first() }.toSet()
    private var matched = false

    /**
     * Whether this keydown passes [filter], a call of
     * [ShortcutRegistration.ShortcutFilterJs.matchKeydown].
     */
    fun matches(filter: JsCall): Boolean {
        filter.invokeOn(this)
        return matched
    }

    // JsCall.invokeOn() hands back what this returns, and a DomEventFilter
    // can't carry a match result, so the result goes to `matched` instead.
    override fun matchKeydown(
        requiredModifiers: String,
        forbiddenModifiers: String,
        checkOwnerScope: Boolean,
        ownerSelector: String?,
        preventDefault: Boolean,
        stopPropagation: Boolean,
        resetFocus: Boolean,
        vararg keys: String
    ): DomEventFilter? {
        matched = key.keys.any { it in keys } &&
                requiredModifiers.toModifierNames().all { it in heldModifiers } &&
                forbiddenModifiers.toModifierNames().none { it in heldModifiers }
        return null
    }

    private fun String.toModifierNames(): List<String> = split(' ').filter { it.isNotEmpty() }
}

/**
 * The data the browser would send for a keydown of [key] with [modifiers] held
 * on this component: `event.key`, and the value of each shortcut filter of the
 * component's keydown listeners. A listener whose filter is missing from the
 * data is not notified, so filters other than shortcuts' never pass.
 */
private fun Component.keydownEventData(key: Key, modifiers: Set<Key>): ObjectNode {
    val data = JacksonUtils.createObjectNode()
    data.put("event.key", key.keys.first())
    val keydown = SimulatedKeydown(key, modifiers)
    element.node.getFeature(ElementListenerMap::class.java)
        .getSnippetEntries("keydown")
        .filter { it.filter && it.call.definitionType == ShortcutRegistration.ShortcutFilterJs::class.java }
        .forEach { data.put(it.key, keydown.matches(it.call)) }
    return data
}

/**
 * Fires a shortcut event with given [key] and [modifiers] in the current UI.
 * This will in turn notify all components currently attached to the current UI
 * which subscribed for this exact key combination.
 */
public fun fireShortcut(key: Key, vararg modifiers: Key) {
    // keep the modifiers of type Key[] instead of KeyModifier[], otherwise
    // you won't be able to call those from Kotlin: https://github.com/vaadin/flow/issues/5051
    // and https://youtrack.jetbrains.com/issue/KT-35021
    currentUI._fireShortcut(key, *modifiers)
}

/**
 * Use the global `fireShortcut()` function unless you know what you're doing!
 */
public fun Component._fireShortcut(key: Key, vararg modifiers: Key) {
    // the shortcut registration sets its keydown filter only in [UI.beforeClientResponse]; run the registration code now.
    MockVaadin.clientRoundtrip()

    // this will fire the "keydown" DOM event, which in turn fires KeyDownEvent event,
    // which in turn invokes the ShortcutListener.
    _fireDomEvent("keydown", keydownEventData(key, modifiers.toSet()))
}
