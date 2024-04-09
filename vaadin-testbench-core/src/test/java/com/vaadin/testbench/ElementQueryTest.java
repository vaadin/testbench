/**
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import com.vaadin.testbench.ElementQuery.AttributeMatch;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class ElementQueryTest {

    private static final String DOCUMENT_QUERY_FRAGMENT = "document.querySelectorAll(arguments[1]+arguments[2])";
    private static final String ELEMENT_QUERY_FRAGMENT = "arguments[0].shadowRoot.querySelectorAll(arguments[1]+arguments[2])";
    private static final String SINGLE_RESULT_QUERY_SUFFIX = "[0]";
    private TestBenchDriverProxy mockDriver;
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
        mockDriver = TestBench
                .createDriver(Mockito.mock(WebDriverWithJS.class));
        exampleElement = TestBenchElement.wrapElement(
                Mockito.mock(WebElement.class),
                mockDriver.getCommandExecutor());

    }

    public class TestElementQuery<T extends TestBenchElement>
            extends ElementQuery<T> {
        private String lastScript;
        private Object lastContext;
        private String lastTagName;
        private Object lastAttributePairs;
        private final List<Predicate<T>> lastConditions;
        private boolean executed;

        public TestElementQuery(Class<T> elementClass) {
            super(elementClass);
            lastConditions = new ArrayList<>();
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
            return List.of();
        }

        @Override
        public ElementQuery<T> withCondition(Predicate<T> condition) {
            lastConditions.add(condition);
            return super.withCondition(condition);
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
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[id='the_id']", query.lastAttributePairs);
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
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[id='the_id']", query.lastAttributePairs);
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
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[foo='bar']", query.lastAttributePairs);
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
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[foo='bar']", query.lastAttributePairs);
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
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[foo='bar'][das='boot']",
                query.lastAttributePairs);
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
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[foo='bar'][das='boot']",
                query.lastAttributePairs);
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
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[foo='bar'][das='boot'][id='theid']",
                query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(exampleElement, query.lastContext);
    }

    @Test
    public void findInElement_byHasAttribute() {
        TestElementQuery<ExampleElement> query = createExampleElementQuery();
        try {
            query.hasAttribute("nonexistant").first();
            Assert.fail(
                    "Search should fail as no element with the attribute exists");
        } catch (NoSuchElementException e) {
        }
        Assert.assertTrue("last query script contains ELEMENT_QUERY_FRAGMENT",
                query.lastScript.contains(ELEMENT_QUERY_FRAGMENT));
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[nonexistant]", query.lastAttributePairs);
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
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[foo='bar'][das='boot'][id='theid']",
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
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("[id='theid']", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(null, query.lastContext);
    }

    private <T extends TestBenchElement>
    void assertPredicatesEqualFor(Predicate<T> expectedPredicate,
                                  Predicate<T> actualPredicate,
                                  T testBenchElement) {
        Assert.assertEquals(expectedPredicate.test(testBenchElement),
                actualPredicate.test(testBenchElement));
    }

    @Test
    public void findInElement_byCondition() {
        TestElementQuery<ExampleElement> query = createExampleElementQuery();
        query.withCondition(HasLabel.class::isInstance);
        try {
            query.first();
            Assert.fail("Search should fail as no element with the condition exists.");
        } catch (NoSuchElementException ignored) {
        }
        Assert.assertTrue("last query script contains ELEMENT_QUERY_FRAGMENT",
                query.lastScript.contains(ELEMENT_QUERY_FRAGMENT));
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(exampleElement, query.lastContext);
        Assert.assertEquals(1, query.lastConditions.size());
        assertPredicatesEqualFor(HasLabel.class::isInstance, query.lastConditions.get(0),
                new ExampleElement());
    }

    @Test
    public void findInDocument_byCondition() {
        TestElementQuery<ExampleElement> query = createExampleDocumentQuery();
        query.withCondition(HasLabel.class::isInstance);
        try {
            query.first();
            Assert.fail("Search should fail as no element with the condition exists.");
        } catch (NoSuchElementException ignored) {
        }
        Assert.assertTrue("last query script contains DOCUMENT_QUERY_FRAGMENT",
                query.lastScript.contains(DOCUMENT_QUERY_FRAGMENT));
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(null, query.lastContext);
        Assert.assertEquals(1, query.lastConditions.size());
        assertPredicatesEqualFor(HasLabel.class::isInstance, query.lastConditions.get(0),
                new ExampleElement());
    }

    @Test
    public void findInElement_byPropertyValue() {
        TestElementQuery<ExampleElement> query = createExampleElementQuery();
        query.withPropertyValue(ExampleElement::getTagName, ExampleElement.TAG);
        try {
            query.first();
        } catch (NoSuchElementException e) {
            Assert.fail("Search shouldn't fail as elements with the property value exist.");
        }
        Assert.assertTrue("last query script contains ELEMENT_QUERY_FRAGMENT",
                query.lastScript.contains(ELEMENT_QUERY_FRAGMENT));
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(exampleElement, query.lastContext);
        Assert.assertEquals(1, query.lastConditions.size());
        assertPredicatesEqualFor(HasLabel.class::isInstance, query.lastConditions.get(0),
                new ExampleElement());
    }

    @Test
    public void findInDocument_byPropertyValue() {
        TestElementQuery<ExampleElement> query = createExampleDocumentQuery();
        query.withPropertyValue(ExampleElement::getTagName, ExampleElement.TAG);
        try {
            query.first();
        } catch (NoSuchElementException e) {
            Assert.fail("Search shouldn't fail as elements with the property value exist.");
        }
        Assert.assertTrue("last query script contains DOCUMENT_QUERY_FRAGMENT",
                query.lastScript.contains(DOCUMENT_QUERY_FRAGMENT));
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(null, query.lastContext);
        Assert.assertEquals(1, query.lastConditions.size());
        assertPredicatesEqualFor(HasLabel.class::isInstance, query.lastConditions.get(0),
                new ExampleElement());
    }

    @Test
    public void findInElement_byLabel() {
        TestElementQuery<ExampleElement> query = createExampleElementQuery();
        query.withLabel("nonexistent");
        try {
            query.first();
            Assert.fail("Search should fail as no element with the label exists");
        } catch (NoSuchElementException ignored) {
        }
        Assert.assertTrue("last query script contains ELEMENT_QUERY_FRAGMENT",
                query.lastScript.contains(ELEMENT_QUERY_FRAGMENT));
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(exampleElement, query.lastContext);
        Assert.assertEquals(1, query.lastConditions.size());
        assertPredicatesEqualFor(element -> (element instanceof HasLabel hasLabel) &&
                        "nonexistent".equals(hasLabel.getLabel()),
                query.lastConditions.get(0),
                new ExampleElement());
    }

    @Test
    public void findInDocument_byLabel() {
        TestElementQuery<ExampleElement> query = createExampleDocumentQuery();
        query.withLabel("nonexistent");
        try {
            query.first();
            Assert.fail("Search should fail as no element with the label exists");
        } catch (NoSuchElementException ignored) {
        }
        Assert.assertTrue("last query script contains DOCUMENT_QUERY_FRAGMENT",
                query.lastScript.contains(DOCUMENT_QUERY_FRAGMENT));
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(null, query.lastContext);
        Assert.assertEquals(1, query.lastConditions.size());
        assertPredicatesEqualFor(element -> (element instanceof HasLabel hasLabel) &&
                        "nonexistent".equals(hasLabel.getLabel()),
                query.lastConditions.get(0),
                new ExampleElement());
    }

    @Test
    public void findInElement_byContainsLabel() {
        TestElementQuery<ExampleElement> query = createExampleElementQuery();
        query.withLabelContaining("nonexistent");
        try {
            query.first();
            Assert.fail("Search should fail as no element with the label exists");
        } catch (NoSuchElementException ignored) {
        }
        Assert.assertTrue("last query script contains ELEMENT_QUERY_FRAGMENT",
                query.lastScript.contains(ELEMENT_QUERY_FRAGMENT));
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(exampleElement, query.lastContext);
        Assert.assertEquals(1, query.lastConditions.size());
        assertPredicatesEqualFor(element -> (element instanceof HasLabel hasLabel) &&
                        Optional.ofNullable(hasLabel.getLabel()).filter(label -> label.contains("nonexistent")).isPresent(),
                query.lastConditions.get(0),
                new ExampleElement());
    }

    @Test
    public void findInDocument_byContainsLabel() {
        TestElementQuery<ExampleElement> query = createExampleDocumentQuery();
        query.withLabelContaining("nonexistent");
        try {
            query.first();
            Assert.fail("Search should fail as no element with the label exists");
        } catch (NoSuchElementException ignored) {
        }
        Assert.assertTrue("last query script contains DOCUMENT_QUERY_FRAGMENT",
                query.lastScript.contains(DOCUMENT_QUERY_FRAGMENT));
        Assert.assertTrue(
                "last query script end with SINGLE_RESULT_QUERY_SUFFIX",
                query.lastScript.endsWith(SINGLE_RESULT_QUERY_SUFFIX));
        Assert.assertEquals("", query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(null, query.lastContext);
        Assert.assertEquals(1, query.lastConditions.size());
        assertPredicatesEqualFor(element -> (element instanceof HasLabel hasLabel) &&
                        Optional.ofNullable(hasLabel.getLabel()).filter(label -> label.contains("nonexistent")).isPresent(),
                query.lastConditions.get(0),
                new ExampleElement());
    }

    @Attribute(name = "id", value = Attribute.SIMPLE_CLASS_NAME)
    public static class MyFancyViewElement extends TestBenchElement {
    }

    @Attribute(name = "class", contains = Attribute.SIMPLE_CLASS_NAME)
    public static class MyFancyViewContainsElement extends TestBenchElement {
    }

    public static class MyExtendedFancyViewElement extends MyFancyViewElement {
    }

    @Attribute(name = "class", contains = "foo")
    @Attribute(name = "class", contains = "bar")
    public static class MultipleAnnotationElement extends TestBenchElement {
    }

    @Attribute(name = "id", value = "overruled")
    public static class MyExtendedAndOverriddenFancyViewElement
            extends MyFancyViewElement {

    }

    @Test
    public void attributesConventionValue() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MyFancyViewElement.class);
        Assert.assertEquals(set(new AttributeMatch("id", "my-fancy-view")),
                attributes);
    }

    @Test
    public void attributesConventionContains() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MyFancyViewContainsElement.class);
        Assert.assertEquals(set(
                new AttributeMatch("class", "~=", "my-fancy-view-contains")),
                attributes);
    }

    @Test
    public void attributesInherited() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MyExtendedFancyViewElement.class);
        Assert.assertEquals(
                set(new AttributeMatch("id", "my-extended-fancy-view")),
                attributes);
    }

    @Test
    public void attributesCanBeOverridden() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MyExtendedAndOverriddenFancyViewElement.class);
        Assert.assertEquals(set(new AttributeMatch("id", "overruled")),
                attributes);
    }

    @Test
    public void multipleAttributeAnnotations() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MultipleAnnotationElement.class);
        Assert.assertEquals(set(new AttributeMatch("class", "~=", "foo"),
                new AttributeMatch("class", "~=", "bar")), attributes);
    }

    private <T> Set<T> set(T... ts) {
        HashSet<T> set = new HashSet<>();
        for (T t : ts) {
            set.add(t);
        }
        return set;
    }
}
