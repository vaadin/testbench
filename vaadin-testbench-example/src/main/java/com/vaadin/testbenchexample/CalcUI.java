package com.vaadin.testbenchexample;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

@Theme("valo")
@Title("Calculator example")
@SuppressWarnings("serial")
public class CalcUI extends UI {

    @Override
    protected void init(VaadinRequest request) {

        // Define a panel that will hold our app
        Panel applicationPanel = new Panel();
        applicationPanel.setCaption("Calculator");
        applicationPanel.setStyleName("calculator-app");
        applicationPanel.setSizeUndefined();

        // Use Vaadin 7's style handling feature to add a margin around our app
        getPage().getStyles().add(".calculator-app { margin: 25px; }");

        // Instantiate a keypad, which contains our main logic
        Keypad keypad = new Keypad();

        // The Log object displays a list of performed operations, enhancing
        // usability
        Log log = new Log();

        // Connect our Log to the keypad
        keypad.addLogger(log);

        // Use a horizontal layout to place the keypad and log side-by-side
        HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.setSpacing(true);
        hlayout.setMargin(true);
        hlayout.addComponent(keypad);
        hlayout.addComponent(log);
        hlayout.setSizeUndefined();

        // Add the horizontal layout to our panel to define the application
        // content, and set the panel to be our UI content to display our app.
        applicationPanel.setContent(hlayout);
        setContent(applicationPanel);

    }

}
