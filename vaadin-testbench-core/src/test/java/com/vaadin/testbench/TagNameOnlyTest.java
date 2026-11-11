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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.backend.selenium.SeleniumBackend;
import com.vaadin.testbench.backend.selenium.SeleniumElementBackend;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.spi.Backend;
import com.vaadin.testbench.spi.ElementBackend;
import com.vaadin.testbench.testutils.ScriptOnlyBackend;

/**
 * {@code $(String)} takes a tag name: a CSS selector throws on any backend but
 * Selenium, which keeps searching with it until vaadin/testbench#2394 is
 * decided.
 */
public class TagNameOnlyTest {

    public interface JavascriptDriver extends WebDriver, JavascriptExecutor {
    }

    /** Vaadin is never busy, and no search finds anything. */
    private static Object answer(String script) {
        return script.contains("isActive()") ? Boolean.TRUE : List.of();
    }

    private static TestBenchElement elementOn(Backend backend,
            ElementBackend node) {
        TestBenchCommandExecutor executor = new TestBenchCommandExecutor(null,
                null) {
            @Override
            public Backend getBackend() {
                return backend;
            }
        };
        return TestBench.createElement(TestBenchElement.class, node, executor);
    }

    private static TestBenchElement elementOnOtherBackend() {
        return elementOn(new ScriptOnlyBackend() {
            @Override
            public Object executeScript(String script, Object... args) {
                return answer(script);
            }

            @Override
            public Object executeAsyncScript(String script, Object... args) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getBrowserName() {
                return "chromium";
            }
        }, Mockito.mock(ElementBackend.class));
    }

    @Test
    public void tagNamesSearch() {
        TestBenchElement element = elementOnOtherBackend();
        for (String tagName : List.of("*", "div", "vaadin-button", "h1",
                "foreignObject", "x_y")) {
            Assert.assertEquals(tagName, List.of(), element.$(tagName).all());
        }
    }

    @Test
    public void cssSelectorsThrowOnOtherBackends() {
        TestBenchElement element = elementOnOtherBackend();
        for (String selector : List.of(".highcharts-point", "a, b",
                "div > span", "button + vaadin-tooltip", "[part~='x']",
                ":not([hidden])", "div.v-system-error", "my-element.v2", "#id",
                "a b", "1div", "")) {
            try {
                element.$(selector).all();
                Assert.fail("Expected '" + selector + "' to be rejected");
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage(),
                        e.getMessage().contains("is not a tag name"));
            }
        }
    }

    @Test
    public void cssSelectorsStillSearchOnSelenium() {
        JavascriptDriver driver = Mockito.mock(JavascriptDriver.class,
                invocation -> invocation.getMethod().getName().equals(
                        "executeScript") ? answer(invocation.getArgument(0))
                                : null);
        SeleniumBackend backend = new SeleniumBackend(driver);
        TestBenchElement element = elementOn(backend,
                new SeleniumElementBackend(backend,
                        Mockito.mock(WebElement.class)));
        Assert.assertEquals(List.of(), element.$("a, b").all());
    }
}
