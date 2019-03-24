package com.vaadin.testbench.v08.tb.demo;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@WebServlet("/*")
@VaadinServletConfiguration(productionMode = false, ui = MyUI.class)
public class MyProjectServlet extends VaadinServlet { }
