package com.vaadin.testbench.annotations;

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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnotationHelperTest {

    private static Method objectUnderTest;

    @BeforeAll
    static void setup() throws NoSuchMethodException {
        objectUnderTest = TestClass.class.getMethod("testMethod");
    }

    @Test
    void findFirstSpecifiedParameter() {
        assertEquals("Them", AnnotationHelper.findFirstSpecifiedParameter(
                objectUnderTest, TestAnnotation.class, TestAnnotation::param, "MyParam"));

        assertEquals("Them", AnnotationHelper.findFirstSpecifiedParameter(
                TestClass.class, TestAnnotation.class, TestAnnotation::param, "MyParam"));

        assertEquals("MyParam", AnnotationHelper.findFirstSpecifiedParameter(
                objectUnderTest, TestAnnotation.class, TestAnnotation::param, "DefaultValue"));

        assertArrayEquals(new String[] {"Me", "You"}, AnnotationHelper.findFirstSpecifiedParameter(
                objectUnderTest, TestAnnotation.class, TestAnnotation::arrayParam, new String[0]));
    }

    @Test
    void mergeSpecifiedParameters() {
        assertArrayEquals(
                new String[] {"MyParam", "Them"},
                AnnotationHelper.mergeSpecifiedParameters(
                        objectUnderTest, TestAnnotation.class, TestAnnotation::param, new LinkedList<>()).toArray());

        assertArrayEquals(
                new String[][] {new String[] {"Me", "You"}, new String[] {"Us"}},
                AnnotationHelper.mergeSpecifiedParameters(
                        objectUnderTest, TestAnnotation.class, TestAnnotation::arrayParam, new LinkedList<>()).toArray());
    }

    @Test
    void flatten() {
        final List<String[]> input = Arrays.asList(
                new String[]{"a", "b"},
                new String[]{"c", "d"},
                new String[]{"e", "f", "g"});

        final Collection<String> expected = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
        final Collection<String> actual = AnnotationHelper.flatten(input);

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Target({TYPE, METHOD})
    @Retention(RUNTIME)
    @interface TestAnnotation {

        String param() default "MyParam";
        String[] arrayParam();
    }

    @TestAnnotation(param = "Them", arrayParam = "Us")
    static abstract class TestSuperClass {
    }

    public static class TestClass extends TestSuperClass {

        @TestAnnotation(arrayParam = {"Me", "You"})
        public void testMethod() {
            assertTrue(true);
        }
    }
}
