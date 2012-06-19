package com.vaadin.testbenchexample;

import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * A simple calculator app using Vaadin. This app is just a demo app that can be
 * tested with TestBench. It is closely related to the official demo app, but
 * has some tuning to help testing like usage of setDebugId(String) and some
 * additional features to emphasize advanced features in TestBench.
 */
public class Calc extends Application {

    @Override
    public void init() {
        setMainWindow(new CalcWindow());
    }

    @Override
    public Window getWindow(String name) {
        // Multitab support, return new windows for each tab
        Window window = super.getWindow(name);
        if (window == null && name != null) {
            window = new CalcWindow();
            window.setName(name);
            addWindow(window);
        }
        return window;
    }

}
