/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.flow.component.html;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.browserless.Tests;
import com.vaadin.flow.component.html.DescriptionList;

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
