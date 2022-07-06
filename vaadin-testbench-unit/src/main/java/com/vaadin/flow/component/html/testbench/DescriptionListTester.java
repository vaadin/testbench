/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.html.testbench;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.html.DescriptionList;
import com.vaadin.testbench.unit.Tests;

@Tests(DescriptionList.class)
public class DescriptionListTester extends HtmlClickContainer<DescriptionList> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public DescriptionListTester(DescriptionList component) {
        super(component);
    }

    /**
     * Get all descriptions of DescriptionList.
     *
     * @return the list of descriptions for this description list
     */
    public List<DescriptionList.Description> getDesciptions() {
        return getComponent().getChildren()
                .filter(DescriptionList.Description.class::isInstance)
                .map(DescriptionList.Description.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Get all terms of DescriptionList.
     *
     * @return the list of terms for this description list
     */
    public List<DescriptionList.Term> getTerms() {
        return getComponent().getChildren()
                .filter(DescriptionList.Term.class::isInstance)
                .map(DescriptionList.Term.class::cast)
                .collect(Collectors.toList());
    }
}
