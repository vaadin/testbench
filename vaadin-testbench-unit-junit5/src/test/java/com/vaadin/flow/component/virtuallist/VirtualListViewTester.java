/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.virtuallist;

import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.testbench.DivTester;
import com.vaadin.flow.component.html.testbench.NativeButtonTester;
import com.vaadin.flow.component.html.testbench.SpanTester;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.TesterWrappers;
import com.vaadin.testbench.unit.Tests;

@Tests(VirtualListView.class)
public class VirtualListViewTester extends ComponentTester<VirtualListView>
        implements TesterWrappers {

    public VirtualListViewTester(VirtualListView virtualListView) {
        super(virtualListView);
        ensureComponentIsUsable();
    }

    /**
     * Get the tester for the value provider virtual list.
     *
     * @return the tester for the value provider virtual list.
     */
    @SuppressWarnings("unchecked")
    public VirtualListTester<VirtualList<User>, User> $valueProviderVirtualList() {
        return test(find(VirtualList.class)
                .atIndex(1));
    }

    /**
     * Get the tester for the component renderer virtual list.
     *
     * @return the tester for the component renderer virtual list.
     */
    @SuppressWarnings("unchecked")
    public VirtualListTester<VirtualList<User>, User> $componentRendererVirtualList() {
        return test(find(VirtualList.class)
                .atIndex(2));
    }

    /**
     * Get the tester for the callback lit renderer virtual list.
     *
     * @return the tester for the callback lit renderer virtual list.
     */
    @SuppressWarnings("unchecked")
    public VirtualListTester<VirtualList<User>, User> $callbackLitRendererVirtualList() {
        return test(find(VirtualList.class)
                .atIndex(3));
    }

    /**
     * Get the tester for the first name span of the given DivTester.
     *
     * @param divTester the DivTester of the item
     * @return the Span containing the first name
     */
    public SpanTester $firstNameSpanFor(DivTester divTester) {
        return test(divTester.find(Span.class)
                .withId("first-name")
                .single());
    }

    /**
     * Get the tester for the last name span of the given DivTester.
     *
     * @param divTester the DivTester of the item
     * @return the Span containing the last name
     */
    public SpanTester $lastNameSpanFor(DivTester divTester) {
        return test(divTester.find(Span.class)
                .withId("last-name")
                .single());
    }

    /**
     * Get the tester for the active text span of the given DivTester.
     *
     * @param divTester the DivTester of the item
     * @return the Span containing the active text
     */
    public SpanTester $activeSpanFor(DivTester divTester) {
        return test(divTester.find(Span.class)
                .withId("active")
                .single());
    }

    /**
     * Get the tester for the first name span of the given DivTester.
     *
     * @param divTester the DivTester of the item
     * @return the Span containing the first name
     */
    public NativeButtonTester $activeToggleButtonFor(DivTester divTester) {
        return test(divTester.find(NativeButton.class)
                .withCaption("Toggle")
                .single());
    }
}
