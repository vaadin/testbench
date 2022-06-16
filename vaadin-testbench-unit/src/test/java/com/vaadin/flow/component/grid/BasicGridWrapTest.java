/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
public class BasicGridWrapTest extends UIUnitTest {

    BasicGridView view;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(BasicGridView.class);

        view = navigate(BasicGridView.class);
    }

    @Test
    void basicGrid_verifyColumnContent() {
        Assertions.assertEquals(2, test(view.basicGrid).size());

        Assertions.assertTrue(test(view.basicGrid).getSelected().isEmpty());

        Assertions.assertEquals("Jorma",
                test(view.basicGrid).getCellText(0, 0));
        Assertions.assertEquals("46", test(view.basicGrid).getCellText(0, 1));
        Assertions.assertEquals("Maya", test(view.basicGrid).getCellText(1, 0));
        Assertions.assertEquals("18", test(view.basicGrid).getCellText(1, 1));
    }

    @Test
    void basicGrid_selectionOnClick() {

        Assertions.assertTrue(test(view.basicGrid).getSelected().isEmpty());

        test(view.basicGrid).clickRow(0);

        Assertions.assertEquals(1, test(view.basicGrid).getSelected().size());
        Assertions.assertSame(view.first,
                test(view.basicGrid).getSelected().iterator().next());

        test(view.basicGrid).clickRow(1);

        Assertions.assertEquals(1, test(view.basicGrid).getSelected().size());
        Assertions.assertSame(view.second,
                test(view.basicGrid).getSelected().iterator().next());
    }

    @Test
    void basicGrid_deselectSelectedOnClick() {

        Assertions.assertTrue(test(view.basicGrid).getSelected().isEmpty());

        test(view.basicGrid).clickRow(0);

        Assertions.assertEquals(1, test(view.basicGrid).getSelected().size());
        Assertions.assertSame(view.first,
                test(view.basicGrid).getSelected().iterator().next());

        test(view.basicGrid).clickRow(0);

        Assertions.assertTrue(test(view.basicGrid).getSelected().isEmpty(),
                "Clicking selected row should deselect");
    }

    @Test
    void basicGrid_selectWillChangeSelection() {
        test(view.basicGrid).select(1);

        Assertions.assertEquals(1, test(view.basicGrid).getSelected().size());
        Assertions.assertSame(view.second,
                test(view.basicGrid).getSelected().iterator().next());

        test(view.basicGrid).select(0);
        Assertions.assertEquals(1, test(view.basicGrid).getSelected().size(),
                "Single select should only change selection.");
        Assertions.assertSame(view.first,
                test(view.basicGrid).getSelected().iterator().next());
    }

    @Test
    void basicGrid_headerContent() {
        Assertions.assertEquals(view.firstHeader,
                test(view.basicGrid).getHeaderCell(0));
        Assertions.assertEquals(view.secondHeader,
                test(view.basicGrid).getHeaderCell(1));
    }

    @Test
    void basicGrid_multiselect() {
        // This is not right for a test, but we are testing features.
        view.basicGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        test(view.basicGrid).clickRow(0);

        Assertions.assertTrue(test(view.basicGrid).getSelected().isEmpty(),
                "Multiselect doesn't select for row click!");

        test(view.basicGrid).select(0);
        test(view.basicGrid).select(1);

        Assertions.assertSame(2, test(view.basicGrid).getSelected().size());
    }

    @Test
    void basicGrid_multiselectAll() {
        // This is not right for a test, but we are testing features.
        view.basicGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        test(view.basicGrid).selectAll();

        Assertions.assertSame(2, test(view.basicGrid).getSelected().size());
    }

    @Test
    void basicGrid_singleSelectThrowsForSelectAll() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.basicGrid).selectAll(),
                "Select all should throw for single select");
    }

    @Test
    void basicGrid_Hidden_getTextThrows() {
        view.basicGrid.setVisible(false);

        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.basicGrid).getHeaderCell(0),
                "Header cell shouldn't be available for hidden grid");
        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.basicGrid).getCellText(0, 0),
                "Cell content shouldn't be available for hidden grid");
    }

    @Test
    void basicGrid_doubleClick() {
        AtomicInteger doubleClicks = new AtomicInteger(0);
        view.basicGrid.addItemDoubleClickListener(
                event -> doubleClicks.incrementAndGet());

        test(view.basicGrid).clickRow(0);

        Assertions.assertEquals(0, doubleClicks.get(),
                "Click should not generate a double click event");

        test(view.basicGrid).doubleClickRow(0);

        Assertions.assertEquals(1, doubleClicks.get(),
                "Double click event should have fired");

    }

    @Test
    void getCellComponent_columnByKey_returnsInstantiatedComponent() {
        final Component cellComponent = test(view.basicGrid).getCellComponent(1,
                view.subscriber);
        Assertions.assertTrue(cellComponent instanceof CheckBox);
        Assertions.assertFalse(((CheckBox) cellComponent).isChecked());
    }

    @Test
    void getCellComponentByFaultyKey_throwsException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> test(view.basicGrid).getCellComponent(1, "property"));
    }

    @Test
    void getCellComponent_columnByPosition_returnsInstantiatedComponent() {
        final Component cellComponent = test(view.basicGrid).getCellComponent(1,
                2);
        Assertions.assertTrue(cellComponent instanceof CheckBox);
        Assertions.assertFalse(((CheckBox) cellComponent).isChecked());
    }

    @Test
    void getCellComponent_columnByPosition_stringColumnThrows() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> test(view.basicGrid).getCellComponent(1, 1));
    }

    @Test
    void basicGrid_reorderColumns() {
        List<Grid.Column<Person>> columns = new ArrayList<>(
                view.basicGrid.getColumns());
        Collections.reverse(columns);
        view.basicGrid.setColumnOrder(columns);

        Assertions.assertEquals("Jorma",
                test(view.basicGrid).getCellText(0, 2));
        Assertions.assertEquals("46", test(view.basicGrid).getCellText(0, 1));
        Assertions.assertEquals("Maya", test(view.basicGrid).getCellText(1, 2));
        Assertions.assertEquals("18", test(view.basicGrid).getCellText(1, 1));
    }

}
