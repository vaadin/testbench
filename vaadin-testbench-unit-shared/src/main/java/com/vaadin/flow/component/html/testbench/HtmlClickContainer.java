/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.testbench.unit.MetaKeys;

public abstract class HtmlClickContainer<T extends HtmlContainer>
        extends HtmlContainerTester<T> implements ClickHandler {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public HtmlClickContainer(T component) {
        super(component);
    }

    @Override
    public void click(int button, MetaKeys metaKeys) {
        ensureComponentIsUsable();
        ComponentUtil.fireEvent(getComponent(),
                new ClickEvent(getComponent(), true, 0, 0, 0, 0, 0, button,
                        metaKeys.isCtrl(), metaKeys.isShift(), metaKeys.isAlt(),
                        metaKeys.isMeta()));
    }
}
