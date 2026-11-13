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

import org.jspecify.annotations.Nullable;

/**
 * Selenium's {@code ExpectedConditions} on TestBench's types, the ones tests
 * use, so that a wait using them ports by changing the import:
 *
 * <pre>{@code
 * waitUntil(ExpectedConditions.elementToBeClickable(button));
 * }</pre>
 *
 * Each checks what Selenium's checks, catches what it catches, and describes
 * itself as it does in a timeout's message.
 */
public final class ExpectedConditions {

    private ExpectedConditions() {
    }

    /** Found, displayed and enabled; holds as the element. */
    public static ExpectedCondition<TestBenchElement> elementToBeClickable(
            By locator) {
        return new ExpectedCondition<>() {
            private @Nullable String message;

            @Override
            public @Nullable TestBenchElement apply(BrowserDriver driver) {
                message = null;
                try {
                    TestBenchElement element = driver.findElement(locator);
                    message = whyNotClickable(element);
                    return message == null ? element : null;
                } catch (NoSuchElementException
                        | StaleElementReferenceException e) {
                    message = "was not found: " + shortDescription(e);
                    return null;
                }
            }

            @Override
            public String toString() {
                return String.format(
                        "element found by %s to be clickable, but the element %s.",
                        locator, message);
            }
        };
    }

    /** Displayed and enabled; holds as the element. */
    public static ExpectedCondition<TestBenchElement> elementToBeClickable(
            TestBenchElement element) {
        return new ExpectedCondition<>() {
            private @Nullable String message;

            @Override
            public @Nullable TestBenchElement apply(BrowserDriver driver) {
                message = null;
                try {
                    message = whyNotClickable(element);
                    return message == null ? element : null;
                } catch (NoSuchElementException
                        | StaleElementReferenceException e) {
                    message = "was not found: " + shortDescription(e);
                    return null;
                }
            }

            @Override
            public String toString() {
                return String.format(
                        "element to be clickable, but the element %s.",
                        message);
            }
        };
    }

    private static @Nullable String whyNotClickable(TestBenchElement element) {
        if (!element.isDisplayed()) {
            return "was not visible";
        }
        return element.isEnabled() ? null : "was not enabled";
    }

    /** Found; holds as the element. */
    public static ExpectedCondition<TestBenchElement> presenceOfElementLocated(
            By locator) {
        return new ExpectedCondition<>() {
            @Override
            public @Nullable TestBenchElement apply(BrowserDriver driver) {
                try {
                    return driver.findElement(locator);
                } catch (NoSuchElementException notFound) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "presence of element found by " + locator;
            }
        };
    }

    /** Displayed; holds as the element. */
    public static ExpectedCondition<TestBenchElement> visibilityOf(
            TestBenchElement element) {
        return new ExpectedCondition<>() {
            @Override
            public @Nullable TestBenchElement apply(BrowserDriver driver) {
                return element.isDisplayed() ? element : null;
            }

            @Override
            public String toString() {
                return "visibility of " + element;
            }
        };
    }

    /** Found and displayed; holds as the element. */
    public static ExpectedCondition<TestBenchElement> visibilityOfElementLocated(
            By locator) {
        return new ExpectedCondition<>() {
            private @Nullable RuntimeException error;

            @Override
            public @Nullable TestBenchElement apply(BrowserDriver driver) {
                error = null;
                try {
                    TestBenchElement element = driver.findElement(locator);
                    return element.isDisplayed() ? element : null;
                } catch (StaleElementReferenceException
                        | NoSuchElementException gone) {
                    error = gone;
                    return null;
                }
            }

            @Override
            public String toString() {
                if (error != null) {
                    return String.format(
                            "visibility of element found by %s, but... %s.",
                            locator, shortDescription(error));
                }
                return String.format("visibility of element found by %s",
                        locator);
            }
        };
    }

    /** Not displayed, or gone. */
    public static ExpectedCondition<Boolean> invisibilityOf(
            TestBenchElement element) {
        return new ExpectedCondition<>() {
            @Override
            public Boolean apply(BrowserDriver driver) {
                try {
                    return !element.isDisplayed();
                } catch (StaleElementReferenceException
                        | NoSuchElementException gone) {
                    return true;
                }
            }

            @Override
            public String toString() {
                return String.format("element %s to become invisible", element);
            }
        };
    }

    /** The current URL contains {@code fraction}. */
    public static ExpectedCondition<Boolean> urlContains(String fraction) {
        return new ExpectedCondition<>() {
            private @Nullable String currentUrl = "";

            @Override
            public Boolean apply(BrowserDriver driver) {
                currentUrl = driver.getCurrentUrl();
                return currentUrl != null && currentUrl.contains(fraction);
            }

            @Override
            public String toString() {
                return String.format(
                        "url to contain \"%s\". Current url: \"%s\"", fraction,
                        currentUrl);
            }
        };
    }

    /**
     * The attribute is {@code expectedValue}; with no such attribute, the CSS
     * property of that name.
     */
    public static ExpectedCondition<Boolean> attributeToBe(
            TestBenchElement element, String attribute, String expectedValue) {
        return new ExpectedCondition<>() {
            private @Nullable String actualValue;

            @Override
            public Boolean apply(BrowserDriver driver) {
                actualValue = attributeOrCssValue(element, attribute);
                return expectedValue.equals(actualValue);
            }

            @Override
            public String toString() {
                return String.format(
                        "attribute or CSS value \"%s\"=\"%s\". Current value: \"%s\".",
                        attribute, expectedValue, actualValue);
            }
        };
    }

    /**
     * The attribute contains {@code expectedValue}; with no such attribute, the
     * CSS property of that name.
     */
    public static ExpectedCondition<Boolean> attributeContains(
            TestBenchElement element, String attribute, String expectedValue) {
        return new ExpectedCondition<>() {
            private @Nullable String actualValue;

            @Override
            public Boolean apply(BrowserDriver driver) {
                actualValue = attributeOrCssValue(element, attribute);
                return actualValue != null
                        && actualValue.contains(expectedValue);
            }

            @Override
            public String toString() {
                return String.format(
                        "attribute or CSS value \"%s\" to contain \"%s\". Current value: \"%s\".",
                        attribute, expectedValue, actualValue);
            }
        };
    }

    private static @Nullable String attributeOrCssValue(
            TestBenchElement element, String name) {
        String value = element.getAttribute(name);
        if (value == null || value.isEmpty()) {
            value = element.getCssValue(name);
        }
        return value == null || value.isEmpty() ? null : value;
    }

    /** The element found has exactly this text. */
    public static ExpectedCondition<Boolean> textToBe(By locator,
            String expectedText) {
        return new ExpectedCondition<>() {
            private @Nullable String actualText;
            private @Nullable RuntimeException error;

            @Override
            public Boolean apply(BrowserDriver driver) {
                actualText = null;
                error = null;
                try {
                    actualText = driver.findElement(locator).getText();
                    return actualText.equals(expectedText);
                } catch (NoSuchElementException
                        | StaleElementReferenceException e) {
                    error = e;
                    return false;
                }
            }

            @Override
            public String toString() {
                if (error != null) {
                    return String.format(
                            "element found by %s to have text \"%s\", but... %s.",
                            locator, expectedText, shortDescription(error));
                }
                return String.format(
                        "element found by %s to have text \"%s\". Current text: \"%s\".",
                        locator, expectedText, actualText);
            }
        };
    }

    /** The element's node is gone from the page. */
    public static ExpectedCondition<Boolean> stalenessOf(
            TestBenchElement element) {
        return new ExpectedCondition<>() {
            @Override
            public Boolean apply(BrowserDriver ignored) {
                try {
                    // Any call checks for staleness
                    element.isEnabled();
                    return false;
                } catch (StaleElementReferenceException
                        | NoSuchElementException expected) {
                    return true;
                }
            }

            @Override
            public String toString() {
                return String.format("element (%s) to become stale", element);
            }
        };
    }

    /** As many windows open as {@code expectedNumberOfWindows}. */
    public static ExpectedCondition<Boolean> numberOfWindowsToBe(
            int expectedNumberOfWindows) {
        return new ExpectedCondition<>() {
            private int actualNumberOfWindows = -1;

            @Override
            public Boolean apply(BrowserDriver driver) {
                actualNumberOfWindows = driver.getWindowHandles().size();
                return actualNumberOfWindows == expectedNumberOfWindows;
            }

            @Override
            public String toString() {
                return String.format(
                        "number of open windows to be %s, but was: %s",
                        expectedNumberOfWindows, actualNumberOfWindows);
            }
        };
    }

    /** Exactly this many found; holds as the elements. */
    public static ExpectedCondition<List<TestBenchElement>> numberOfElementsToBe(
            By locator, Integer expectedNumberOfElements) {
        return new ExpectedCondition<>() {
            private int actualNumberOfElements = -1;

            @Override
            public @Nullable List<TestBenchElement> apply(
                    BrowserDriver driver) {
                List<TestBenchElement> elements = driver.findElements(locator);
                actualNumberOfElements = elements.size();
                return expectedNumberOfElements.equals(actualNumberOfElements)
                        ? elements
                        : null;
            }

            @Override
            public String toString() {
                return String.format(
                        "number of elements found by %s to be %s. Found: %s element(s).",
                        locator, expectedNumberOfElements,
                        actualNumberOfElements);
            }
        };
    }

    /** Holds while {@code condition} does not. */
    public static ExpectedCondition<Boolean> not(
            ExpectedCondition<?> condition) {
        return new ExpectedCondition<>() {
            @Override
            public Boolean apply(BrowserDriver driver) {
                Object result = condition.apply(driver);
                return result == null || result.equals(Boolean.FALSE);
            }

            @Override
            public String toString() {
                return "condition not to be valid: " + condition;
            }
        };
    }

    // The exception's class and the first line of its message, as Selenium's
    private static String shortDescription(RuntimeException e) {
        String message = e.getMessage() == null ? "null" : e.getMessage();
        return e.getClass().getName() + ": " + message.split("\\n", 2)[0];
    }
}
