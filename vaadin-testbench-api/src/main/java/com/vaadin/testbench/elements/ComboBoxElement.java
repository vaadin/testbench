/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.ComboBox")
public class ComboBoxElement extends AbstractSelectElement {

    private static org.openqa.selenium.By bySuggestionPopup = By
            .vaadin("#popup");
    private static org.openqa.selenium.By byNextPage = By
            .className("v-filterselect-nextpage");
    private static org.openqa.selenium.By byPrevPage = By
            .className("v-filterselect-prevpage");

    /**
     * Selects the first option in the ComboBox which matches the given text.
     *
     * @param text
     *            the text of the option to select
     */
    public void selectByText(String text) {
        if (!isTextInputAllowed()) {
            selectByTextFromPopup(text);
            return;
        }
        getInputField().clear();
        sendInputFieldKeys(text);

        List<String> popupSuggestions = getPopupSuggestions();
        if (popupSuggestions.size() != 0
                && text.equals(popupSuggestions.get(0))) {
            getSuggestionPopup().findElement(By.tagName("td")).click();
        }
    }

    /**
     * Selects, without filtering, the first option in the ComboBox which
     * matches the given text.
     * 
     * @param text
     *            the text of the option to select
     */
    private void selectByTextFromPopup(String text) {
        // This method assumes there is no need to touch the filter string

        // 1. Find first page
        // 2. Select first matching text if found
        // 3. Iterate towards end

        while (openPrevPage()) {
            // Scroll until beginning
        }

        do {
            for (WebElement suggestion : getPopupSuggestionElements()) {
                if (text.equals(suggestion.getText())) {
                    suggestion.click();
                    return;
                }
            }
        } while (openNextPage());
    }

    private boolean isReadOnly(WebElement elem) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        return (Boolean) js.executeScript("return arguments[0].readOnly", elem);
    }

    /**
     * Checks if text input is allowed for the combo box.
     * 
     * @return <code>true</code> if text input is allowed, <code>false</code>
     *         otherwise
     */
    public boolean isTextInputAllowed() {
        return !isReadOnly(getInputField());
    }

    /*
     * Workaround selenium's bug: sendKeys() will not send left parentheses
     * properly. See #14048.
     */
    private void sendInputFieldKeys(String text) {
        WebElement textBox = getInputField();
        if (!text.contains("(")) {
            textBox.sendKeys(text);
            return;
        }

        String OPEN_PARENTHESES = "_OPEN_PARENT#H#ESES_";
        String tamperedText = text.replaceAll("\\(", OPEN_PARENTHESES);
        textBox.sendKeys(tamperedText);

        JavascriptExecutor js = getCommandExecutor();
        String jsScript = String.format(
                "arguments[0].value = arguments[0].value.replace(/%s/g, '(')",
                OPEN_PARENTHESES);
        js.executeScript(jsScript, textBox);

        // refresh suggestions popupBox
        textBox.sendKeys("a" + Keys.BACK_SPACE);
    }

    /**
     * Open the suggestion popup
     */
    public void openPopup() {
        findElement(By.vaadin("#button")).click();
    }

    /**
     * Gets the text representation of all suggestions on the current page
     *
     * @return List of suggestion texts
     */
    public List<String> getPopupSuggestions() {
        List<String> suggestionsTexts = new ArrayList<String>();
        List<WebElement> suggestions = getPopupSuggestionElements();
        for (WebElement suggestion : suggestions) {
            String text = suggestion.getText();
            if (!text.isEmpty()) {
                suggestionsTexts.add(text);
            }
        }
        return suggestionsTexts;
    }

    /**
     * Gets the elements of all suggestions on the current page.
     * <p>
     * Opens the popup if not already open.
     *
     * @return a list of elements for the suggestions on the current page
     */
    public List<WebElement> getPopupSuggestionElements() {
        List<WebElement> tables = getSuggestionPopup()
                .findElements(By.tagName("table"));
        if (tables == null || tables.isEmpty()) {
            return Collections.emptyList();
        }
        WebElement table = tables.get(0);
        return table.findElements(By.tagName("td"));
    }

    /**
     * Opens next popup page.
     *
     * @return True if next page opened. false if doesn't have next page
     */
    public boolean openNextPage() {
        try {
            getSuggestionPopup().findElement(byNextPage).click();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Open previous popup page.
     *
     * @return True if previous page opened. False if doesn't have previous page
     */
    public boolean openPrevPage() {
        try {
            getSuggestionPopup().findElement(byPrevPage).click();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Returns the suggestion popup element
     */
    public WebElement getSuggestionPopup() {
        ensurePopupOpen();
        return findElement(bySuggestionPopup);
    }

    /**
     * Return value of the combo box element
     *
     * @return value of the combo box element
     */
    public String getValue() {
        return getInputField().getAttribute("value");
    }

    /**
     * Returns the text input field element, used for entering text into the
     * combo box.
     *
     * @return the input field element
     */
    public WebElement getInputField() {
        return findElement(By.xpath("input"));
    }

    private void ensurePopupOpen() {
        if (!isElementPresent(bySuggestionPopup)) {
            openPopup();
        }
    }
}
