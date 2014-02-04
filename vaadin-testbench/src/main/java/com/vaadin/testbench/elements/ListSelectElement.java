package com.vaadin.testbench.elements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

@ServerClass("com.vaadin.ui.ListSelect")
public class ListSelectElement extends AbstractSelectElement {

    private Select select;
    private static By bySelect = By.tagName("select");

    @Override
    protected void init() {
        super.init();
        select = new Select(findElement(bySelect));
    }

    public void selectByText(String text) {
        select.selectByVisibleText(text);
    }

    public void deselectByText(String text) {
        select.deselectByVisibleText(text);
    }

    public List<String> getOptions() {
        List<String> options = new ArrayList<String>();
        for (WebElement webElement : select.getOptions()) {
            options.add(webElement.getText());
        }
        return options;
    }

}
