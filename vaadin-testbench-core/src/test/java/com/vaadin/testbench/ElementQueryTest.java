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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ElementQueryTest {
    private static final String DOCUMENT_QUERY_FRAGMENT = "document.querySelectorAll(arguments[1]+arguments[2])";
    private static final String ELEMENT_QUERY_FRAGMENT = "arguments[0].shadowRoot.querySelectorAll(arguments[1]+arguments[2])";
    private static final String SINGLE_RESULT_QUERY_SUFFIX = ";return result";
    private static final String FIRST_RESULT_QUERY_SUFFIX = ";return result[0]";

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

    public abstract static class WebDriverWithJS
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

    public static class TestElementQuery<T extends TestBenchElement>
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

    private TestElementQuery<ExampleElement> createExampleElementQuery() {
        return new TestElementQuery<>(ExampleElement.class)
                .context(exampleElement);
    }

    private TestElementQuery<ExampleElement> createExampleDocumentQuery() {
        return new TestElementQuery<>(ExampleElement.class).context(mockDriver);
    }

    /**
     * Private utility method to make repetitive test code DRYer.
     *
     * @param queryFragment
     *          a fragment that the query script is expected to contain
     * @param query
     *          the element query of the test with its context set
     * @param action
     *          the actions to perform upon the element query
     * @param expectedAttributePairs
     *          the expected attribute pairs in the script as a result of executing the query
     * @param expectedResultQuerySuffix
     *          the expected suffix of the script as a result of executing the query
     * @param expectedLastContext
     *          the expected context of the query at the time of executing it
     * @param unexpectedlyFoundMessage
     *          a message to display if a matching element is unexpectedly found
     *          - pass {@code null} if elements are expected
     */
    private void findIn(String queryFragment,
                        TestElementQuery<ExampleElement> query,
                        Function<TestElementQuery<ExampleElement>, ?> action,
                        String expectedAttributePairs, String expectedResultQuerySuffix,
                        TestBenchElement expectedLastContext,
                        String unexpectedlyFoundMessage) {
        try {
            action.apply(query);
            if (unexpectedlyFoundMessage != null) {
                fail(unexpectedlyFoundMessage);
            }
        } catch (NoSuchElementException ignored) {
        }
        assertTrue("last query script should contain " +
                        (ELEMENT_QUERY_FRAGMENT.equals(queryFragment) ? "ELEMENT_QUERY_FRAGMENT" : "DOCUMENT_QUERY_FRAGMENT"),
                query.lastScript.contains(queryFragment));
        assertTrue(
                "last query script should end with \"" + expectedResultQuerySuffix + "\"",
                query.lastScript.endsWith(expectedResultQuerySuffix));
        assertEquals(expectedAttributePairs, query.lastAttributePairs);
        assertEquals(ExampleElement.TAG, query.lastTagName);
        assertSame(expectedLastContext, query.lastContext);
    }

    private void findInElement(TestElementQuery<ExampleElement> query,
                               Function<TestElementQuery<ExampleElement>, ?> action,
                               String expectedAttributePairs, String expectedResultQuerySuffix,
                               String unexpectedlyFoundMessage) {
        findIn(ELEMENT_QUERY_FRAGMENT, query, action, expectedAttributePairs, expectedResultQuerySuffix, exampleElement, unexpectedlyFoundMessage);
    }

    private void findSingleInElement(Function<TestElementQuery<ExampleElement>, ?> action,
                                     String expectedAttributePairs, String unexpectedlyFoundMessage) {
        findInElement(createExampleElementQuery(), action,
                expectedAttributePairs, SINGLE_RESULT_QUERY_SUFFIX, unexpectedlyFoundMessage);
    }

    private void findFirstInElement(Function<TestElementQuery<ExampleElement>, ?> action,
                                    String expectedAttributePairs, String unexpectedlyFoundMessage) {
        findInElement(createExampleElementQuery(), action,
                expectedAttributePairs, FIRST_RESULT_QUERY_SUFFIX, unexpectedlyFoundMessage);
    }

    private void findInDocument(TestElementQuery<ExampleElement> query,
                                Function<TestElementQuery<ExampleElement>, ?> action,
                                String expectedAttributePairs, String expectedResultQuerySuffix,
                                String unexpectedlyFoundMessage) {
        findIn(DOCUMENT_QUERY_FRAGMENT, query, action, expectedAttributePairs, expectedResultQuerySuffix, null, unexpectedlyFoundMessage);
    }

    private void findSingleInDocument(Function<TestElementQuery<ExampleElement>, ?> action,
                                      String expectedAttributePairs, String unexpectedlyFoundMessage) {
        findInDocument(createExampleDocumentQuery(), action,
                expectedAttributePairs, SINGLE_RESULT_QUERY_SUFFIX, unexpectedlyFoundMessage);
    }

    private void findFirstInDocument(Function<TestElementQuery<ExampleElement>, ?> action,
                                     String expectedAttributePairs, String unexpectedlyFoundMessage) {
        findInDocument(createExampleDocumentQuery(), action,
                expectedAttributePairs, FIRST_RESULT_QUERY_SUFFIX, unexpectedlyFoundMessage);
    }

    @Test
    public void findInElement_allElements() {
        findSingleInElement(ElementQuery::all,
                "",
                null);
    }

    @Test
    public void findInDocument_allElements() {
        findSingleInDocument(ElementQuery::all,
                "",
                null);
    }

    @Test
    public void findInElement_byHasAttribute() {
        findFirstInElement(query -> query
                        .withAttribute("nonexistent")
                        .first(),
                "[nonexistent]",
                "Search should fail as no element with the attribute exists in element");
    }

    @Test
    public void findInDocument_byHasAttribute() {
        findFirstInDocument(query -> query
                        .withAttribute("nonexistent")
                        .first(),
                "[nonexistent]",
                "Search should fail as no element with the attribute exists in document");
    }

    @Test
    public void findInElement_byWithAttribute() {
        findFirstInElement(query -> query
                        .withAttribute("foo", "bar")
                        .first(),
                "[foo='bar']",
                "Search should fail as no element with the attribute exists in element");
    }

    @Test
    public void findInDocument_byWithAttribute() {
        findFirstInDocument(query -> query
                        .withAttribute("foo", "bar")
                        .first(),
                "[foo='bar']",
                "Search should fail as no element with the attribute exists in document");
    }

    @Test
    public void findInElement_byWithAttributes() {
        findFirstInElement(query -> query
                        .withAttribute("foo", "bar")
                        .withAttribute("das", "boot")
                        .first(),
                "[foo='bar'][das='boot']",
                "Search should fail as no element with the attributes exist in element");
    }

    @Test
    public void findInDocument_byWithAttributes() {
        findFirstInDocument(query -> query
                        .withAttribute("foo", "bar")
                        .withAttribute("das", "boot")
                        .first(),
                "[foo='bar'][das='boot']",
                "Search should fail as no element with the attributes exist in document");
    }

    @Test
    public void findInElement_byWithAttributeContaining() {
        findFirstInElement(query -> query
                        .withAttributeContaining("foo", "bar")
                        .first(),
                "[foo~='bar']",
                "Search should fail as no element containing the attribute value exists in element");
    }

    @Test
    public void findInDocument_byWithAttributeContaining() {
        findFirstInDocument(query -> query
                        .withAttributeContaining("foo", "bar")
                        .first(),
                "[foo~='bar']",
                "Search should fail as no element containing the attribute value exists in document");
    }

    @Test
    public void findInElement_byWithoutHasAttribute() {
        findFirstInElement(query -> query
                        .withoutAttribute("nonexistent")
                        .first(),
                ":not([nonexistent])",
                null);
    }

    @Test
    public void findInDocument_byWithoutHasAttribute() {
        findFirstInDocument(query -> query
                        .withoutAttribute("nonexistent")
                        .first(),
                ":not([nonexistent])",
                null);
    }

    @Test
    public void findInElement_byWithoutAttribute() {
        findFirstInElement(query -> query
                        .withoutAttribute("foo", "bar")
                        .first(),
                ":not([foo='bar'])",
                null);
    }

    @Test
    public void findInDocument_byWithoutAttribute() {
        findFirstInDocument(query -> query
                        .withoutAttribute("foo", "bar")
                        .first(),
                ":not([foo='bar'])",
                null);
    }

    @Test
    public void findInElement_byWithoutAttributeContaining() {
        findFirstInElement(query -> query
                        .withoutAttributeContaining("foo", "bar")
                        .first(),
                ":not([foo~='bar'])",
                null);
    }

    @Test
    public void findInDocument_byWithoutAttributeContaining() {
        findFirstInDocument(query -> query
                        .withoutAttributeContaining("foo", "bar")
                        .first(),
                ":not([foo~='bar'])",
                null);
    }

    @Test
    public void findInElement_byId() {
        findSingleInElement(query -> query
                        .id("the_id"),
                "[id='the_id']",
                "Search should fail as no element with the id exists in element");
    }

    @Test
    public void findInDocument_byId() {
        findSingleInDocument(query -> query
                        .id("the_id"),
                "[id='the_id']",
                "Search should fail as no element with the id exists in document");
    }

    @Test
    public void findInElement_byWithId() {
        findSingleInElement(query -> query
                        .withId("the_id")
                        .single(),
                "[id='the_id']",
                "Search should fail as no element with the id exists in element");
    }

    @Test
    public void findInDocument_byWithId() {
        findSingleInDocument(query -> query
                        .withId("the_id")
                        .single(),
                "[id='the_id']",
                "Search should fail as no element with the id exists in document");
    }

    @Test
    public void findInElement_byWithAttributesAndId() {
        findSingleInElement(query -> query
                        .withAttribute("foo", "bar")
                        .withAttribute("das", "boot")
                        .withId("theid")
                        .single(),
                "[foo='bar'][das='boot'][id='theid']",
                "Search should fail as no element with the attributes and id exists in element");
    }

    @Test
    public void findInDocument_byWithAttributesAndId() {
        findSingleInDocument(query -> query
                        .withAttribute("foo", "bar")
                        .withAttribute("das", "boot")
                        .withId("theid")
                        .single(),
                "[foo='bar'][das='boot'][id='theid']",
                "Search should fail as no element with the attributes and id exists in document");
    }

    @Test
    public void findInElement_byWithClassName() {
        findFirstInElement(query -> query
                        .withClassName("pretty")
                        .first(),
                "[class~='pretty']",
                "Search should fail as no element with the class name exists in element");
    }

    @Test
    public void findInDocument_byWithClassName() {
        findFirstInDocument(query -> query
                        .withClassName("pretty")
                        .first(),
                "[class~='pretty']",
                "Search should fail as no element with the class name exists in document");
    }

    @Test
    public void findInElement_byWithClassNames() {
        findFirstInElement(query -> query
                        .withClassName("pretty")
                        .withClassName("ugly")
                        .first(),
                "[class~='pretty'][class~='ugly']",
                "Search should fail as no element with the class names exist in element");
    }

    @Test
    public void findInDocument_byWithClassNames() {
        findFirstInDocument(query -> query
                        .withClassName("pretty")
                        .withClassName("ugly")
                        .first(),
                "[class~='pretty'][class~='ugly']",
                "Search should fail as no element with the class names exist in document");
    }

    @Test
    public void findInElement_byWithoutClassName() {
        findFirstInElement(query -> query
                        .withoutClassName("pretty")
                        .first(),
                ":not([class~='pretty'])",
                null);
    }

    @Test
    public void findInDocument_byWithoutClassName() {
        findFirstInDocument(query -> query
                        .withoutClassName("pretty")
                        .first(),
                ":not([class~='pretty'])",
                null);
    }

    @Test
    public void findInElement_byWithoutClassNames() {
        findFirstInElement(query -> query
                        .withoutClassName("pretty")
                        .withoutClassName("ugly")
                        .first(),
                ":not([class~='pretty']):not([class~='ugly'])",
                null);
    }

    @Test
    public void findInDocument_byWithoutClassNames() {
        findFirstInDocument(query -> query
                        .withoutClassName("pretty")
                        .withoutClassName("ugly")
                        .first(),
                ":not([class~='pretty']):not([class~='ugly'])",
                null);
    }

    @Test
    public void findInElement_byWithTheme() {
        findFirstInElement(query -> query
                        .withTheme("compact")
                        .first(),
                "[theme='compact']",
                "Search should fail as no element with the theme exists in element");
    }

    @Test
    public void findInDocument_byWithTheme() {
        findFirstInDocument(query -> query
                        .withTheme("compact")
                        .first(),
                "[theme='compact']",
                "Search should fail as no element with the theme exists in document");
    }

    @Test
    public void findInElement_byWithoutTheme() {
        findFirstInElement(query -> query
                        .withoutTheme("compact")
                        .first(),
                ":not([theme='compact'])",
                null);
    }

    @Test
    public void findInDocument_byWithoutTheme() {
        findFirstInDocument(query -> query
                        .withoutTheme("compact")
                        .first(),
                ":not([theme='compact'])",
                null);
    }

    @Test
    public void findInElement_onPage() {
        findInDocument(createExampleElementQuery(),
                query -> query
                        .onPage()
                        .id("theid"),
                "[id='theid']",
                SINGLE_RESULT_QUERY_SUFFIX,
                "Search should fail as no element with the id exists in element on page");
    }

    @Test
    public void findInDocument_onPage() {
        findSingleInDocument(query -> query
                        .onPage()
                        .id("theid"),
                "[id='theid']",
                "Search should fail as no element with the id exists in document on page");
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
        assertEquals(set(new AttributeMatch("id", "my-fancy-view")),
                attributes);
    }

    @Test
    public void attributesConventionContains() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MyFancyViewContainsElement.class);
        assertEquals(set(
                        new AttributeMatch("class", "~=", "my-fancy-view-contains")),
                attributes);
    }

    @Test
    public void attributesInherited() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MyExtendedFancyViewElement.class);
        assertEquals(
                set(new AttributeMatch("id", "my-extended-fancy-view")),
                attributes);
    }

    @Test
    public void attributesCanBeOverridden() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MyExtendedAndOverriddenFancyViewElement.class);
        assertEquals(set(new AttributeMatch("id", "overruled")),
                attributes);
    }

    @Test
    public void multipleAttributeAnnotations() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MultipleAnnotationElement.class);
        assertEquals(set(new AttributeMatch("class", "~=", "foo"),
                new AttributeMatch("class", "~=", "bar")), attributes);
    }

    @SafeVarargs
    private <T> Set<T> set(T... ts) {
        return Set.of(ts);
    }
}
