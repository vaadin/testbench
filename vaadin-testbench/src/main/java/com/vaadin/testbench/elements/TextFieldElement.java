package com.vaadin.testbench.elements;

@ServerClass("com.vaadin.ui.TextField")
public class TextFieldElement extends AbstractTextFieldElement {
    public String getValue() {
        return getAttribute("value");
    }
}
