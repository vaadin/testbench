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

import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;

/**
 * Test wrapper for components with helpful methods for testing a component.
 * <p>
 * More targeted methods for specific components exist in named component
 * wrappers.
 *
 * @param <T>
 *         component type
 */
public class ComponentWrap<T extends Component> {

    private final T component;

    /**
     * Wrap given component for testing.
     *
     * @param component
     *         target component
     */
    public ComponentWrap(T component) {
        this.component = component;
        if (!isUsable()) {
            LoggerFactory.getLogger("Test wrap")
                    .debug("Wrapped component '{}' that is not interactable",
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
     * @return {@code true} if component can be interacted with by the user
     */
    public boolean isUsable() {
        Component component = getComponent();
        return component.getElement().isEnabled() && component.isAttached()
                && isEffectivelyVisible(component) && !component.getElement()
                .getNode().isInert();
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
     *         {@code true} to make component modal, {@code false} to remove
     *         modality
     */
    public void setModal(boolean modal) {
        UI.getCurrent().setChildComponentModal(component, modal);

        UI.getCurrent().getInternals().getStateTree()
                .collectChanges(nodeChange -> {
                });
    }
}
