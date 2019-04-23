package com.vaadin.testbench.tests.component.ironlist;

import com.vaadin.flow.component.ironlist.testbench.IronListElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import com.vaadin.testbench.tests.ui.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.ironlist.testbench.test.IronListView.HUNDRED_THOUSAND;
import static com.vaadin.flow.component.ironlist.testbench.test.IronListView.NAV;

public class IronListIT extends AbstractIT {

    @VaadinTest(navigateTo = NAV)
    public void scrollTo(GenericTestPageObject po) throws Exception {
        final IronListElement def = po.$(IronListElement.class).id(HUNDRED_THOUSAND);

        def.scrollToRow(1000);
        Assertions.assertEquals(1000.0, def.getFirstVisibleRowIndex(), 2);
    }

    @VaadinTest(navigateTo = NAV)
    public void rowCount(GenericTestPageObject po) {
        final IronListElement def = po.$(IronListElement.class).id(HUNDRED_THOUSAND);

        Assertions.assertEquals(100000, def.getRowCount());
    }

    @VaadinTest(navigateTo = NAV)
    public void firstLastVisibleRow(GenericTestPageObject po) throws Exception {
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