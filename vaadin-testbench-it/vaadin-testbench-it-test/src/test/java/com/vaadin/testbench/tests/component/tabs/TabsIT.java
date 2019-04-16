package com.vaadin.testbench.tests.component.tabs;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.tabs.testbench.TabsElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.webdriver.SkipBrowsers;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import com.vaadin.testbench.tests.ui.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.tabs.testbench.test.TabsView.DEFAULT;
import static com.vaadin.flow.component.tabs.testbench.test.TabsView.NAV;
import static com.vaadin.testbench.addons.webdriver.BrowserTypes.FIREFOX;
import static com.vaadin.testbench.addons.webdriver.BrowserTypes.IE;

public class TabsIT extends AbstractIT {

    public static final String TEXT = "Text";
    public static final String DISABLED = "Disabled";

    @VaadinTest(navigateTo = NAV)
    public void selectTabByIndex(GenericTestPageObject po) throws Exception {
        final TabsElement def = po.$(TabsElement.class).id(DEFAULT);
        Assertions.assertEquals(0, def.getSelectedTabIndex());
        def.setSelectedTabIndex(2);
        Assertions.assertEquals(2, def.getSelectedTabIndex());
        def.setSelectedTabIndex(0);
        Assertions.assertEquals(0, def.getSelectedTabIndex());
    }

    /**
     * https://github.com/vaadin/vaadin-tabs-flow/issues/27
     *
     * @param po
     * @throws Exception
     */
    @VaadinTest(navigateTo = NAV)
    @SkipBrowsers(value = {FIREFOX, IE})
    public void getSelectedTabElement(GenericTestPageObject po) throws Exception {
        final TabsElement def = po.$(TabsElement.class).id(DEFAULT);

        def.getSelectedTabElement().$(ButtonElement.class).first().click();
        Assertions.assertEquals("2. Hello clicked", getLogRow(po, 0));
        def.setSelectedTabIndex(2);
        Assertions.assertEquals(TEXT, def.getSelectedTabElement().getText());
    }

    @VaadinTest(navigateTo = NAV)
    public void getTab(GenericTestPageObject po) throws Exception {
        final TabsElement def = po.$(TabsElement.class).id(DEFAULT);

        Assertions.assertEquals(1, def.getTab(DISABLED));
        Assertions.assertEquals(2, def.getTab(TEXT));
    }

    @VaadinTest(navigateTo = NAV)
    public void isEnabled(GenericTestPageObject po) throws Exception {
        final TabsElement def = po.$(TabsElement.class).id(DEFAULT);

        Assertions.assertTrue(def.getTabElement(TEXT).isEnabled());
        Assertions.assertFalse(def.getTabElement(DISABLED).isEnabled());

    }
}
