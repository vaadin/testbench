package com.vaadin.flow.component.checkbox.testbench.test;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(CheckboxView.NAV)
@Theme(Lumo.class)
public class CheckboxView extends AbstractView {

    public static final String TEXT = "text";
    public static final String NOTEXT = "notext";
    public static final String CHECKED = "checked";
    public static final String NAV = "Checkbox";

    public CheckboxView() {

        Checkbox checkBoxWithoutText = new Checkbox();
        checkBoxWithoutText.setId(NOTEXT);
        checkBoxWithoutText.addClickListener(e -> {
            log("CheckBox '" + e.getSource().getLabel() + "' clicked");
        });
        add(checkBoxWithoutText);

        Checkbox checkBoxWithText = new Checkbox("Text");
        checkBoxWithText.setId(TEXT);
        checkBoxWithText.addClickListener(e -> {
            log("CheckBox '" + e.getSource().getLabel() + "' clicked");
        });
        add(checkBoxWithText);

        Checkbox initiallyChecked = new Checkbox("Checked initially");
        initiallyChecked.setId(CHECKED);
        initiallyChecked.addClickListener(e -> {
            log("CheckBox '" + e.getSource().getLabel() + "' clicked");
        });
        initiallyChecked.setValue(true);
        add(initiallyChecked);
    }

}
