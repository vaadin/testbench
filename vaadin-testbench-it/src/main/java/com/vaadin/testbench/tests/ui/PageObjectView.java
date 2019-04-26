package com.vaadin.testbench.tests.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route(PageObjectView.ROUTE)
public class PageObjectView extends Div {

    public static final String ROUTE = "PageObjectView";

    public PageObjectView() {
        Div someDiv = new Div();
        someDiv.setText("Some div");

        MyComponentWithId idView = new MyComponentWithId();
        MyComponentWithClasses classesView = new MyComponentWithClasses();

        Div anotherDiv = new Div();
        anotherDiv.setText("Some div");

        add(someDiv, idView, classesView, anotherDiv);
    }

    public static class MyComponentWithClasses extends Div {

        public MyComponentWithClasses() {
            addClassName("my-component-first");
            addClassName("my-component-with-classes");
            addClassName("my-component-last");
            setText(getClass().getSimpleName());
        }
    }

    public static class MyComponentWithId extends Div {

        public MyComponentWithId() {
            setId("my-component-with-id");
            setText(getClass().getSimpleName());
        }
    }
}
