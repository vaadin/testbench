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

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;

public class BasicGridWrapTest extends UIUnitTest {

    @Override
    protected String scanPackage() {
        return "com.vaadin.flow.component.grid";
    }

    BasicGridView view;
    GridWrap<Grid<Person>, Person> grid_;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(BasicGridView.class);

        view = navigate(BasicGridView.class);
        grid_ = wrap(view.basicGrid);
    }

    @Test
    void basicGrid_verifyColumnContent() {
        Assertions.assertEquals(2, grid_.size());

        Assertions.assertTrue(grid_.getSelected().isEmpty());

        Assertions.assertEquals("Jorma", grid_.getCellText(0, 0));
        Assertions.assertEquals("46", grid_.getCellText(0, 1));
        Assertions.assertEquals("Maya", grid_.getCellText(1, 0));
        Assertions.assertEquals("18", grid_.getCellText(1, 1));
    }

    @Test
    void basicGrid_selectionOnClick() {

        Assertions.assertTrue(grid_.getSelected().isEmpty());

        grid_.clickRow(0);

        Assertions.assertEquals(1, grid_.getSelected().size());
        Assertions.assertSame(view.first,
                grid_.getSelected().iterator().next());

        grid_.clickRow(1);

        Assertions.assertEquals(1, grid_.getSelected().size());
        Assertions.assertSame(view.second,
                grid_.getSelected().iterator().next());
    }

    @Test
    void basicGrid_deselectSelectedOnClick() {

        Assertions.assertTrue(grid_.getSelected().isEmpty());

        grid_.clickRow(0);

        Assertions.assertEquals(1, grid_.getSelected().size());
        Assertions.assertSame(view.first,
                grid_.getSelected().iterator().next());

        grid_.clickRow(0);

        Assertions.assertTrue(grid_.getSelected().isEmpty(),
                "Clicking selected row should deselect");
    }

    @Test
    void basicGrid_selectWillChangeSelection() {
        grid_.select(1);

        Assertions.assertEquals(1, grid_.getSelected().size());
        Assertions.assertSame(view.second,
                grid_.getSelected().iterator().next());

        grid_.select(0);
        Assertions.assertEquals(1, grid_.getSelected().size(),
                "Single select should only change selection.");
        Assertions.assertSame(view.first,
                grid_.getSelected().iterator().next());
    }

    @Test
    void basicGrid_headerContent() {
        Assertions.assertEquals(view.firstHeader, grid_.getHeaderCell(0));
        Assertions.assertEquals(view.secondHeader, grid_.getHeaderCell(1));
    }

    @Test
    void basicGrid_multiselect() {
        // This is not right for a test, but we are testing features.
        view.basicGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid_.clickRow(0);

        Assertions.assertTrue(grid_.getSelected().isEmpty(),
                "Multiselect doesn't select for row click!");

        grid_.select(0);
        grid_.select(1);

        Assertions.assertSame(2, grid_.getSelected().size());
    }

    @Test
    void basicGrid_multiselectAll() {
        // This is not right for a test, but we are testing features.
        view.basicGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid_.selectAll();

        Assertions.assertSame(2, grid_.getSelected().size());
    }

    @Test
    void basicGrid_singleSelectThrowsForSelectAll() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> grid_.selectAll(),
                "Select all should throw for single select");
    }

    @Test
    void basicGrid_Hidden_getTextThrows() {
        view.basicGrid.setVisible(false);

        Assertions.assertThrows(IllegalStateException.class,
                () -> grid_.getHeaderCell(0),
                "Header cell shouldn't be available for hidden grid");
        Assertions.assertThrows(IllegalStateException.class,
                () -> grid_.getCellText(0, 0),
                "Cell content shouldn't be available for hidden grid");
    }

    @Test
    void basicGrid_doubleClick() {
        AtomicInteger doubleClicks = new AtomicInteger(0);
        view.basicGrid.addItemDoubleClickListener(
                event -> doubleClicks.incrementAndGet());

        grid_.clickRow(0);

        Assertions.assertEquals(0, doubleClicks.get(),
                "Click should not generate a double click event");

        grid_.doubleClickRow(0);

        Assertions.assertEquals(1, doubleClicks.get(),
                "Double click event should have fired");

    }
}
