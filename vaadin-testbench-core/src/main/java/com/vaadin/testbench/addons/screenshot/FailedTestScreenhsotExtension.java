package com.vaadin.testbench.addons.screenshot;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Method;
import java.util.Optional;

import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.takeScreenshot;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.webdriver;

public class FailedTestScreenhsotExtension implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext,
                                             Throwable throwable) throws Throwable {

        WebDriver webDriver = webdriver(extensionContext);

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

        takeScreenshot(finalName, webDriver);

        throw throwable;
    }
}
