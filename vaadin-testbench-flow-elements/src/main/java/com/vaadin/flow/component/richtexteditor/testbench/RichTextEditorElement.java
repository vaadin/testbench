/**
 * Copyright 2000-2026 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>} for the full
 * license.
 */
package com.vaadin.flow.component.richtexteditor.testbench;

import java.util.List;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-rich-text-editor")
public class RichTextEditorElement extends TestBenchElement {

    public TestBenchElement getEditor() {
        return $("div").withAttributeContainingWord("class", "ql-editor")
                .first();
    }

    public List getTitles() {
        TestBenchElement toolbar = $("div")
                .withAttributeContainingWord("part", "toolbar").first();
        // A sibling combinator has no $ form; the toolbar has no shadow root,
        // so its light DOM is all $ searched
        List<TestBenchElement> buttonTooltips = (List<TestBenchElement>) executeScript(
                "return Array.from(arguments[0].querySelectorAll('button + vaadin-tooltip'))",
                toolbar);
        return buttonTooltips.stream()
                .map(tooltip -> tooltip.getProperty("text")).toList();
    }

}
