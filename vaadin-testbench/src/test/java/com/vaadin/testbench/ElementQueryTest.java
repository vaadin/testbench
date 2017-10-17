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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.Element;

public class ElementQueryTest {

    private static final String DOCUMENT_QUERY_FRAGMENT = "document.querySelectorAll(arguments[1]+arguments[2])";
    private static final String ELEMENT_QUERY_FRAGMENT = "arguments[0].shadowRoot.querySelectorAll(arguments[1]+arguments[2])";
    private static final String SINGLE_RESULT_QUERY_SUFFIX = "[0]";
    private WebDriverWithJS mockDriver;
    private TestBenchElement exampleElement;

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
        exampleElement = TestBenchElement
                .wrapElement(EasyMock.createMock(WebElement.class), null);

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
        public TestElementQuery<T> context(SearchContext searchContext) {
            return (TestElementQuery<T>) super.context(searchContext);
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
    public void findInDocument_allElements() {
        TestElementQuery<ExampleElement> query = createExampleDocumentQuery();
        query.all();
        Assert.assertTrue("last query script contains DOCUMENT_QUERY_FRAGMENT",
                query.lastScript.contains(DOCUMENT_QUERY_FRAGMENT));
        Assert.assertEquals("", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertNull(query.lastContext);
    }

    private TestElementQuery<ExampleElement> createExampleDocumentQuery() {
        return new TestElementQuery<>(ExampleElement.class).context(mockDriver);
    }

    private TestElementQuery<ExampleElement> createExampleElementQuery() {
        return new TestElementQuery<>(ExampleElement.class)
                .context(exampleElement);

    }

    @Test
    public void findInElement_allElements() {
        TestElementQuery<ExampleElement> query = createExampleElementQuery();
        query.all();
        Assert.assertTrue("last query script contains ELEMENT_QUERY_FRAGMENT",
                query.lastScript.contains(ELEMENT_QUERY_FRAGMENT));
        Assert.assertEquals("", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(exampleElement, query.lastContext);
    }

    @Test
    public void findInElement_byId() {
        TestElementQuery<ExampleElement> query = createExampleElementQuery();
        try {
            query.id("the_id");
            Assert.fail("Search should fail as no element with the id exists");
        } catch (NoSuchElementException e) {
        }
        Assert.assertTrue("last query script contains ELEMENT_QUERY_FRAGMENT",
                query.lastScript.contains(ELEMENT_QUERY_FRAGMENT));
        Assert.assertTrue("last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[id=the_id]", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(exampleElement, query.lastContext);
    }

    @Test
    public void findInDocument_byId() {
        TestElementQuery<ExampleElement> query = createExampleDocumentQuery();
        try {
            query.id("the_id");
            Assert.fail("Search should fail as no element with the id exists");
        } catch (NoSuchElementException e) {
        }
        Assert.assertTrue("last query script contains DOCUMENT_QUERY_FRAGMENT",
                query.lastScript.contains(DOCUMENT_QUERY_FRAGMENT));
        Assert.assertTrue("last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[id=the_id]", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(null, query.lastContext);
    }

    @Test
    public void findInElement_byAttribute() {
        TestElementQuery<ExampleElement> query = createExampleElementQuery();
        try {
            query.attribute("foo", "bar").first();
            Assert.fail("Search should fail as no element with the id exists");
        } catch (NoSuchElementException e) {
        }
        Assert.assertTrue("last query script contains ELEMENT_QUERY_FRAGMENT",
                query.lastScript.contains(ELEMENT_QUERY_FRAGMENT));
        Assert.assertTrue("last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[foo=bar]", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(exampleElement, query.lastContext);
    }

    @Test
    public void findInDocument_byAttribute() {
        TestElementQuery<ExampleElement> query = createExampleDocumentQuery();
        try {
            query.attribute("foo", "bar").first();
            Assert.fail("Search should fail as no element with the id exists");
        } catch (NoSuchElementException e) {
        }
        Assert.assertTrue("last query script contains DOCUMENT_QUERY_FRAGMENT",
                query.lastScript.contains(DOCUMENT_QUERY_FRAGMENT));
        Assert.assertTrue("last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[foo=bar]", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(null, query.lastContext);
    }

    @Test
    public void findInElement_byAttributes() {
        TestElementQuery<ExampleElement> query = createExampleElementQuery();
        try {
            query.attribute("foo", "bar").attribute("das", "boot").first();
            Assert.fail("Search should fail as no element with the id exists");
        } catch (NoSuchElementException e) {
        }
        Assert.assertTrue("last query script contains ELEMENT_QUERY_FRAGMENT",
                query.lastScript.contains(ELEMENT_QUERY_FRAGMENT));
        Assert.assertTrue("last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[foo=bar][das=boot]", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(exampleElement, query.lastContext);
    }

    @Test
    public void findInDocument_byAttributes() {
        TestElementQuery<ExampleElement> query = createExampleDocumentQuery();
        try {
            query.attribute("foo", "bar").attribute("das", "boot").first();
            Assert.fail("Search should fail as no element with the id exists");
        } catch (NoSuchElementException e) {
        }
        Assert.assertTrue("last query script contains DOCUMENT_QUERY_FRAGMENT",
                query.lastScript.contains(DOCUMENT_QUERY_FRAGMENT));
        Assert.assertTrue("last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[foo=bar][das=boot]", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(null, query.lastContext);
    }

    @Test
    public void findInElement_byAttributesAndId() {
        TestElementQuery<ExampleElement> query = createExampleElementQuery();
        try {
            query.attribute("foo", "bar").attribute("das", "boot").id("theid");
            Assert.fail("Search should fail as no element with the id exists");
        } catch (NoSuchElementException e) {
        }
        Assert.assertTrue("last query script contains ELEMENT_QUERY_FRAGMENT",
                query.lastScript.contains(ELEMENT_QUERY_FRAGMENT));
        Assert.assertTrue("last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[foo=bar][das=boot][id=theid]",
                query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(exampleElement, query.lastContext);
    }

    @Test
    public void findInDocument_byAttributesAndId() {
        TestElementQuery<ExampleElement> query = createExampleDocumentQuery();
        try {
            query.attribute("foo", "bar").attribute("das", "boot").id("theid");
            Assert.fail("Search should fail as no element with the id exists");
        } catch (NoSuchElementException e) {
        }
        Assert.assertTrue("last query script contains DOCUMENT_QUERY_FRAGMENT",
                query.lastScript.contains(DOCUMENT_QUERY_FRAGMENT));
        Assert.assertTrue("last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[foo=bar][das=boot][id=theid]",
                query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(null, query.lastContext);
    }

    @Test
    public void findInElement_onPage() {
        TestElementQuery<ExampleElement> query = createExampleDocumentQuery();
        try {
            query.onPage().id("theid");
            Assert.fail("Search should fail as no element with the id exists");
        } catch (NoSuchElementException e) {
        }
        Assert.assertTrue("last query script contains DOCUMENT_QUERY_FRAGMENT",
                query.lastScript.contains(DOCUMENT_QUERY_FRAGMENT));
        Assert.assertTrue("last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[id=theid]", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(null, query.lastContext);
    }
}
