/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.internal.nodefeature.ElementListenerMap;
import com.vaadin.testbench.unit.internal.PrettyPrintTreeKt;

import elemental.json.Json;
import elemental.json.JsonObject;

/**
 * Test wrapper for components with helpful methods for testing a component.
 * <p>
 * More targeted methods for specific components exist in named component
 * wrappers.
 *
 * @param <T>
 *            component type
 */
public class ComponentWrap<T extends Component> {

    private final T component;

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public ComponentWrap(T component) {
        this.component = component;
        if (!isUsable()) {
            LoggerFactory.getLogger("Test wrap").debug(
                    "Wrapped component '{}' that is not interactable",
                    component.getClass().getSimpleName());
        }
    }

    /**
     * Get the wrapped component.
     *
     * @return wrapped component
     */
    public T getComponent() {
        return component;
    }

    /**
     * Validate that component can be interacted with and should be visible in
     * the UI.
     *
     * Subclasses overriding this method should also override
     * {@link #notUsableReasons(Consumer)} to provide additional details to the
     * potential exception thrown by {@link #ensureComponentIsUsable()}.
     *
     * @return {@code true} if component can be interacted with by the user
     * @see #notUsableReasons(Consumer)
     * @see #ensureComponentIsUsable()
     */
    public boolean isUsable() {
        Component component = getComponent();
        return component.getElement().isEnabled() && component.isAttached()
                && isEffectivelyVisible(component)
                && !component.getElement().getNode().isInert();
    }

    private boolean isEffectivelyVisible(Component component) {
        return component.isVisible() && (!component.getParent().isPresent()
                || isEffectivelyVisible(component.getParent().get()));
    }

    /**
     * Set component modality.
     * <p>
     * Automatically generates a client side change to propagate modality.
     *
     * @param modal
     *            {@code true} to make component modal, {@code false} to remove
     *            modality
     */
    public void setModal(boolean modal) {
        UI.getCurrent().setChildComponentModal(component, modal);
    }

    /**
     * Checks that wrapped component is usable, otherwise throws an
     * {@link IllegalStateException} with details on the current state of the
     * component.
     */
    protected final void ensureComponentIsUsable() {
        if (!isUsable()) {
            StringBuilder message = new StringBuilder(
                    PrettyPrintTreeKt.toPrettyString(component)
                            + " is not usable");
            Stream.Builder<String> reasons = Stream.builder();
            notUsableReasons(reasons::add);
            message.append(reasons.build()
                    .collect(Collectors.joining(", ", " because it is ", ".")));
            throw new IllegalStateException(message.toString());
        }
    }

    /**
     * Provides messages explaining why the component is actually not usable.
     *
     * Subclasses overriding {@link #isUsable()} should also override this
     * method to provide additional details to the potential exception throw by
     * {@link #ensureComponentIsUsable()}.
     *
     * @see #isUsable()
     * @see #ensureComponentIsUsable()
     */
    protected void notUsableReasons(Consumer<String> collector) {
        if (!component.getElement().isEnabled()) {
            collector.accept("not enabled");
        }
        if (!component.isAttached()) {
            collector.accept("not attached");
        }
        if (!component.isVisible()) {
            collector.accept("not visible");
        } else if (!isEffectivelyVisible(component)) {
            collector.accept("part of a not visible subtree");
        }
        if (component.getElement().getNode().isInert()) {
            collector.accept("behind a modality curtain");
        }
    }

    /**
     * Check that the component is visible for the user. Else throw an
     * {@link IllegalStateException}
     */
    protected void ensureVisible() {
        if (!getComponent().isVisible() || !getComponent().isAttached()) {
            throw new IllegalStateException(
                    PrettyPrintTreeKt.toPrettyString(component)
                            + " is not visible!");
        }
    }

    /**
     * Simulates a server round-trip, flushing pending component changes.
     */
    protected void roundTrip() {
        BaseUIUnitTest.roundTrip();
    }

    /**
     * Get field with given name in the wrapped component.
     *
     * @param fieldName
     *            field name
     * @return accessible field
     * @throws IllegalArgumentException
     *             if field doesn't exist
     */
    protected Field getField(String fieldName) {
        return getField(getComponent().getClass(), fieldName);
    }

    /**
     * Get field with given name in the given class.
     *
     * @param target
     *            class to get field from
     * @param fieldName
     *            field name
     * @return accessible field
     * @throws IllegalArgumentException
     *             if field doesn't exist
     */
    protected Field getField(Class target, String fieldName) {
        try {
            final Field field = target.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Get method with given name and parameters in the wrapped component.
     *
     * @param methodName
     *            method name
     * @param parameterTypes
     *            parameter types the method has
     * @return accessible method
     */
    protected Method getMethod(String methodName, Class<?>... parameterTypes) {
        return getMethod(getComponent().getClass(), methodName, parameterTypes);
    }

    /**
     * Get method with given name and parameters in the given class.
     *
     * @param target
     *            class to get method from
     * @param methodName
     *            method name
     * @param parameterTypes
     *            parameter types the method has
     * @return accessible method
     */
    protected Method getMethod(Class target, String methodName,
            Class<?>... parameterTypes) {
        try {
            final Method method = target.getDeclaredMethod(methodName,
                    parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fires a DOM event of the given type on the wrapped component.
     *
     * @param eventType
     *            the type of the event, not null.
     */
    protected void fireDomEvent(String eventType) {
        fireDomEvent(eventType, Json.createObject());
    }

    /**
     * Fires a DOM event with the given type and payload on the wrapped
     * component.
     *
     * @param eventType
     *            the type of the event, not null.
     * @param eventData
     *            additional data related to the event, not null
     */
    protected void fireDomEvent(String eventType, JsonObject eventData) {
        DomEvent event = new DomEvent(getComponent().getElement(), eventType,
                eventData);
        fireDomEvent(event);
    }

    /**
     * Fires a DOM event on the wrapped component.
     *
     * @param event
     *            the event that should be fired.
     */
    protected void fireDomEvent(DomEvent event) {
        event.getSource().getNode().getFeature(ElementListenerMap.class)
                .fireEvent(event);
    }
}
