/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class GridTesterSortTest extends UIUnitTest {

    SortGridView view;
    GridTester<Grid<Person>, Person> grid_;
    GridTester<Grid<Person>, Person> beanGrid_;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(SortGridView.class);

        view = navigate(SortGridView.class);
        grid_ = test(view.grid);
        beanGrid_ = test(view.beanGrid);
    }

    @Test
    void isColumnSortableIndex_getColumnSortableState() {
        Assertions.assertTrue(grid_.isColumnSortable(0),
                "firstName colum should be sortable");
        Assertions.assertTrue(grid_.isColumnSortable(1),
                "age colum should be sortable");
        Assertions.assertFalse(grid_.isColumnSortable(2),
                "email colum should not be sortable");
    }

    @Test
    void isColumnSortableProperty_getColumnSortableState() {
        Assertions.assertTrue(beanGrid_.isColumnSortable("firstName"),
                "firstName colum should be sortable");
        Assertions.assertTrue(beanGrid_.isColumnSortable("age"),
                "age colum should be sortable");
        Assertions.assertFalse(beanGrid_.isColumnSortable("email"),
                "email colum should not be sortable");
    }

    @Test
    void isColumnSortable_invalidColumn_throws() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> grid_.isColumnSortable(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> grid_.isColumnSortable(10));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> grid_.isColumnSortable("email"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> beanGrid_.isColumnSortable("notAProperty"));
    }

    @Test
    void getSortDirectionIndex_sortableColum_getsDirection() {
        Assertions.assertNull(grid_.getSortDirection(0));

        grid_.getComponent().sort(GridSortOrder
                .desc(grid_.getComponent().getColumns().get(0)).build());
        Assertions.assertEquals(grid_.getSortDirection(0),
                SortDirection.DESCENDING);

        grid_.getComponent().sort(GridSortOrder
                .asc(grid_.getComponent().getColumns().get(0)).build());
        Assertions.assertEquals(grid_.getSortDirection(0),
                SortDirection.ASCENDING);
    }

    @Test
    void getSortDirectionOproperty_sortableColum_getsDirection() {
        Assertions.assertNull(beanGrid_.getSortDirection("firstName"));

        beanGrid_.getComponent()
                .sort(GridSortOrder.desc(
                        beanGrid_.getComponent().getColumnByKey("firstName"))
                        .build());
        Assertions.assertEquals(beanGrid_.getSortDirection("firstName"),
                SortDirection.DESCENDING);

        beanGrid_.getComponent()
                .sort(GridSortOrder.asc(
                        beanGrid_.getComponent().getColumnByKey("firstName"))
                        .build());
        Assertions.assertEquals(beanGrid_.getSortDirection("firstName"),
                SortDirection.ASCENDING);
    }

    @Test
    void getSortDirection_nonSortableColum_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> grid_.getSortDirection(2));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> beanGrid_.getSortDirection("email"));
    }

    @Test
    void getSortDirection_invalidColum_throws() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> grid_.getSortDirection(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> grid_.getSortDirection(10));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> grid_.getSortDirection("email"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> beanGrid_.getSortDirection("notAProperty"));
    }

    @Test
    void sortByColumnIndex_sortableColumn_gridIsSorted() {
        // Sort ASC
        grid_.sortByColumn(1);
        Assertions.assertIterableEquals(
                List.of(view.second, view.third, view.first),
                List.of(grid_.getRow(0), grid_.getRow(1), grid_.getRow(2)));

        // Sort DESC
        grid_.sortByColumn(1);
        Assertions.assertIterableEquals(
                List.of(view.first, view.third, view.second),
                List.of(grid_.getRow(0), grid_.getRow(1), grid_.getRow(2)));

        // Remove sort
        grid_.sortByColumn(1);
        Assertions.assertIterableEquals(
                List.of(view.first, view.second, view.third),
                List.of(grid_.getRow(0), grid_.getRow(1), grid_.getRow(2)));

    }

    @Test
    void sortByColumnIndexAndDirection_sortableColumn_gridIsSorted() {
        List<List<GridSortOrder<Person>>> sortEvents = new ArrayList<>();
        view.grid.addSortListener(ev -> sortEvents.add(ev.getSortOrder()));
        Grid.Column<Person> columnToSort = view.grid.getColumns().get(1);

        // Should click 2 times to get descending order
        grid_.sortByColumn(1, SortDirection.DESCENDING);
        Assertions.assertEquals(2, sortEvents.size());
        List<SortDirection> sorts = sortEvents.stream().flatMap(
                s -> s.stream().filter(x -> columnToSort == x.getSorted()))
                .map(SortOrder::getDirection).collect(Collectors.toList());

        Assertions.assertIterableEquals(
                List.of(SortDirection.ASCENDING, SortDirection.DESCENDING),
                sorts);

        Assertions.assertIterableEquals(
                List.of(view.first, view.third, view.second),
                List.of(grid_.getRow(0), grid_.getRow(1), grid_.getRow(2)));

    }

    @Test
    void sortByColumnProperty_sortableColumn_gridIsSorted() {
        beanGrid_.sortByColumn("age");
        Assertions.assertIterableEquals(
                List.of(view.second, view.third, view.first),
                List.of(beanGrid_.getRow(0), beanGrid_.getRow(1),
                        beanGrid_.getRow(2)));
        // Sort DESC
        beanGrid_.sortByColumn("age");
        Assertions.assertIterableEquals(
                List.of(view.first, view.third, view.second),
                List.of(beanGrid_.getRow(0), beanGrid_.getRow(1),
                        beanGrid_.getRow(2)));

        // Remove sort
        beanGrid_.sortByColumn("age");
        Assertions.assertIterableEquals(
                List.of(view.first, view.second, view.third),
                List.of(beanGrid_.getRow(0), beanGrid_.getRow(1),
                        beanGrid_.getRow(2)));
    }

    @Test
    void sortByColumnPropertyAndDirection_sortableColumn_gridIsSorted() {
        List<List<GridSortOrder<Person>>> sortEvents = new ArrayList<>();
        view.beanGrid.addSortListener(ev -> sortEvents.add(ev.getSortOrder()));
        Grid.Column<Person> columnToSort = view.beanGrid.getColumnByKey("age");

        // Should click 2 times to get descending order
        beanGrid_.sortByColumn("age", SortDirection.DESCENDING);
        Assertions.assertEquals(2, sortEvents.size());
        List<SortDirection> sorts = sortEvents.stream().flatMap(
                s -> s.stream().filter(x -> columnToSort == x.getSorted()))
                .map(SortOrder::getDirection).collect(Collectors.toList());

        Assertions.assertIterableEquals(
                List.of(view.first, view.third, view.second),
                List.of(beanGrid_.getRow(0), beanGrid_.getRow(1),
                        beanGrid_.getRow(2)));

        sortEvents.clear();
        beanGrid_.sortByColumn("age", SortDirection.DESCENDING);
        Assertions.assertTrue(sortEvents.isEmpty(),
                "Sort direction already reached, should do nothing");
    }

    @Test
    void sortByColumn_nonSortableColumn_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> grid_.sortByColumn(2));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> beanGrid_.sortByColumn("email"));
    }

    @Test
    void sortByColumn_multisort_gridIsSorted() {
        view.first.setFirstName("T");
        view.first.setAge(20);
        view.second.setFirstName("A");
        view.second.setAge(25);
        view.third.setFirstName("G");
        view.third.setAge(25);
        view.grid.setMultiSort(true);

        grid_.sortByColumn(0);
        grid_.sortByColumn(1, SortDirection.DESCENDING);
        Assertions.assertIterableEquals(
                List.of(view.second, view.third, view.first),
                List.of(grid_.getRow(0), grid_.getRow(1), grid_.getRow(2)));

    }
}
