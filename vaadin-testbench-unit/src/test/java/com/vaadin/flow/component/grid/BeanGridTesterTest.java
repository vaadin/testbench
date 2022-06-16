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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
public class BeanGridWrapTest extends UIUnitTest {

    BeanGridView view;
    GridTester<Grid<Person>, Person> grid_;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(BeanGridView.class);

        view = navigate(BeanGridView.class);
        grid_ = test(view.beanGrid);
    }

    @Test
    void beanGrid_assertBeanColumns() {
        Assertions.assertEquals(2, grid_.size());

        Assertions.assertTrue(grid_.getSelected().isEmpty());

        final int firstName = grid_.getColumnPosition("firstName");
        final int age = grid_.getColumnPosition("age");

        Assertions.assertEquals(view.first.getFirstName(),
                grid_.getCellText(0, firstName));
        Assertions.assertEquals(Integer.toString(view.first.getAge()),
                grid_.getCellText(0, age));
        Assertions.assertEquals(view.first.getLastName(),
                grid_.getCellText(0, grid_.getColumnPosition("lastName")));
        Assertions.assertEquals(view.first.getEmail(),
                grid_.getCellText(0, grid_.getColumnPosition("email")));
        Assertions.assertEquals(view.first.getAddress().toString(),
                grid_.getCellText(0, grid_.getColumnPosition("address")));

        Assertions.assertEquals(view.second.getFirstName(),
                grid_.getCellText(1, firstName));
        Assertions.assertEquals(Integer.toString(view.second.getAge()),
                grid_.getCellText(1, age));
    }

}
