/**
 * Copyright (C) 2000-2022 Vaadin Ltd
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
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        private boolean executed;

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
            return new ArrayList<>();
        }
    }

    private TestElementQuery<ExampleElement> createExampleElementQuery() {
        return new TestElementQuery<>(ExampleElement.class)
                .context(exampleElement);
    }

    private TestElementQuery<ExampleElement> createExampleDocumentQuery() {
        return new TestElementQuery<>(ExampleElement.class).context(mockDriver);
    }

    private void findIn(String queryFragment,
                        TestElementQuery<ExampleElement> query,
                        Function<TestElementQuery<ExampleElement>, ?> action,
                        String expectedAttributePairs, String resultQuerySuffix,
                        TestBenchElement lastContext,
                        String message) {
        try {
            action.apply(query);
            if (message != null) {
                Assert.fail(message);
            }
        } catch (NoSuchElementException ignored) {
        }
        Assert.assertTrue("last query script should contain " +
                        (ELEMENT_QUERY_FRAGMENT.equals(queryFragment) ? "ELEMENT_QUERY_FRAGMENT" : "DOCUMENT_QUERY_FRAGMENT"),
                query.lastScript.contains(queryFragment));
        Assert.assertTrue(
                "last query script should end with \"" + resultQuerySuffix + "\"",
                query.lastScript.endsWith(resultQuerySuffix));
        Assert.assertEquals(expectedAttributePairs, query.lastAttributePairs);
        Assert.assertEquals(ExampleElement.TAG, query.lastTagName);
        Assert.assertSame(lastContext, query.lastContext);
    }

    private void findInElement(TestElementQuery<ExampleElement> query,
                               Function<TestElementQuery<ExampleElement>, ?> action,
                               String expectedAttributePairs, String resultQuerySuffix,
                               String message) {
        findIn(ELEMENT_QUERY_FRAGMENT, query, action, expectedAttributePairs, resultQuerySuffix, exampleElement, message);
    }

    private void findSingleInElement(Function<TestElementQuery<ExampleElement>, ?> action,
                                     String expectedAttributePairs, String message) {
        findInElement(createExampleElementQuery(), action,
                expectedAttributePairs, SINGLE_RESULT_QUERY_SUFFIX, message);
    }

    private void findFirstInElement(Function<TestElementQuery<ExampleElement>, ?> action,
                                    String expectedAttributePairs, String message) {
        findInElement(createExampleElementQuery(), action,
                expectedAttributePairs, FIRST_RESULT_QUERY_SUFFIX, message);
    }

    private void findInDocument(TestElementQuery<ExampleElement> query,
                                Function<TestElementQuery<ExampleElement>, ?> action,
                                String expectedAttributePairs, String resultQuerySuffix,
                                String message) {
        findIn(DOCUMENT_QUERY_FRAGMENT, query, action, expectedAttributePairs, resultQuerySuffix, null, message);
    }

    private void findSingleInDocument(Function<TestElementQuery<ExampleElement>, ?> action,
                                      String expectedAttributePairs, String message) {
        findInDocument(createExampleDocumentQuery(), action,
                expectedAttributePairs, SINGLE_RESULT_QUERY_SUFFIX, message);
    }

    private void findFirstInDocument(Function<TestElementQuery<ExampleElement>, ?> action,
                                     String expectedAttributePairs, String message) {
        findInDocument(createExampleDocumentQuery(), action,
                expectedAttributePairs, FIRST_RESULT_QUERY_SUFFIX, message);
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
                        .first(), "[nonexistent]",
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
    public void findInElement_byWithoutAttribute() {
        findFirstInElement(query -> query
                        .withoutAttribute("foo", "bar")
                        .first(),
                ":not([foo='bar'])",
                "Search should fail as no element with the attribute exists in element");
    }

    @Test
    public void findInDocument_byWithoutAttribute() {
        findFirstInDocument(query -> query
                        .withoutAttribute("foo", "bar")
                        .first(),
                ":not([foo='bar'])",
                "Search should fail as no element with the attribute exists in document");
    }

    @Test
    public void findInElement_byId() {
        findSingleInElement(query -> query.id("the_id"),
                "[id='the_id']",
                "Search should fail as no element with the id exists in element");
    }

    @Test
    public void findInDocument_byId() {
        findSingleInDocument(query -> query.id("the_id"),
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

    @SafeVarargs
    private <T> Set<T> set(T... ts) {
        return Arrays.stream(ts).collect(Collectors.toSet());
    }
}
