package com.vaadin.vaadin.addons.junit5.extensions.container;

import com.vaadin.testbench.addons.junit5.extensions.container.UpperCaseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UpperCaseServiceTest {

    @Test
    void test001() {
        Assertions.assertEquals("HALLO", new UpperCaseService().upperCase("hallo"));
    }
}
