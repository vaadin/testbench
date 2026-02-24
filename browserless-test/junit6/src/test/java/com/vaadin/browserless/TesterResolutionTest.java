/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;

@ComponentTesterPackages("com.vaadin.testbench.unit")
@ViewPackages(packages = "com.vaadin.browserless")
public class TesterResolutionTest extends UIUnitTest {

    @Test
    public void wrapTest_returnsTestWrap() {
        TestComponent tc = new TestComponent();

        Assertions.assertTrue(test(tc) instanceof TestTester);
    }

    @Test
    public void wrapComponentExtendingTest_returnsTestWrap() {
        MyTest mt = new MyTest();

        Assertions.assertTrue(test(mt) instanceof TestTester);
    }

    @Test
    public void wrapOtherComponent_returnsGenericComponentWrap() {
        SpecialComponent sc = new SpecialComponent();
        Assertions
                .assertTrue(test(sc).getClass().equals(ComponentTester.class));
    }

    @Test
    public void wrapTestComponentForConcreteWrapper_returnsNonGenericTestWrap() {
        TestComponentForConcreteTester component = new TestComponentForConcreteTester();
        Assertions.assertEquals(test(component).getClass(),
                NonGenericTestTester.class);
    }

    @Test
    void detectComponentType_resolvesComponentTypeThroughHierarchy() {
        Assertions.assertEquals(Component.class,
                detectComponentType(ComponentTester.class));
        Assertions.assertEquals(TestComponent.class,
                detectComponentType(MyTester.class));
        Assertions.assertEquals(MyTest.class,
                detectComponentType(MyExtendedTester.class));
        Assertions.assertEquals(TestComponentForConcreteTester.class,
                detectComponentType(NonGenericTestTester.class));
    }

    public static class MyTest extends TestComponent {
    }

    @Tag("div")
    public static class SpecialComponent extends Component {
    }

    static class MyTester<Y, Z extends TestComponent>
            extends ComponentTester<Z> {
        public MyTester(Z component) {
            super(component);
        }
    }

    static class MyExtendedTester<Y> extends MyTester<Y, MyTest> {
        public MyExtendedTester(MyTest component) {
            super(component);
        }
    }

}
