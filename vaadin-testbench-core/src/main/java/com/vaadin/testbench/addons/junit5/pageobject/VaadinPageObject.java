package com.vaadin.testbench.addons.junit5.pageobject;

import com.vaadin.testbench.proxy.TestBenchDriverProxy;
import org.openqa.selenium.WebDriver;

import static com.vaadin.frp.matcher.Case.match;
import static com.vaadin.frp.matcher.Case.matchCase;
import static com.vaadin.frp.model.Result.failure;
import static com.vaadin.frp.model.Result.success;
import static com.vaadin.testbench.addons.testbench.TestbenchFunctions.unproxy;
import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.webdriverName;

public interface VaadinPageObject extends GenericVaadinAppSpecific {

    String NO_DRIVER = "NoDriver";

    default String drivername() {
        final WebDriver driver = getDriver();
        return match(
                matchCase(() -> failure("no driver present")),
                matchCase(() -> driver instanceof TestBenchDriverProxy, () -> success(webdriverName().apply(unproxy().apply(driver)))),
                matchCase(() -> driver != null, () -> success(webdriverName().apply(driver)))
        )
//        .ifFailed(failed -> logger().warning(failed))
                .getOrElse(() -> NO_DRIVER);
    }
}
