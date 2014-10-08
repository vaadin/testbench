package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Form;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TwinColSelect;

public class AbstractFieldElementSetValueReadOnly extends AbstractTestUI {
    @WebServlet(value = { "/VAADIN/*",
            "/AbstractFieldElementSetValueReadOnly/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = AbstractFieldElementSetValueReadOnly.class)
    public static class Servlet extends VaadinServlet {
    }

    AbstractField<?>[] elems = { new ComboBox(), new ListSelect(),
            new NativeSelect(), new OptionGroup(), new Table(), new Tree(),
            new TwinColSelect(), new TextArea(), new TextField(),
            new DateField(), new PasswordField(), new CheckBox(), new Form(),
            new ProgressBar(), new RichTextArea(), new Slider() };

    @Override
    protected void setup(VaadinRequest request) {
        for (int i = 0; i < elems.length; i++) {
            elems[i].setReadOnly(true);
            addComponent(elems[i]);
        }
    }

    @Override
    protected String getTestDescription() {
        return "When vaadin element is set ReadOnly, setValue() method should raise an exception";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14068;
    }

}
