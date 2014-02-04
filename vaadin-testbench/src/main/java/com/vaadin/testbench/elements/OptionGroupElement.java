package com.vaadin.testbench.elements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;

@ServerClass("com.vaadin.ui.OptionGroup")
public class OptionGroupElement extends AbstractSelectElement {

    private static org.openqa.selenium.By byButtonSpan = By
            .className("v-select-option");
    private static org.openqa.selenium.By byLabel = By.tagName("label");
    private static org.openqa.selenium.By byRadioInput = By.tagName("input");

    public List<String> getOptions() {
        List<String> optionTexts = new ArrayList<String>();
        List<WebElement> options = findElements(byButtonSpan);
        for (WebElement option : options) {
            optionTexts.add(option.findElement(byLabel).getText());
        }
        return optionTexts;
    }

    public void selectByText(String text) {
        List<WebElement> options = findElements(byButtonSpan);
        for (WebElement option : options) {
            if (text.equals(option.findElement(byLabel).getText())) {
                option.findElement(byRadioInput).click();
            }
        }
    }
}
