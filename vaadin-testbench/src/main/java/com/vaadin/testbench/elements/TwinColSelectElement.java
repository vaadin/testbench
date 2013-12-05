package com.vaadin.testbench.elements;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;

@ServerClass("com.vaadin.ui.TwinColSelect")
public class TwinColSelectElement extends AbstractSelectElement {

    private Select select;
    private Select selected;
    private WebElement deselButton;
    private WebElement selButton;

    @Override
    public void init() {
        super.init();
        List<WebElement> selectElements = findElements(By.tagName("select"));
        select = new Select(selectElements.get(0));
        selected = new Select(selectElements.get(1));
        List<WebElement> buttons = findElements(By.className("v-button"));
        selButton = buttons.get(0);
        deselButton = buttons.get(1);
    }

    @Override
    public void deselectAll() {
        if (selected.isMultiple()) {
            if (selected.getAllSelectedOptions().size() != selected
                    .getOptions().size()) {
                for (int i = 0, l = selected.getOptions().size(); i < l; ++i) {
                    selected.selectByIndex(i);
                }
            }
            deselButton.click();
        }
        while (selected.getOptions().size() > 0) {
            selected.selectByIndex(0);
            deselButton.click();
        }
    }

    @Override
    public void deselectByIndex(int index) {
        index -= getUnselectedOptions().size();
        if (index >= 0 && index < selected.getAllSelectedOptions().size()) {
            if (selected.isMultiple()) {
                selected.deselectAll();
            }
            selected.selectByIndex(index);
            deselButton.click();
        }
    }

    @Override
    public void deselectByValue(String value) {
        if (selected.isMultiple()) {
            selected.deselectAll();
        }
        selected.selectByValue(value);
        deselButton.click();
    }

    @Override
    public void deselectByVisibleText(String text) {
        if (selected.isMultiple()) {
            selected.deselectAll();
        }
        selected.selectByVisibleText(text);
        deselButton.click();
    }

    @Override
    public List<TestBenchElement> getAllSelectedOptions() {
        return wrapElements(selected.getAllSelectedOptions(),
                getCommandExecutor());
    }

    @Override
    public TestBenchElement getFirstSelectedOption() {
        return wrapElement(selected.getFirstSelectedOption(),
                getCommandExecutor());
    }

    @Override
    public List<TestBenchElement> getOptions() {
        List<TestBenchElement> options = wrapElements(select.getOptions(),
                getCommandExecutor());
        options.addAll(wrapElements(selected.getOptions(), getCommandExecutor()));
        return options;
    }

    /**
     * Functionality to find out what options are currently unselected.
     * 
     * @return TestBenchElement list of unselected options
     */
    public List<TestBenchElement> getUnselectedOptions() {
        return wrapElements(select.getOptions(), getCommandExecutor());
    }

    @Override
    public void selectByIndex(int index) {
        if (index < getUnselectedOptions().size()) {
            if (selected.isMultiple()) {
                selected.deselectAll();
            }

            select.selectByIndex(index);
            selButton.click();
        }
    }

    @Override
    public boolean isMultiple() {
        return true;
    }

    @Override
    public void selectByValue(String value) {
        if (selected.isMultiple()) {
            selected.deselectAll();
        }

        select.selectByValue(value);
        selButton.click();
    }

    @Override
    public void selectByVisibleText(String text) {
        if (selected.isMultiple()) {
            selected.deselectAll();
        }

        select.selectByVisibleText(text);
        selButton.click();
    }
}