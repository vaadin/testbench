package com.vaadin.flow.component.textfield.testbench.test;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(TextFieldView.NAV)
@Theme(Lumo.class)
public class TextFieldView extends AbstractView {

    public static final String LABEL_EAGER = "text";
    public static final String NOLABEL = "notext";
    public static final String INITIAL_VALUE = "initialvalue";
    public static final String PLACEHOLDER = "placeholder";
    public static final String NAV = "TextField";

    public TextFieldView() {

        TextField textfieldNoLabel = new TextField();
        textfieldNoLabel.setId(NOLABEL);
        textfieldNoLabel.addValueChangeListener(this::onValueChange);

        add(textfieldNoLabel);

        TextField textfieldLabel = new TextField("Label (eager)");
        textfieldLabel.setValueChangeMode(ValueChangeMode.EAGER);
        textfieldLabel.setId(LABEL_EAGER);
        textfieldLabel.addValueChangeListener(this::onValueChange);
        add(textfieldLabel);

        TextField textfieldInitialValue = new TextField("Has an initial value");
        textfieldInitialValue.setId(INITIAL_VALUE);
        textfieldInitialValue.setValue("Initial");
        textfieldInitialValue.addValueChangeListener(this::onValueChange);
        add(textfieldInitialValue);

        TextField textfieldPlaceholder = new TextField("Has a placeholder");
        textfieldPlaceholder.setId(PLACEHOLDER);
        textfieldPlaceholder.setPlaceholder("Text goes here");
        textfieldPlaceholder.addValueChangeListener(this::onValueChange);
        add(textfieldPlaceholder);
    }

    protected void onValueChange(ComponentValueChangeEvent<TextField, String> e) {
        String label = e.getSource().getLabel();
        if (label == null) {
            label = "";
        }
        log("Value of '" + label + "' is now " + e.getValue());
    }

}
