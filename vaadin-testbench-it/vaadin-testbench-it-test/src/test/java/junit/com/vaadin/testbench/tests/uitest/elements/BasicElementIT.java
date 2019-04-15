package junit.com.vaadin.testbench.tests.uitest.elements;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.tests.uitest.ElementQueryView;
import junit.com.vaadin.testbench.tests.uitest.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.TimeoutException;

@VaadinTest
class BasicElementIT {

    private static TestBenchElement fetch(GenericTestPageObject po) {
        po.loadPage(ElementQueryView.ROUTE);
        return po.$(NativeButtonElement.class).first();
    }

    @VaadinTest
    void getSetStringProperty(GenericTestPageObject po) {
        final TestBenchElement buttonElement = fetch(po);
        Assertions.assertNull(buttonElement.getPropertyString("foo"));
        buttonElement.setProperty("foo", "12");
        Assertions.assertEquals("12", buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(12.0, buttonElement.getPropertyDouble("foo"), 0);
        Assertions.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @VaadinTest
    void getSetBooleanProperty(GenericTestPageObject po) {
        final TestBenchElement buttonElement = fetch(po);
        Assertions.assertNull(buttonElement.getPropertyBoolean("foo"));
        buttonElement.setProperty("foo", true);
        Assertions.assertEquals("true", buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(1.0, buttonElement.getPropertyDouble("foo"), 0);
        Assertions.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @VaadinTest
    void getSetDoubleProperty(GenericTestPageObject po) {
        final TestBenchElement buttonElement = fetch(po);
        Assertions.assertNull(buttonElement.getPropertyDouble("foo"));
        buttonElement.setProperty("foo", 12.5);
        Assertions.assertEquals("12.5", buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(12.5, buttonElement.getPropertyDouble("foo"), 0);
        Assertions.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @VaadinTest
    void getSetIntegerProperty(GenericTestPageObject po) {
        final TestBenchElement buttonElement = fetch(po);
        Assertions.assertNull(buttonElement.getPropertyInteger("foo"));
        buttonElement.setProperty("foo", 12);
        Assertions.assertEquals("12", buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(12, buttonElement.getPropertyInteger("foo"), 0);
        Assertions.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @VaadinTest
    void getSetPropertyChain(GenericTestPageObject po) {
        final TestBenchElement buttonElement = fetch(po);
        po.getCommandExecutor().executeScript("arguments[0].foo = {bar: {baz: 123}};", buttonElement);

        Assertions.assertEquals(123L, buttonElement
                .getPropertyDouble("foo", "bar", "baz").longValue());
    }

    @VaadinTest
    void getSetElementProperty(GenericTestPageObject po) {
        final TestBenchElement buttonElement = fetch(po);
        Assertions.assertEquals(buttonElement, buttonElement
                .getPropertyElement("parentElement", "firstElementChild"));
        Assertions.assertNull(
                buttonElement.getPropertyElement("firstElementChild"));
    }

    @VaadinTest
    void getSetElementsProperty(GenericTestPageObject po) {
        final TestBenchElement buttonElement = fetch(po);
        Assertions.assertEquals(0,
                buttonElement.getPropertyElements("children").size());
        Assertions.assertEquals(1, buttonElement
                .getPropertyElements("parentElement", "children").size());
    }

    @VaadinTest
    void getSetPropertyChainMissingValue(GenericTestPageObject po) {
        final TestBenchElement buttonElement = fetch(po);
        po.getCommandExecutor().executeScript("arguments[0].foo = {bar: {baz: 123}};", buttonElement);
        Assertions.assertNull(buttonElement.getPropertyDouble("foo", "baz", "baz"));
    }

    @VaadinTest()
    void waitForNonExistant(GenericTestPageObject po) {
        fetch(po);
        Assertions.assertThrows(TimeoutException.class, () -> {
            po.$(PolymerTemplateViewElement.class).waitForFirst();
            Assertions.fail("Should not have found an element which does not exist");
        });
    }

    @VaadinTest
    void hasAttribute(GenericTestPageObject po) {
        fetch(po);
        NativeButtonElement withAttributes = po.$(NativeButtonElement.class)
                .get(5);
        NativeButtonElement withoutAttributes = po.$(NativeButtonElement.class)
                .get(6);

        Assertions.assertTrue(withAttributes.hasAttribute("string"));
        Assertions.assertTrue(withAttributes.hasAttribute("boolean"));
        Assertions.assertFalse(withAttributes.hasAttribute("nonexistant"));

        Assertions.assertFalse(withoutAttributes.hasAttribute("string"));
        Assertions.assertFalse(withoutAttributes.hasAttribute("boolean"));
        Assertions.assertFalse(withoutAttributes.hasAttribute("nonexistant"));
    }

    @VaadinTest
    void dispatchEvent(GenericTestPageObject po) {
        fetch(po);
        NativeButtonElement withAttributes = po.$(NativeButtonElement.class)
                .get(5);
        withAttributes.dispatchEvent("custom123");
        Assertions.assertEquals("Event on Button 5", po.$("div").id("msg").getText());
    }

    @VaadinTest
    void nativeButtonDisabled(GenericTestPageObject po) {
        fetch(po);
        NativeButtonElement enabled = po.$(NativeButtonElement.class).get(0);
        NativeButtonElement disabled = po.$(NativeButtonElement.class).get(2);
        Assertions.assertTrue(enabled.isEnabled());
        Assertions.assertFalse(disabled.isEnabled());
    }
}
