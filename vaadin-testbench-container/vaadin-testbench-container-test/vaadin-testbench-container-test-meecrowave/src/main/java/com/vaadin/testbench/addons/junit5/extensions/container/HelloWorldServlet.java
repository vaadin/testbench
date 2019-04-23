package com.vaadin.testbench.addons.junit5.extensions.container;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/*")
public class HelloWorldServlet extends HttpServlet {

    @Inject
    private UpperCaseService service;

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws IOException {
        response.setContentType("text/plain; charset=utf-8");

        String value = request.getParameter("value");

        response.getWriter().println(service.upperCase(value));
    }
}
