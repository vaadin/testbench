package com.vaadin.testbench.tests.component.ironlist;

import com.vaadin.flow.component.ironlist.testbench.IronListElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.junit5.pageobject.VaadinPageObject;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.ironlist.testbench.test.IronListView.HUNDRED_THOUSAND;
import static com.vaadin.flow.component.ironlist.testbench.test.IronListView.NAV;

@VaadinTest(navigateTo = NAV)
public class IronListIT extends AbstractIT {

    @VaadinTest
    public void scrollTo(VaadinPageObject po) {
        final IronListElement def = po.$(IronListElement.class).id(HUNDRED_THOUSAND);

        def.scrollToRow(1000);
        Assertions.assertEquals(1000.0, def.getFirstVisibleRowIndex(), 2);
    }

    @VaadinTest
    public void rowCount(VaadinPageObject po) {
        final IronListElement def = po.$(IronListElement.class).id(HUNDRED_THOUSAND);

        Assertions.assertEquals(100000, def.getRowCount());
    }

    @VaadinTest
    public void firstLastVisibleRow(VaadinPageObject po) {
        final IronListElement def = po.$(IronListElement.class).id(HUNDRED_THOUSAND);

        Assertions.assertEquals(0, def.getFirstVisibleRowIndex());
        Assertions.assertEquals(15, def.getLastVisibleRowIndex());
        Assertions.assertTrue(def.isRowInView(0));
        Assertions.assertTrue(def.isRowInView(5));
        Assertions.assertTrue(def.isRowInView(15));
        Assertions.assertFalse(def.isRowInView(105));

        def.scrollToRow(105);
        Assertions.assertEquals(105, def.getFirstVisibleRowIndex(), 2);
        Assertions.assertEquals(105 + 15, def.getLastVisibleRowIndex(), 2);
        Assertions.assertTrue(def.isRowInView(105));
        Assertions.assertFalse(def.isRowInView(0));
        Assertions.assertFalse(def.isRowInView(1000));
    }

}
