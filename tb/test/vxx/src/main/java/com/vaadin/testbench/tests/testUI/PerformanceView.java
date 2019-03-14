package com.vaadin.testbench.tests.testUI;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(PerformanceView.ROUTE)
public class PerformanceView extends Div {

    public static final String ROUTE = "PerformanceView";

    public PerformanceView() {
        NativeButton button = new NativeButton("1s delay", e -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
            }
            add(new Span("Done sleeping"));
        });
        add(button);
    }

}
