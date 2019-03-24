package com.vaadin.testbench.addons.junit5.extensions.container;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;


public class DemoServlet extends GenericServlet {

  @Autowired
  private Environment environment;

  @Override
  public void service(ServletRequest request, ServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/plain");
    response.getWriter()
        .append("Hello World on port " + environment.getProperty("local.server.port"));
  }
}
