package com.vaadin.testbench.unit.internal

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.ShortcutRegistration
import elemental.json.impl.JreJsonFactory
import elemental.json.impl.JreJsonObject

/**
 * Take a look at `DomEventListenerWrapper.matchesFilter()` to see why this is necessary.
 * If this stuff stops working, place a breakpoint into the [getBoolean]/[hasKey] function,
 * to see what kind of keys you're receiving and whether it matches [filter].
 */
private class MockFilterJsonObject(val key: Key, val modifiers: Set<Key>) : JreJsonObject(JreJsonFactory()) {
    val filter: String
    init {

        // compute the filter
        filter = mgenerateEventKeyFilter.invoke(null, key).toString() + " && " +
                // we call the private method with Hashable keys implementations as the ShortcutRegistration class is
                // called by these objects/classes in regular cases.
                mgenerateEventModifierFilter.invoke(null, modifiers.map { getHashableKey(it)}).toString()

        // populate the json object so that KeyDownEvent can be created from it
        put("event.key", key.keys.first())
    }

    /**
     * Returns a HashableKey instance for the given key modifier.
     * @param keyModifier the key modifier
     * @return the HashableKey instance
     *
      */
    private fun getHashableKey(keyModifier: Key): Any? {
        return chashableKey.newInstance(keyModifier)
    }

    override fun hasKey(key: String): Boolean {
        // the "key" is a JavaScript expression which matches the key pressed.
        // we need to match it against the 'filter'
        if (!key.startsWith("([")) {
            // not a filter
            return super.hasKey(key)
        }
        return matchesFilter(key)
    }

    private fun matchesFilter(key: String): Boolean {
        // the "key" is a JavaScript expression which matches the key pressed.
        // we need to match it against the 'filter'
        val probeFilter = key.removeSuffix(" && (event.stopPropagation() || true)")
            .removeSuffix(" && (event.preventDefault() || true)")
        return probeFilter.startsWith(filter)
    }

    override fun getBoolean(key: String): Boolean {
        // the "key" is a JavaScript expression which matches the key pressed.
        // we need to match it against the 'filter'
        if (!key.startsWith("([")) {
            // not a filter
            return super.getBoolean(key)
        }
        return matchesFilter(key)
    }

    companion object {
        private val mgenerateEventKeyFilter = ShortcutRegistration::class.java.getDeclaredMethod("generateEventKeyFilter", Key::class.java)
        private val mgenerateEventModifierFilter = ShortcutRegistration::class.java.getDeclaredMethod("generateEventModifierFilter", Collection::class.java)
        private val chashableKey = ShortcutRegistration::class.java
            .classLoader
            .loadClass("com.vaadin.flow.component.ShortcutRegistration\$HashableKey")
            .declaredConstructors[0]
        init {
            mgenerateEventKeyFilter.isAccessible = true
            mgenerateEventModifierFilter.isAccessible = true
            chashableKey.isAccessible = true
        }
    }
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
    // all shortcut registration carry a filter with them, in order to filter out
    // pressed keys. All the filters are then transferred to the server-side
    // and compared against DomEventListenerWrapper.filter in
    // DomEventListenerWrapper.matchesFilter()

    // The `matchesFilter()` function is peculiar: it simply checks whether the data
    // contains a boolean value with key 'filter'. We need to fake the json object
    // as if it contained all filters (otherwise `matchesFilter()` would NPE on missing key)
    // and respond true only to the matching filter.
    val data = MockFilterJsonObject(key, modifiers.toSet())

    // the shortcut registration is only updated in [UI.beforeClientResponse]; run the registration code now.
    MockVaadin.clientRoundtrip()

    // this will fire the "keydown" DOM event, which in turn fires KeyDownEvent event,
    // which in turn invokes the ShortcutListener.
    _fireDomEvent("keydown", data)
}
