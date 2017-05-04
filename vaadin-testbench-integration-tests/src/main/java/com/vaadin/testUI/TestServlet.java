package com.vaadin.testUI;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.vaadin.server.VaadinServlet;

@WebServlet(value = "/*", asyncSupported = true, initParams = {
        @WebInitParam(name = "heartbeatInterval", value = "10"),
        @WebInitParam(name = "widgetset", value = "com.vaadin.v7.Vaadin7WidgetSet"),
        @WebInitParam(name = "UIProvider", value = "com.vaadin.testUI.TestUIProvider") })
public class TestServlet extends VaadinServlet {
}
