/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.testbenchapi.components;

import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.AbstractComponentElement;

/**
 * Validates {@link AbstractComponentElement#getHTML()}.
 */
public class ComponentGetHTML {

    @SuppressWarnings("unchecked")
    @Test
    public void testTestBenchDriverActsAsProxy() {

        String innerHtmlContent = "inner html content";

        WebElement elementMock = createMock(WebElement.class);
        expect(elementMock.getAttribute(contains("innerHTML"))).andReturn(
                innerHtmlContent);

        AbstractComponentElement abstractComponentMock = createMockBuilder(
                AbstractComponentElement.class).addMockedMethod(
                "getWrappedElement").createMock();

        expect(abstractComponentMock.getWrappedElement())
                .andReturn(elementMock);

        replay(abstractComponentMock, elementMock);
        Assert.assertEquals(innerHtmlContent, abstractComponentMock.getHTML());
        verify(abstractComponentMock, elementMock);
    }
}
