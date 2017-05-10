package com.vaadin.tests.elements.ng;

import static com.vaadin.tests.elements.ng.tooling.BrowserDriverFunctions.readBrowserCombinations;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.TestBenchTestCase;

/**
 *
 */
// prepare @RunWith(ParallelRunner.class)
public class BaseParallelTest extends TestBenchTestCase {

    public BaseParallelTest() {
        throw new RuntimeException("not usable right now");
    }

    // prepare @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        return readBrowserCombinations
            .get()
            .getOrElse(Collections::emptyList);
    }

}
