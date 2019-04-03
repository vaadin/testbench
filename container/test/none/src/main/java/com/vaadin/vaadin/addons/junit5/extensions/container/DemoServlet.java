package com.vaadin.vaadin.addons.junit5.extensions.container;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.servlet.GenericServlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class DemoServlet extends GenericServlet {

    @Autowired
    private Environment environment;

    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws IOException {
        response.setContentType("text/plain");
        response.getWriter()
                .append("Hello World on port " + environment.getProperty("local.server.port"));
    }
}
