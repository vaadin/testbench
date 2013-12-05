package com.vaadin.testbench.elements;

import java.util.List;

import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;

@ServerClass("com.vaadin.ui.NativeSelect")
public class NativeSelectElement extends AbstractSelectElement {
    private Select selectElement;

    @Override
    protected void init() {
        super.init();
        selectElement = new Select(findElement(By.tagName("select")));
    }

    @Override
    public void deselectAll() {
        selectElement.deselectAll();
    }

    @Override
    public void deselectByIndex(int index) {
        selectElement.deselectByIndex(index);
    }

    @Override
    public void deselectByValue(String value) {
        selectElement.deselectByValue(value);
    }

    @Override
    public void deselectByVisibleText(String text) {
        selectElement.deselectByVisibleText(text);
    }

    @Override
    public List<TestBenchElement> getAllSelectedOptions() {
        return wrapElements(selectElement.getAllSelectedOptions(),
                getCommandExecutor());
    }

    @Override
    public TestBenchElement getFirstSelectedOption() {
        return wrapElement(selectElement.getFirstSelectedOption(),
                getCommandExecutor());
    }

    @Override
    public List<TestBenchElement> getOptions() {
        return wrapElements(selectElement.getOptions(), getCommandExecutor());
    }

    @Override
    public boolean isMultiple() {
        return selectElement.isMultiple();
    }

    @Override
    public void selectByIndex(int index) {
        selectElement.selectByIndex(index);
    }

    @Override
    public void selectByValue(String value) {
        selectElement.selectByValue(value);
    }

    @Override
    public void selectByVisibleText(String text) {
        selectElement.selectByVisibleText(text);
    }
}
