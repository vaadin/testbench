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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElementQueryTest {
    private static final String DOCUMENT_QUERY_FRAGMENT = "document.querySelectorAll(arguments[1]+arguments[2])";
    private static final String ELEMENT_QUERY_FRAGMENT = "arguments[0].shadowRoot.querySelectorAll(arguments[1]+arguments[2])";
    private static final String SINGLE_RESULT_QUERY_SUFFIX = ";return result";
    private static final String FIRST_RESULT_QUERY_SUFFIX = ";return result[0]";

    private TestBenchDriverProxy mockDriver;
    private TestBenchElement exampleElement;
    private ExampleElement mockElement;

    @Element(ExampleElement.TAG)
    public static class ExampleElement extends TestBenchElement
            implements HasLabel, HasPlaceholder {
        public static final String TAG = "example-element";
    }

    public abstract static class WebDriverWithJS
            implements WebDriver, JavascriptExecutor {
    }

    @BeforeEach
    void setup() {
        mockDriver = TestBench
                .createDriver(Mockito.mock(WebDriverWithJS.class));
        exampleElement = TestBenchElement.wrapElement(
                Mockito.mock(WebElement.class),
                mockDriver.getCommandExecutor());

        mockElement = Mockito.mock(ExampleElement.class);
        Mockito.when(mockElement.getTagName()).thenReturn(ExampleElement.TAG);
        Mockito.when(mockElement.getLabel()).thenReturn("Example element label");
        Mockito.when(mockElement.getPlaceholder()).thenReturn("Example element placeholder");
        Mockito.when(mockElement.getText()).thenReturn("Example element text");
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
     * @param expectedPredicate
     *          a predicate that should evaluate to the same result as the query's conditions
     */
    private void findIn(String queryFragment,
                        TestElementQuery<ExampleElement> query,
                        Function<TestElementQuery<ExampleElement>, ?> action,
                        String expectedAttributePairs, String expectedResultQuerySuffix,
                        TestBenchElement expectedLastContext,
                        String unexpectedlyFoundMessage,
                        Predicate<ExampleElement> expectedPredicate) {
        try {
            // apply query selectors
            action.apply(query);

            // fail test if an exception was expected
            assertNull(unexpectedlyFoundMessage, unexpectedlyFoundMessage);
        } catch (NoSuchElementException ignored) {
        }

        // ensure query script contains expected fragment
        assertTrue(query.lastScript.contains(queryFragment),
                "query script should contain " +
                        (ELEMENT_QUERY_FRAGMENT.equals(queryFragment)
                                ? "ELEMENT_QUERY_FRAGMENT"
                                : "DOCUMENT_QUERY_FRAGMENT"));

        // ensure query script has correct indexing suffix
        assertTrue(
                query.lastScript.endsWith(expectedResultQuerySuffix),
                "query script should end with \"" + expectedResultQuerySuffix + "\"");

        // ensure the attribute pairs of the query match
        assertEquals(expectedAttributePairs, query.lastAttributePairs);

        // ensure the tag name of the query is correct
        assertEquals(ExampleElement.TAG, query.lastTagName);

        // ensure the context of the query is correct
        assertSame(expectedLastContext, query.lastContext);

        if (expectedPredicate != null) {
            // ensure query has conditions
            assertNotEquals(0, query.lastConditions.size());

            // ensure the query conditions evaluate to the same result as the expected predicate
            assertEquals(expectedPredicate.test(mockElement),
                    query.lastConditions.stream().allMatch(condition -> condition.test(mockElement)));
        }
    }

    private void findInElement(TestElementQuery<ExampleElement> query,
                               Function<TestElementQuery<ExampleElement>, ?> action,
                               String expectedAttributePairs, String expectedResultQuerySuffix,
                               String unexpectedlyFoundMessage,
                               Predicate<ExampleElement> expectedPredicate) {
        findIn(ELEMENT_QUERY_FRAGMENT, query, action, expectedAttributePairs, expectedResultQuerySuffix, exampleElement,
                unexpectedlyFoundMessage, expectedPredicate);
    }

    private void findSingleInElement(Function<TestElementQuery<ExampleElement>, ?> action,
                                     String expectedAttributePairs, String unexpectedlyFoundMessage) {
        findInElement(createExampleElementQuery(), action,
                expectedAttributePairs, SINGLE_RESULT_QUERY_SUFFIX, unexpectedlyFoundMessage, null);
    }

    private void findFirstInElement(Function<TestElementQuery<ExampleElement>, ?> action,
                                    String expectedAttributePairs, String unexpectedlyFoundMessage) {
        findInElement(createExampleElementQuery(), action,
                expectedAttributePairs, FIRST_RESULT_QUERY_SUFFIX, unexpectedlyFoundMessage, null);
    }

    private void findFirstConditionInElement(Function<TestElementQuery<ExampleElement>, ?> action,
                                             Predicate<ExampleElement> expectedPredicate) {
        findInElement(createExampleElementQuery(), action,
                "", FIRST_RESULT_QUERY_SUFFIX, null, expectedPredicate);
    }

    private void findInDocument(TestElementQuery<ExampleElement> query,
                                Function<TestElementQuery<ExampleElement>, ?> action,
                                String expectedAttributePairs, String expectedResultQuerySuffix,
                                String unexpectedlyFoundMessage,
                                Predicate<ExampleElement> expectedPredicate) {
        findIn(DOCUMENT_QUERY_FRAGMENT, query, action, expectedAttributePairs, expectedResultQuerySuffix, null,
                unexpectedlyFoundMessage, expectedPredicate);
    }

    private void findSingleInDocument(Function<TestElementQuery<ExampleElement>, ?> action,
                                      String expectedAttributePairs, String unexpectedlyFoundMessage) {
        findInDocument(createExampleDocumentQuery(), action,
                expectedAttributePairs, SINGLE_RESULT_QUERY_SUFFIX, unexpectedlyFoundMessage, null);
    }

    private void findFirstInDocument(Function<TestElementQuery<ExampleElement>, ?> action,
                                     String expectedAttributePairs, String unexpectedlyFoundMessage) {
        findInDocument(createExampleDocumentQuery(), action,
                expectedAttributePairs, FIRST_RESULT_QUERY_SUFFIX, unexpectedlyFoundMessage, null);
    }

    private void findFirstConditionInDocument(Function<TestElementQuery<ExampleElement>, ?> action,
                                              Predicate<ExampleElement> expectedPredicate) {
        findInDocument(createExampleDocumentQuery(), action,
                "", FIRST_RESULT_QUERY_SUFFIX, null, expectedPredicate);
    }

    @Test
    void findInElement_allElements() {
        findSingleInElement(ElementQuery::all,
                "",
                null);
    }

    @Test
    void findInDocument_allElements() {
        findSingleInDocument(ElementQuery::all,
                "",
                null);
    }

    @Test
    void findInElement_byHasAttribute() {
        findFirstInElement(query -> query
                        .withAttribute("nonexistent")
                        .first(),
                "[nonexistent]",
                "Search should fail as no element with the attribute exists in element");
    }

    @Test
    void findInDocument_byHasAttribute() {
        findFirstInDocument(query -> query
                        .withAttribute("nonexistent")
                        .first(),
                "[nonexistent]",
                "Search should fail as no element with the attribute exists in document");
    }

    @Test
    void findInElement_byWithAttribute() {
        findFirstInElement(query -> query
                        .withAttribute("foo", "bar")
                        .first(),
                "[foo='bar']",
                "Search should fail as no element with the attribute exists in element");
    }

    @Test
    void findInDocument_byWithAttribute() {
        findFirstInDocument(query -> query
                        .withAttribute("foo", "bar")
                        .first(),
                "[foo='bar']",
                "Search should fail as no element with the attribute exists in document");
    }

    @Test
    void findInElement_byWithAttributes() {
        findFirstInElement(query -> query
                        .withAttribute("foo", "bar")
                        .withAttribute("das", "boot")
                        .first(),
                "[foo='bar'][das='boot']",
                "Search should fail as no element with the attributes exist in element");
    }

    @Test
    void findInDocument_byWithAttributes() {
        findFirstInDocument(query -> query
                        .withAttribute("foo", "bar")
                        .withAttribute("das", "boot")
                        .first(),
                "[foo='bar'][das='boot']",
                "Search should fail as no element with the attributes exist in document");
    }

    @Test
    void findInElement_byWithAttributeContaining() {
        findFirstInElement(query -> query
                        .withAttributeContaining("foo", "bar")
                        .first(),
                "[foo~='bar']",
                "Search should fail as no element containing the attribute value exists in element");
    }

    @Test
    void findInDocument_byWithAttributeContaining() {
        findFirstInDocument(query -> query
                        .withAttributeContaining("foo", "bar")
                        .first(),
                "[foo~='bar']",
                "Search should fail as no element containing the attribute value exists in document");
    }

    @Test
    void findInElement_byWithoutHasAttribute() {
        findFirstInElement(query -> query
                        .withoutAttribute("nonexistent")
                        .first(),
                ":not([nonexistent])",
                null);
    }

    @Test
    void findInDocument_byWithoutHasAttribute() {
        findFirstInDocument(query -> query
                        .withoutAttribute("nonexistent")
                        .first(),
                ":not([nonexistent])",
                null);
    }

    @Test
    void findInElement_byWithoutAttribute() {
        findFirstInElement(query -> query
                        .withoutAttribute("foo", "bar")
                        .first(),
                ":not([foo='bar'])",
                null);
    }

    @Test
    void findInDocument_byWithoutAttribute() {
        findFirstInDocument(query -> query
                        .withoutAttribute("foo", "bar")
                        .first(),
                ":not([foo='bar'])",
                null);
    }

    @Test
    void findInElement_byWithoutAttributeContaining() {
        findFirstInElement(query -> query
                        .withoutAttributeContaining("foo", "bar")
                        .first(),
                ":not([foo~='bar'])",
                null);
    }

    @Test
    void findInDocument_byWithoutAttributeContaining() {
        findFirstInDocument(query -> query
                        .withoutAttributeContaining("foo", "bar")
                        .first(),
                ":not([foo~='bar'])",
                null);
    }

    @Test
    void findInElement_byId() {
        findSingleInElement(query -> query
                        .id("the_id"),
                "[id='the_id']",
                "Search should fail as no element with the id exists in element");
    }

    @Test
    void findInDocument_byId() {
        findSingleInDocument(query -> query
                        .id("the_id"),
                "[id='the_id']",
                "Search should fail as no element with the id exists in document");
    }

    @Test
    void findInElement_byWithId() {
        findSingleInElement(query -> query
                        .withId("the_id")
                        .single(),
                "[id='the_id']",
                "Search should fail as no element with the id exists in element");
    }

    @Test
    void findInDocument_byWithId() {
        findSingleInDocument(query -> query
                        .withId("the_id")
                        .single(),
                "[id='the_id']",
                "Search should fail as no element with the id exists in document");
    }

    @Test
    void findInElement_byWithAttributesAndId() {
        findSingleInElement(query -> query
                        .withAttribute("foo", "bar")
                        .withAttribute("das", "boot")
                        .withId("theid")
                        .single(),
                "[foo='bar'][das='boot'][id='theid']",
                "Search should fail as no element with the attributes and id exists in element");
    }

    @Test
    void findInDocument_byWithAttributesAndId() {
        findSingleInDocument(query -> query
                        .withAttribute("foo", "bar")
                        .withAttribute("das", "boot")
                        .withId("theid")
                        .single(),
                "[foo='bar'][das='boot'][id='theid']",
                "Search should fail as no element with the attributes and id exists in document");
    }

    @Test
    void findInElement_byWithClassName() {
        findFirstInElement(query -> query
                        .withClassName("pretty")
                        .first(),
                "[class~='pretty']",
                "Search should fail as no element with the class name exists in element");
    }

    @Test
    void findInDocument_byWithClassName() {
        findFirstInDocument(query -> query
                        .withClassName("pretty")
                        .first(),
                "[class~='pretty']",
                "Search should fail as no element with the class name exists in document");
    }

    @Test
    void findInElement_byWithClassNames() {
        findFirstInElement(query -> query
                        .withClassName("pretty")
                        .withClassName("ugly")
                        .first(),
                "[class~='pretty'][class~='ugly']",
                "Search should fail as no element with the class names exist in element");
    }

    @Test
    void findInDocument_byWithClassNames() {
        findFirstInDocument(query -> query
                        .withClassName("pretty")
                        .withClassName("ugly")
                        .first(),
                "[class~='pretty'][class~='ugly']",
                "Search should fail as no element with the class names exist in document");
    }

    @Test
    void findInElement_byWithoutClassName() {
        findFirstInElement(query -> query
                        .withoutClassName("pretty")
                        .first(),
                ":not([class~='pretty'])",
                null);
    }

    @Test
    void findInDocument_byWithoutClassName() {
        findFirstInDocument(query -> query
                        .withoutClassName("pretty")
                        .first(),
                ":not([class~='pretty'])",
                null);
    }

    @Test
    void findInElement_byWithoutClassNames() {
        findFirstInElement(query -> query
                        .withoutClassName("pretty")
                        .withoutClassName("ugly")
                        .first(),
                ":not([class~='pretty']):not([class~='ugly'])",
                null);
    }

    @Test
    void findInDocument_byWithoutClassNames() {
        findFirstInDocument(query -> query
                        .withoutClassName("pretty")
                        .withoutClassName("ugly")
                        .first(),
                ":not([class~='pretty']):not([class~='ugly'])",
                null);
    }

    @Test
    void findInElement_byWithTheme() {
        findFirstInElement(query -> query
                        .withTheme("compact")
                        .first(),
                "[theme='compact']",
                "Search should fail as no element with the theme exists in element");
    }

    @Test
    void findInDocument_byWithTheme() {
        findFirstInDocument(query -> query
                        .withTheme("compact")
                        .first(),
                "[theme='compact']",
                "Search should fail as no element with the theme exists in document");
    }

    @Test
    void findInElement_byWithoutTheme() {
        findFirstInElement(query -> query
                        .withoutTheme("compact")
                        .first(),
                ":not([theme='compact'])",
                null);
    }

    @Test
    void findInDocument_byWithoutTheme() {
        findFirstInDocument(query -> query
                        .withoutTheme("compact")
                        .first(),
                ":not([theme='compact'])",
                null);
    }

    @Test
    void findInElement_onPage() {
        findInDocument(createExampleElementQuery(),
                query -> query
                        .onPage()
                        .id("theid"),
                "[id='theid']",
                SINGLE_RESULT_QUERY_SUFFIX,
                "Search should fail as no element with the id exists in element on page",
                null);
    }

    @Test
    void findInDocument_onPage() {
        findSingleInDocument(query -> query
                        .onPage()
                        .id("theid"),
                "[id='theid']",
                "Search should fail as no element with the id exists in document on page");
    }

    @Test
    void findInElement_byCondition() {
        findFirstConditionInDocument(query -> query
                        .withCondition(element -> element.getTagName().equals(ExampleElement.TAG))
                        .first(),
                element -> element.getTagName().equals(ExampleElement.TAG));
    }

    @Test
    void findInDocument_byCondition() {
        findFirstConditionInElement(query -> query
                        .withCondition(element -> element.getTagName().equals(ExampleElement.TAG))
                        .first(),
                element -> element.getTagName().equals(ExampleElement.TAG));
    }

    @Test
    void findInElement_byPropertyValue() {
        findFirstConditionInElement(query -> query
                        .withPropertyValue(ExampleElement::getTagName, ExampleElement.TAG)
                        .first(),
                element -> element.getTagName().equals(ExampleElement.TAG));
    }

    @Test
    void findInDocument_byPropertyValue() {
        findFirstConditionInDocument(query -> query
                        .withPropertyValue(ExampleElement::getTagName, ExampleElement.TAG)
                        .first(),
                element -> element.getTagName().equals(ExampleElement.TAG));
    }

    @Test
    void findInElement_byLabel() {
        findFirstConditionInElement(query -> query
                        .withLabel("Example element label")
                        .first(),
                element -> element.getLabel().equals("Example element label"));
    }

    @Test
    void findInDocument_byLabel() {
        findFirstConditionInDocument(query -> query
                        .withLabel("Example element label")
                        .first(),
                element -> element.getLabel().equals("Example element label"));
    }

    @Test
    void findInElement_byContainsLabel() {
        findFirstConditionInElement(query -> query
                        .withLabelContaining("element")
                        .first(),
                element -> element.getLabel().contains("element"));
    }

    @Test
    void findInDocument_byContainsLabel() {
        findFirstConditionInDocument(query -> query
                        .withLabelContaining("element")
                        .first(),
                element -> element.getLabel().contains("element"));
    }

    @Test
    void findInElement_byPlaceholder() {
        findFirstConditionInElement(query -> query
                        .withPlaceholder("Example element placeholder")
                        .first(),
                element -> element.getPlaceholder().equals("Example element placeholder"));
    }

    @Test
    void findInDocument_byPlaceholder() {
        findFirstConditionInDocument(query -> query
                        .withPlaceholder("Example element placeholder")
                        .first(),
                element -> element.getPlaceholder().equals("Example element placeholder"));
    }

    @Test
    void findInElement_byContainsPlaceholder() {
        findFirstConditionInElement(query -> query
                        .withPlaceholderContaining("element")
                        .first(),
                element -> element.getPlaceholder().contains("element"));
    }

    @Test
    void findInDocument_byContainsPlaceholder() {
        findFirstConditionInDocument(query -> query
                        .withPlaceholderContaining("element")
                        .first(),
                element -> element.getPlaceholder().contains("element"));
    }

    @Test
    void findInElement_byCaption() {
        findFirstConditionInElement(query -> query
                        .withCaption("Example element caption")
                        .first(),
                element -> element.getLabel().equals("Example element caption"));
    }

    @Test
    void findInDocument_byCaption() {
        findFirstConditionInDocument(query -> query
                        .withCaption("Example element caption")
                        .first(),
                element -> element.getLabel().equals("Example element caption"));
    }

    @Test
    void findInElement_byContainsCaption() {
        findFirstConditionInElement(query -> query
                        .withCaptionContaining("element")
                        .first(),
                element -> element.getLabel().contains("element"));
    }

    @Test
    void findInDocument_byContainsCaption() {
        findFirstConditionInDocument(query -> query
                        .withCaptionContaining("element")
                        .first(),
                element -> element.getLabel().contains("element"));
    }

    @Test
    void findInElement_byText() {
        findFirstConditionInElement(query -> query
                        .withText("Example element text")
                        .first(),
                element -> element.getText().equals("Example element text"));
    }

    @Test
    void findInDocument_byText() {
        findFirstConditionInDocument(query -> query
                        .withText("Example element text")
                        .first(),
                element -> element.getText().equals("Example element text"));
    }

    @Test
    void findInElement_byContainsText() {
        findFirstConditionInElement(query -> query
                        .withTextContaining("element")
                        .first(),
                element -> element.getText().contains("element"));
    }

    @Test
    void findInDocument_byContainsText() {
        findFirstConditionInDocument(query -> query
                        .withTextContaining("element")
                        .first(),
                element -> element.getText().contains("element"));
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
    void attributesConventionValue() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MyFancyViewElement.class);
        assertEquals(set(new AttributeMatch("id", "my-fancy-view")),
                attributes);
    }

    @Test
    void attributesConventionContains() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MyFancyViewContainsElement.class);
        assertEquals(set(
                        new AttributeMatch("class", "~=", "my-fancy-view-contains")),
                attributes);
    }

    @Test
    void attributesInherited() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MyExtendedFancyViewElement.class);
        assertEquals(
                set(new AttributeMatch("id", "my-extended-fancy-view")),
                attributes);
    }

    @Test
    void attributesCanBeOverridden() {
        Set<AttributeMatch> attributes = ElementQuery
                .getAttributes(MyExtendedAndOverriddenFancyViewElement.class);
        assertEquals(set(new AttributeMatch("id", "overruled")),
                attributes);
    }

    @Test
    void multipleAttributeAnnotations() {
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
