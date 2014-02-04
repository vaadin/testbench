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

    public void deselectByText(String text) {
        selectElement.deselectByVisibleText(text);
    }

    public List<TestBenchElement> getAllSelectedOptions() {
        return wrapElements(selectElement.getAllSelectedOptions(),
                getCommandExecutor());
    }

    public TestBenchElement getFirstSelectedOption() {
        return wrapElement(selectElement.getFirstSelectedOption(),
                getCommandExecutor());
    }

    public List<TestBenchElement> getOptions() {
        return wrapElements(selectElement.getOptions(), getCommandExecutor());
    }

    public boolean isMultiple() {
        return selectElement.isMultiple();
    }

    public void selectByText(String text) {
        selectElement.selectByVisibleText(text);
    }
}
