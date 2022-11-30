/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.accordion;

import org.jetbrains.annotations.Nullable;

import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

@Tests(Accordion.class)
public class AccordionTester<T extends Accordion> extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public AccordionTester(T component) {
        super(component);
    }

    /**
     * Open the accordion with the given summary.
     *
     * @param summary
     *            summary of accordion panel
     * @throws IllegalArgumentException
     *             if no dropdown panel found for summary
     */
    public void openDetails(String summary) {
        ensureComponentIsUsable();
        final AccordionPanel childPanel = getPanelBySummary(summary);
        if (childPanel == null) {
            throw new IllegalArgumentException(
                    "No dropdown found for '" + summary + "'");
        }
        getComponent().open(childPanel);
        roundTrip();
    }

    /**
     * Check if accordion with the summary is open.
     *
     * @param summary
     *            summary of accordion panel
     * @return {@code true} if panel is open
     */
    public boolean isOpen(String summary) {
        ensureVisible();
        final AccordionPanel childPanel = getPanelBySummary(summary);
        return isOpen(childPanel);
    }

    /**
     * Get the panel with the summary. Throws if panel is not open.
     *
     * @param summary
     *            summary of accordion panel
     * @return {@code AccordionPanel} for the given summary
     */
    public AccordionPanel getPanel(String summary) {
        ensureVisible();
        final AccordionPanel panel = getPanelBySummary(summary);
        if (!isOpen(panel)) {
            throw new IllegalStateException(
                    "Requested panel is not open to the user");
        }
        return panel;
    }

    /**
     * Check if a panel for summary exists in accordion.
     *
     * @param summary
     *            summary of accordion panel
     * @return {@code true} if panel exists
     */
    public boolean hasPanel(String summary) {
        return getPanelBySummary(summary) != null;
    }

    private boolean isOpen(AccordionPanel childPanel) {
        if (getComponent().getOpenedPanel().isPresent()) {
            return getComponent().getOpenedPanel().get().equals(childPanel);
        }
        return false;
    }

    @Nullable
    private AccordionPanel getPanelBySummary(String summary) {
        final AccordionPanel childPanel = getComponent().getChildren()
                .filter(child -> child instanceof AccordionPanel)
                .map(AccordionPanel.class::cast)
                .filter(panel -> panel.getSummaryText().equals(summary))
                .findFirst().orElse(null);
        return childPanel;
    }
}
