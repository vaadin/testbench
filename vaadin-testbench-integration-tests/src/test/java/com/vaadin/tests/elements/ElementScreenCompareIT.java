/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testUI.ElementQueryUI;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;

public class ElementScreenCompareIT extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        // intentionally overriding this method instead of getDeploymentPath()
        // in order to test different ways of determining the deployment path
        return ElementQueryUI.class;
    }

    @Test
    public void elementCompareScreen() throws Exception {
        openTestURL();
        TestBenchElement button4 = (TestBenchElement) findElements(
                By.className("v-button")).get(4);

        compareScreen(button4, "button4");
        TestBenchElement layout = (TestBenchElement) button4
                .findElement(By.xpath("../.."));
        compareScreen(layout, "layout");
    }

    private void compareScreen(TestBenchElement element, String referenceId)
            throws IOException {
        assertTrue(errorMessage(element, referenceId),
                element.compareScreen(referenceId));
    }

    private String errorMessage(TestBenchElement element,
            String referenceId) {
        ReferenceNameGenerator referenceNameGenerator = element
                .getCommandExecutor().getReferenceNameGenerator();
        return String.format(
                "Screenshot comparison failed. Comparison file: "
                        + "%s.png (platform and number may vary)",
                referenceNameGenerator.generateName(referenceId,
                        getDesiredCapabilities()));
    }

}
