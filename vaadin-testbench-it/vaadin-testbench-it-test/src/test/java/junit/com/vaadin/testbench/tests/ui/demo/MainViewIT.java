package junit.com.vaadin.testbench.tests.ui.demo;

import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

@VaadinTest
class MainViewIT {

    @DisplayName("Hello World - Click twice 001")
    @VaadinTest(preload = false)
    void test001(VaadinAppPageObject pageObject) {
        pageObject.loadPage();
        assertEquals(0, pageObject.clickCount());
        pageObject.click();
        assertEquals(1, pageObject.clickCount());
    }

    @DisplayName("Hello World - Click twice 002")
    @VaadinTest(navigateTo = "")
    void test002(VaadinAppPageObject pageObject) {
        assertEquals(0, pageObject.clickCount());
        pageObject.click();
        assertEquals(1, pageObject.clickCount());
    }

    @DisplayName("Hello World - Click twice 003")
    @VaadinTest
    void test003(VaadinAppPageObject pageObject) {
        assertEquals(0, pageObject.clickCount());
        pageObject.click();
        assertEquals(1, pageObject.clickCount());
    }

    @DisplayName("Hello World - Click twice 004")
    @VaadinTest
    void test004(VaadinAppPageObject pageObject) {
        assertEquals(0, pageObject.clickCount());
        pageObject.click();
        assertEquals(1, pageObject.clickCount());
    }
}
