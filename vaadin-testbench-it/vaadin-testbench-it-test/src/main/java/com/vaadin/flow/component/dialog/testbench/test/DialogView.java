package com.vaadin.flow.component.dialog.testbench.test;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(DialogView.NAV)
@Theme(Lumo.class)
public class DialogView extends AbstractView {

    public static final String THE_DIALOG = "the-dialog";
    public static final String NAV = "Dialog";
    private Dialog dialog;

    public DialogView() {
        dialog = new Dialog();
        dialog.setId(THE_DIALOG);
        dialog.add(new Label("This is the contents of the dialog"));
        dialog.add(new Button("Close", e -> dialog.close()));
        dialog.open();
    }

}
