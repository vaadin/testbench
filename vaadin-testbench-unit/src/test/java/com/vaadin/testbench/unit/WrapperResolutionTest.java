/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;

@ComponentWrapPackages("com.vaadin.testbench.unit")
public class WrapperResolutionTest extends UIUnitTest {

    @Override
    protected String scanPackage() {
        return "com.vaadin.testbench";
    }

    @Test
    public void wrapTest_returnsTestWrap() {
        TestComponent tc = new TestComponent();

        Assertions.assertTrue(wrap(tc) instanceof TestWrap);
    }

    @Test
    public void wrapComponentExtendingTest_returnsTestWrap() {
        MyTest mt = new MyTest();

        Assertions.assertTrue(wrap(mt) instanceof TestWrap);
    }

    @Test
    public void wrapOtherComponent_returnsGenericComponentWrap() {
        SpecialComponent sc = new SpecialComponent();
        Assertions.assertTrue(wrap(sc).getClass().equals(ComponentWrap.class));
    }

    @Test
    public void wrapTestComponentForConcreteWrapper_returnsNonGenericTestWrap() {
        TestComponentForConcreteWrapper component = new TestComponentForConcreteWrapper();
        Assertions.assertEquals(wrap(component).getClass(),
                NonGenericTestWrap.class);
    }

    @Test
    void detectComponentType_resolvesComponentTypeThroughHierarchy() {
        Assertions.assertEquals(Component.class,
                detectComponentType(ComponentWrap.class));
        Assertions.assertEquals(TestComponent.class,
                detectComponentType(MyWrap.class));
        Assertions.assertEquals(MyTest.class,
                detectComponentType(MyExtendedWrap.class));
        Assertions.assertEquals(TestComponentForConcreteWrapper.class,
                detectComponentType(NonGenericTestWrap.class));
    }

    public static class MyTest extends TestComponent {
    }

    @Tag("div")
    public static class SpecialComponent extends Component {
    }

    static class MyWrap<Y, Z extends TestComponent> extends ComponentWrap<Z> {
        public MyWrap(Z component) {
            super(component);
        }
    }

    static class MyExtendedWrap<Y> extends MyWrap<Y, MyTest> {
        public MyExtendedWrap(MyTest component) {
            super(component);
        }
    }

}
