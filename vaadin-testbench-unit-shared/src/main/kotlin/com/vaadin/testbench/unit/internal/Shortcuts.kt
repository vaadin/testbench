package com.vaadin.testbench.unit.internal

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Key

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
    // the shortcut registration is only updated in [UI.beforeClientResponse]; run the registration code now.
    MockVaadin.clientRoundtrip()

    // Every shortcut listens for "keydown" with a filter that matches its key
    // and modifiers. Dispatching the keydown runs the filters the way the
    // browser does, so that only the listeners of matching shortcuts are
    // notified with a KeyDownEvent, which in turn invokes the ShortcutListener.
    ClientDomEvent.dispatchKeyDown(element, key, modifiers.toList()).fire()
}
