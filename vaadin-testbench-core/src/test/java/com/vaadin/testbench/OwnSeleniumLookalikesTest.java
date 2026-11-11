/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * TestBench's own {@code By}, {@code Keys}, {@code Point}, {@code Dimension}
 * and {@code Rectangle} stand in for Selenium's, so a test ports by an import
 * change: each must say what Selenium's says.
 */
@SuppressWarnings("deprecation")
public class OwnSeleniumLookalikesTest {

    /** Left out on purpose: Chrome receives them as the left-hand keys. */
    private static final Set<String> LEFT_OUT = Set.of("RIGHT_SHIFT",
            "RIGHT_CONTROL", "RIGHT_ALT", "RIGHT_COMMAND", "FN");

    @Test
    public void keysMatchSeleniumNameForNameAndCodeForCode() {
        Map<String, Integer> selenium = new LinkedHashMap<>();
        for (org.openqa.selenium.Keys key : org.openqa.selenium.Keys.values()) {
            if (!LEFT_OUT.contains(key.name())) {
                selenium.put(key.name(), key.getCodePoint());
            }
        }
        Map<String, Integer> ours = new LinkedHashMap<>();
        for (Keys key : Keys.values()) {
            ours.put(key.name(), key.getCodePoint());
        }
        Assert.assertEquals(selenium, ours);
    }

    @Test
    public void keysBehaveAsSeleniumsAsCharSequences() {
        Assert.assertEquals(org.openqa.selenium.Keys
                .chord(org.openqa.selenium.Keys.CONTROL, "a"),
                Keys.chord(Keys.CONTROL, "a"));
        Assert.assertEquals(org.openqa.selenium.Keys.ENTER.toString(),
                Keys.ENTER.toString());
        Assert.assertEquals(Keys.LEFT,
                Keys.getKeyFromUnicode(Keys.ARROW_LEFT.charAt(0)));
        Assert.assertNull(Keys.getKeyFromUnicode('a'));
    }

    @Test
    public void byIsSeleniumsCssWithItsEscaping() {
        Assert.assertEquals("#save", By.id("save").toCssSelector());
        Assert.assertEquals("#a\\.b\\ c", By.id("a.b c").toCssSelector());
        Assert.assertEquals("#\\31 23", By.id("123").toCssSelector());
        Assert.assertEquals(".v\\-\\[x\\]",
                By.className("v-[x]").toCssSelector());
        Assert.assertEquals("vaadin-button",
                By.tagName("vaadin-button").toCssSelector());
        Assert.assertEquals("a > b", By.cssSelector("a > b").toCssSelector());
    }

    @Test
    public void byPrintsAndComparesAsSeleniums() {
        for (String[] pair : new String[][] {
                { By.id("x").toString(),
                        org.openqa.selenium.By.id("x").toString() },
                { By.className("x").toString(),
                        org.openqa.selenium.By.className("x").toString() },
                { By.tagName("x").toString(),
                        org.openqa.selenium.By.tagName("x").toString() },
                { By.cssSelector("x").toString(), org.openqa.selenium.By
                        .cssSelector("x").toString() } }) {
            Assert.assertEquals(Arrays.toString(pair), pair[1], pair[0]);
        }
        Assert.assertEquals(By.id("x"), By.id("x"));
        Assert.assertNotEquals(By.id("x"), By.className("x"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void byClassNameRejectsCompoundNames() {
        By.className("a b");
    }

    @Test(expected = IllegalArgumentException.class)
    public void byTagNameRejectsBlank() {
        By.tagName("");
    }

    @Test
    public void geometryIsSeleniumsFieldForField() {
        Rectangle rect = new Rectangle(1, 2, 30, 40);
        org.openqa.selenium.Rectangle seleniumRect = new org.openqa.selenium.Rectangle(
                1, 2, 30, 40);
        Assert.assertEquals(seleniumRect.getHeight(), rect.getHeight());
        Assert.assertEquals(seleniumRect.getWidth(), rect.getWidth());
        Assert.assertEquals(rect,
                new Rectangle(new Point(1, 2), new Dimension(40, 30)));
        Assert.assertEquals(new Dimension(40, 30), rect.getDimension());
        Assert.assertEquals(new Point(1, 2), rect.getPoint());

        Assert.assertEquals(new org.openqa.selenium.Point(3, 4).toString(),
                new Point(1, 2).moveBy(2, 2).toString());
        Assert.assertEquals(
                new org.openqa.selenium.Dimension(800, 600).toString(),
                new Dimension(800, 600).toString());
    }
}
