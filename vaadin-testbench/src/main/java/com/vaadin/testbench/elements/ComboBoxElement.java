package com.vaadin.testbench.elements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;

@ServerClass("com.vaadin.ui.ComboBox")
public class ComboBoxElement extends AbstractSelectElement {

    private static org.openqa.selenium.By bySuggestMenu = By
            .className("v-filterselect-suggestmenu");
    private static org.openqa.selenium.By byNextPage = By
            .className("v-filterselect-nextpage");
    private static org.openqa.selenium.By byPrevPage = By
            .className("v-filterselect-prevpage");

    /**
     * Input the given text to ComboBox
     * 
     * @param text
     */
    public void selectByText(String text) {
        findElement(By.vaadin("#textbox")).sendKeys(text, Keys.ENTER);
    }

    /**
     * Open the suggestion popup
     */
    public void openPopup() {
        findElement(By.vaadin("#button")).click();
    }

    /**
     * Get the text representation of all suggestions on the current page
     * 
     * @return List of suggestion texts
     */
    public List<String> getPopupSuggestions() {
        List<String> suggestionsTexts = new ArrayList<String>();
        if (!isElementPresent(bySuggestMenu)) {
            openPopup();
        }
        try {
            List<WebElement> suggestions = getSuggestionMenu().findElements(
                    By.tagName("span"));
            for (WebElement suggestion : suggestions) {
                suggestionsTexts.add(suggestion.getText());
            }
        } catch (NoSuchElementException e) {
            // Something went horribly wrong.
        }
        return suggestionsTexts;
    }

    /**
     * Opens next popup page.
     * 
     * @return True if next page opened. false if doesn't have next page
     */
    public boolean openNextPage() {
        try {
            getSuggestionMenu().findElement(byNextPage).click();
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
            getSuggestionMenu().findElement(byPrevPage).click();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private WebElement getSuggestionMenu() {
        ensurePopupOpen();
        // Following needs an extension to ComboBox subparts.
        // return findElement(By.vaadin("#menu"));
        return getDriver().findElement(bySuggestMenu);
    }

    private void ensurePopupOpen() {
        if (getDriver().findElements(bySuggestMenu).isEmpty()) {
            openPopup();
        }
    }
}
