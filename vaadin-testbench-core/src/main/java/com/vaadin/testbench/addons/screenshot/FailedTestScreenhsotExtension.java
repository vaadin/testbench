package com.vaadin.testbench.addons.screenshot;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Method;
import java.util.Optional;

import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.takeScreenShot;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.webdriver;

public class FailedTestScreenhsotExtension implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext,
                                             Throwable throwable) throws Throwable {

        WebDriver webDriver = webdriver().apply(extensionContext);

        String displayName = extensionContext.getDisplayName();
        Optional<Class<?>> testClass = extensionContext.getTestClass();
        Optional<Method> testMethod = extensionContext.getTestMethod();
        String className = testClass.get().getSimpleName();
        String methodName = testMethod.get().getName();

        String finalName = (className + "-" + methodName + "-" + displayName)
                .replaceAll(" ", "_")
                .replaceAll(":", "_")
                .replaceAll("/", "_");

        System.out.println("finalName = " + finalName);

        takeScreenShot(finalName).accept(webDriver);

        throw throwable;
    }
}
