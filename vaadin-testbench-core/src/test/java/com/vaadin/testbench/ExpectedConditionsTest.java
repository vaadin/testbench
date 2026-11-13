/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.spi.BrowserBackend;
import com.vaadin.testbench.spi.ElementBackend;

/**
 * Our {@code ExpectedConditions} hold, fail and describe themselves as
 * Selenium's do, and the element shims hand back what they were given.
 */
public class ExpectedConditionsTest {

    private final BrowserDriver driver = Mockito.mock(BrowserDriver.class);

    private static TestBenchElement element(boolean displayed,
            boolean enabled) {
        TestBenchElement element = Mockito.mock(TestBenchElement.class);
        Mockito.when(element.isDisplayed()).thenReturn(displayed);
        Mockito.when(element.isEnabled()).thenReturn(enabled);
        return element;
    }

    private static TestBenchElement stale() {
        TestBenchElement element = Mockito.mock(TestBenchElement.class);
        StaleElementReferenceException gone = new StaleElementReferenceException(
                "gone\nmore");
        Mockito.when(element.isDisplayed()).thenThrow(gone);
        Mockito.when(element.isEnabled()).thenThrow(gone);
        return element;
    }

    @Test
    public void elementToBeClickableSaysWhyNot() {
        ExpectedCondition<TestBenchElement> hidden = ExpectedConditions
                .elementToBeClickable(element(false, true));
        Assert.assertNull(hidden.apply(driver));
        Assert.assertEquals(
                "element to be clickable, but the element was not visible.",
                hidden.toString());

        ExpectedCondition<TestBenchElement> disabled = ExpectedConditions
                .elementToBeClickable(element(true, false));
        Assert.assertNull(disabled.apply(driver));
        Assert.assertTrue(disabled.toString().contains("was not enabled"));

        TestBenchElement ready = element(true, true);
        Assert.assertSame(ready,
                ExpectedConditions.elementToBeClickable(ready).apply(driver));

        ExpectedCondition<TestBenchElement> gone = ExpectedConditions
                .elementToBeClickable(stale());
        Assert.assertNull(gone.apply(driver));
        Assert.assertEquals("element to be clickable, but the element was not "
                + "found: com.vaadin.testbench.StaleElementReferenceException:"
                + " gone.", gone.toString());
    }

    @Test
    public void aStaleElementIsInvisibleAndStale() {
        Assert.assertTrue(
                ExpectedConditions.invisibilityOf(stale()).apply(driver));
        Assert.assertFalse(ExpectedConditions
                .invisibilityOf(element(true, true)).apply(driver));
        Assert.assertTrue(
                ExpectedConditions.stalenessOf(stale()).apply(driver));
        Assert.assertFalse(ExpectedConditions.stalenessOf(element(true, true))
                .apply(driver));
    }

    @Test
    public void attributeToBeFallsBackToTheCssValue() {
        TestBenchElement element = Mockito.mock(TestBenchElement.class);
        Mockito.when(element.getCssValue("color")).thenReturn("red");
        Assert.assertTrue(ExpectedConditions
                .attributeToBe(element, "color", "red").apply(driver));
        ExpectedCondition<Boolean> contains = ExpectedConditions
                .attributeContains(element, "color", "blue");
        Assert.assertFalse(contains.apply(driver));
        Assert.assertEquals(
                "attribute or CSS value \"color\" to contain "
                        + "\"blue\". Current value: \"red\".",
                contains.toString());
    }

    @Test
    public void notHoldsWhileTheElementIsMissing() {
        By missing = By.id("missing");
        Mockito.when(driver.findElement(missing))
                .thenThrow(new NoSuchElementException("missing"));
        Assert.assertTrue(ExpectedConditions
                .not(ExpectedConditions.presenceOfElementLocated(missing))
                .apply(driver));
    }

    @Test
    public void numberOfElementsHoldsAsTheElements() {
        By rows = By.tagName("tr");
        List<TestBenchElement> two = List.of(element(true, true),
                element(true, true));
        Mockito.when(driver.findElements(rows)).thenReturn(two);
        Assert.assertSame(two,
                ExpectedConditions.numberOfElementsToBe(rows, 2).apply(driver));
        ExpectedCondition<List<TestBenchElement>> three = ExpectedConditions
                .numberOfElementsToBe(rows, 3);
        Assert.assertNull(three.apply(driver));
        Assert.assertEquals("number of elements found by By.tagName: tr to be"
                + " 3. Found: 2 element(s).", three.toString());
    }

    /** An element's waitUntil takes a condition too, on its own browser. */
    @Test
    public void aConditionIsAnElementsWaitCondition() {
        BrowserBackend backend = Mockito.mock(BrowserBackend.class);
        Mockito.when(backend.getCurrentUrl())
                .thenReturn("http://localhost/side-nav-test-target-view");
        TestBenchCommandExecutor executor = Mockito
                .mock(TestBenchCommandExecutor.class);
        Mockito.when(executor.getBackend()).thenReturn(backend);
        WaitCondition<Boolean> condition = ExpectedConditions
                .urlContains("side-nav-test-target-view");
        Assert.assertEquals(Boolean.TRUE, condition.apply(executor));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void theElementShimsHandBackWhatTheyGet() {
        TestBenchCommandExecutor executor = Mockito
                .mock(TestBenchCommandExecutor.class);
        TestBenchElement element = TestBench.createElement(
                TestBenchElement.class, Mockito.mock(ElementBackend.class),
                executor);
        Assert.assertSame(element, element.getWrappedElement());
        Assert.assertSame(element,
                TestBenchElement.wrapElement(element, executor));
        Assert.assertEquals(List.of(element),
                TestBenchElement.wrapElements(List.of(element), executor));
        Assert.assertSame(executor, element.getDriver().getCommandExecutor());
    }
}
