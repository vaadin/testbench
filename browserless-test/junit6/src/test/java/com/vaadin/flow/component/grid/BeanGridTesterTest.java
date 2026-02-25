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

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.router.RouteConfiguration;

@ViewPackages
public class BeanGridTesterTest extends BrowserlessTest {

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
