package com.vaadin.testbench.addons.junit5.extensions.container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApp {
    public static void main(String[] args) {
        SpringApplication.run(DemoApp.class, args);
    }

    @Bean
    public DemoServlet demoServlet() {
        return new DemoServlet();
    }

    @Bean
    public ServletRegistrationBean exampleServletBean(DemoServlet demoServlet) {
        ServletRegistrationBean bean = new ServletRegistrationBean(demoServlet, "/demo/*");
        bean.setLoadOnStartup(1);
        return bean;
    }
}
