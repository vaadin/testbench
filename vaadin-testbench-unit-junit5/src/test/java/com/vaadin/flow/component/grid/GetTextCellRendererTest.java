/**
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
class GetTextCellRendererTest extends UIUnitTest {

    RendererGridView view;
    GridTester<Grid<Person>, Person> grid_;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(RendererGridView.class);

        view = navigate(RendererGridView.class);
        grid_ = test(view.grid);
    }

    @Test
    void getCellText_componentRenderer_getTextRecursively() {
        Assertions.assertEquals(
                String.format("%s%s%d", view.first.getFirstName(),
                        view.first.getLastName(), view.first.getAge()),
                grid_.getCellText(0, 0));
    }

    @Test
    void getCellText_renderNull_getsNull() {
        Assertions.assertNull(grid_.getCellText(0, 1));
    }

    @Test
    void getCellText_nonTextComponent_getsEmptyString() {
        Assertions.assertTrue(grid_.getCellText(0, 2).isEmpty());
    }

}
