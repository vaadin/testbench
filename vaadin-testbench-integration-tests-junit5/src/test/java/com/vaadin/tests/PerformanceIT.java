package com.vaadin.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PerformanceView;
import com.vaadin.testbench.TestBenchTest;
import com.vaadin.tests.elements.NativeButtonElement;

public class PerformanceIT extends AbstractJUnit5TB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return PerformanceView.class;
    }

    @TestBenchTest
    @Disabled("timeSpentServicingLastRequest test is unstable")
    public void serverTime() {
        openTestURL();
        $(NativeButtonElement.class).first().click();

        Assertions.assertEquals(1250.0,
                testBenchUtil.testBench().timeSpentServicingLastRequest(),
                250.0);
        $(NativeButtonElement.class).first().click();
        Assertions.assertEquals(2500,
                testBenchUtil.testBench().totalTimeSpentServicingRequests(),
                500.0);
    }

    @TestBenchTest
    @Disabled("timeSpentServicingLastRequest does not work: https://github.com/vaadin/testbench/issues/1316")
    public void renderingTime() {
        openTestURL();
        long initialRendering = testBenchUtil.testBench()
                .timeSpentRenderingLastRequest();
        // Assuming initial rendering is done in 1-299ms
        Assertions.assertEquals(150, initialRendering, 149);
        Assertions.assertEquals(initialRendering,
                testBenchUtil.testBench().totalTimeSpentRendering());
        $(NativeButtonElement.class).first().click();
        $(NativeButtonElement.class).first().click();
        $(NativeButtonElement.class).first().click();

        // Assuming rendering three poll responses is done in 50ms
        Assertions.assertTrue(
                testBenchUtil.testBench()
                        .totalTimeSpentRendering() > initialRendering,
                "totalTimeSpentRendering() > initialRendering");
        Assertions.assertEquals(initialRendering,
                testBenchUtil.testBench().totalTimeSpentRendering(), 50);
    }

}
