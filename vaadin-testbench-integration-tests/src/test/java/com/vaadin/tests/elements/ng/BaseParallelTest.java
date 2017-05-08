package com.vaadin.tests.elements.ng;

import static com.vaadin.tests.elements.ng.tooling.BrowserDriverFunctions.readBrowserCombinations;
import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.ParallelRunner;

/**
 *
 */
@RunWith(ParallelRunner.class)
public class BaseParallelTest extends TestBenchTestCase {

    public BaseParallelTest() {
        throw new RuntimeException("not unsable right now");
    }

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        return readBrowserCombinations
            .get()
            .getOrElse(Collections::emptyList);
    }


}
