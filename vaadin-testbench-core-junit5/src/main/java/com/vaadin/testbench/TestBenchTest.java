package com.vaadin.testbench;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.vaadin.testbench.capabilities.DesiredCapabilitiesInvocationContextProvider;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@TestTemplate
@ExtendWith(DesiredCapabilitiesInvocationContextProvider.class)
public @interface TestBenchTest {

}
