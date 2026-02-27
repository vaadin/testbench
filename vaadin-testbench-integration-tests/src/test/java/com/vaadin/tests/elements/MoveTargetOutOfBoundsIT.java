/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.MoveTargetOutOfBoundsView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractTB6Test;

/**
 * Tests that {@code doubleClick()}, {@code click()} and {@code click(x,y)}
 * handle elements inside Shadow DOM containers with absolutely-positioned rows,
 * reproducing the vaadin-grid scenario from issue #2156 where
 * {@code MoveTargetOutOfBoundsException} is thrown.
 */
public class MoveTargetOutOfBoundsIT extends AbstractTB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return MoveTargetOutOfBoundsView.class;
    }

    @Before
    public void openView() {
        // Use a narrow viewport so that the target cell (positioned far to the
        // right by the shadow DOM layout) extends well beyond the viewport,
        // reproducing the MoveTargetOutOfBoundsException scenario from
        // vaadin-grid.
        getDriver().manage().window().setSize(new Dimension(800, 600));
        openTestURL();
    }

    @Test
    public void targetElementIsOutsideViewportBeforeScroll() {
        TestBenchElement target = $(TestBenchElement.class)
                .id("target-element");
        // The target is slotted into a shadow DOM container where it is
        // positioned far to the right (beyond the viewport width).
        Boolean inViewport = (Boolean) executeScript(
                """
                        try {
                          var elem = arguments[0];
                          var rect = elem.getBoundingClientRect();
                          if (rect.width === 0 && rect.height === 0) return false;
                          var vw = window.innerWidth || document.documentElement.clientWidth;
                          var vh = window.innerHeight || document.documentElement.clientHeight;
                          if (rect.bottom < 0 || rect.top > vh || rect.right < 0 || rect.left > vw) return false;
                          var parent = elem.parentElement;
                          while (parent) {
                            var style = getComputedStyle(parent);
                            var overflow = style.overflow + style.overflowX + style.overflowY;
                            if (/auto|scroll|hidden/.test(overflow)) {
                              var parentRect = parent.getBoundingClientRect();
                              if (rect.bottom <= parentRect.top || rect.top >= parentRect.bottom
                                  || rect.right <= parentRect.left || rect.left >= parentRect.right) return false;
                            }
                            parent = parent.parentElement;
                          }
                          return true;
                        } catch(e) { return true; }
                        """,
                target);
        Assert.assertFalse(
                "Target element should NOT be in the viewport before scrolling",
                inViewport);
    }

    @Test
    public void scrollIntoViewBringsTargetIntoViewport() {
        TestBenchElement target = $(TestBenchElement.class)
                .id("target-element");
        target.scrollIntoView(Map.of("block", "nearest", "inline", "nearest"));
        Long left = (Long) executeScript(
                "return Math.round(arguments[0].getBoundingClientRect().left)",
                target);
        Long viewportWidth = (Long) executeScript("return window.innerWidth");
        Assert.assertTrue(
                "Target element left (" + left
                        + ") should be within viewport width (" + viewportWidth
                        + ") after scrollIntoView",
                left >= 0 && left < viewportWidth);
    }

    @Test
    public void doubleClickOnElementInsideOverflowHiddenContainer() {
        TestBenchElement target = $(TestBenchElement.class)
                .id("target-element");
        target.doubleClick();
        TestBenchElement result = $(TestBenchElement.class)
                .id("dblclick-result");
        Assert.assertEquals("Double-click received", result.getText());
    }

    @Test
    public void clickOnElementInsideOverflowHiddenContainer() {
        TestBenchElement target = $(TestBenchElement.class)
                .id("target-element");
        target.click();
        TestBenchElement result = $(TestBenchElement.class).id("click-result");
        Assert.assertEquals("Click received", result.getText());
    }

    @Test
    public void clickWithCoordinatesOnElementInsideOverflowHiddenContainer() {
        TestBenchElement target = $(TestBenchElement.class)
                .id("target-element");
        target.click(0, 0);
        TestBenchElement result = waitUntil(
                d -> $(TestBenchElement.class).id("click-result"));
        Assert.assertEquals("Click received", result.getText());
    }
}
