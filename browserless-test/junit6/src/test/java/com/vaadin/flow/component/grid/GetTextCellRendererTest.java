/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
