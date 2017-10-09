/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.Element;

public class ElementQueryTest {

    private static final Object DOCUMENT_QUERY_SCRIPT = "return document.querySelectorAll(arguments[1]+arguments[2])";
    private static final Object ELEMENT_QUERY_SCRIPT = "return arguments[0].shadowRoot.querySelectorAll(arguments[1]+arguments[2])";
    private WebDriverWithJS mockDriver;

    @Element(ExampleElement.TAG)
    public static class ExampleElement extends TestBenchElement {
        public static final String TAG = "example-element";
    }

    @Element("other-element")
    public static class OtherElement extends TestBenchElement {
    }

    @Element("third-element")
    public static class ThirdElement extends TestBenchElement {
    }

    public abstract class WebDriverWithJS
            implements WebDriver, JavascriptExecutor {

    }

    @Before
    public void setup() {
        mockDriver = EasyMock.createMock(WebDriverWithJS.class);
    }

    public class TestElementQuery<T extends TestBenchElement>
            extends ElementQuery<T> {
        private String lastScript;
        private Object lastContext;
        private String lastTagName;
        private Object lastAttributePairs;
        private boolean executed;
        private JavascriptExecutor lastExecutor;

        public TestElementQuery(Class<T> elementClass) {
            super(elementClass);
        }

        @Override
        protected List<T> executeSearchScript(String script, Object context,
                String tagName, String attributePairs,
                JavascriptExecutor executor) {
            if (executed) {
                throw new IllegalStateException(
                        "Query was already executed once");
            }
            executed = true;
            lastScript = script;
            lastContext = context;
            lastTagName = tagName;
            lastAttributePairs = attributePairs;
            lastExecutor = executor;
            return new ArrayList<>();
        }
    }

    @Test
    public void findAllElementsInDocument() {
        TestElementQuery<ExampleElement> query = new TestElementQuery<>(
                ExampleElement.class);
        query.context(mockDriver).all();
        Assert.assertEquals(DOCUMENT_QUERY_SCRIPT, query.lastScript);
        Assert.assertEquals("", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertNull(query.lastContext);
    }

    @Test
    public void findAllElementsInElement() {
        TestBenchElement context = TestBenchElement
                .wrapElement(EasyMock.createMock(WebElement.class), null);
        TestElementQuery<ExampleElement> query = new TestElementQuery<>(
                ExampleElement.class);
        query.context(context).all();
        Assert.assertEquals(ELEMENT_QUERY_SCRIPT, query.lastScript);
        Assert.assertEquals("", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(context, query.lastContext);
    }

}
