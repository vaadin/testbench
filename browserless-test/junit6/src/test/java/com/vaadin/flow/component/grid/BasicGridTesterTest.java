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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.RouteConfiguration;

@ViewPackages
class BasicGridTesterTest extends BrowserlessTest {

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
        // second column is hidden
        Assertions.assertEquals("46", test(view.basicGrid).getCellText(0, 1));

        Assertions.assertEquals("Maya", test(view.basicGrid).getCellText(1, 0));
        // second column is hidden
        Assertions.assertEquals("18", test(view.basicGrid).getCellText(1, 1));
    }

    @Test
    void basicGrid_selectionOnClick() {
        Assertions.assertTrue(test(view.basicGrid).getSelected().isEmpty());

        test(view.basicGrid).clickRow(0);
        Assertions.assertEquals(1, test(view.basicGrid).getSelected().size());
        Assertions.assertSame(view.person1,
                test(view.basicGrid).getSelected().iterator().next());

        test(view.basicGrid).clickRow(1);
        Assertions.assertEquals(1, test(view.basicGrid).getSelected().size());
        Assertions.assertSame(view.person2,
                test(view.basicGrid).getSelected().iterator().next());
    }

    @Test
    void basicGrid_deselectSelectedOnClick() {
        Assertions.assertTrue(test(view.basicGrid).getSelected().isEmpty());

        test(view.basicGrid).clickRow(0);
        Assertions.assertEquals(1, test(view.basicGrid).getSelected().size());
        Assertions.assertSame(view.person1,
                test(view.basicGrid).getSelected().iterator().next());

        test(view.basicGrid).clickRow(0);
        Assertions.assertTrue(test(view.basicGrid).getSelected().isEmpty(),
                "Clicking selected row should deselect");
    }

    @Test
    void basicGrid_selectWillChangeSelection() {
        test(view.basicGrid).select(1);
        Assertions.assertEquals(1, test(view.basicGrid).getSelected().size());
        Assertions.assertSame(view.person2,
                test(view.basicGrid).getSelected().iterator().next());

        test(view.basicGrid).select(0);
        Assertions.assertEquals(1, test(view.basicGrid).getSelected().size(),
                "Single select should only change selection.");
        Assertions.assertSame(view.person1,
                test(view.basicGrid).getSelected().iterator().next());
    }

    @Test
    void basicGrid_headerContent() {
        Assertions.assertEquals("First Name", test(view.basicGrid)
                .getColumn(BasicGridView.FIRST_NAME_KEY).getHeaderText());
        Assertions.assertEquals("Age", test(view.basicGrid)
                .getColumn(BasicGridView.AGE_KEY).getHeaderText());
        Assertions.assertEquals("Subscriber", test(view.basicGrid)
                .getColumn(BasicGridView.SUBSCRIBER_KEY).getHeaderText());
        Assertions.assertEquals("Deceased", test(view.basicGrid)
                .getColumn(BasicGridView.DECEASED_KEY).getHeaderText());
    }

    @Test
    void basicGrid_multiselect() {
        // This is not normally appropriate for a test, but we are testing
        // features.
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
        // This is not normally appropriate for a test, but we are testing
        // features.
        view.basicGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        test(view.basicGrid).selectAll();
        Assertions.assertSame(2, test(view.basicGrid).getSelected().size());
    }

    @Test
    void basicGrid_singleSelectThrowsForSelectAll() {
        GridTester<Grid<Person>, Person> grid_ = test(view.basicGrid);
        Assertions.assertThrows(IllegalStateException.class, grid_::selectAll,
                "Select all should throw for single select");
    }

    @Test
    void basicGrid_Hidden_getTextThrows() {
        view.basicGrid.setVisible(false);

        GridTester<Grid<Person>, Person> grid_ = test(view.basicGrid);

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

        test(view.basicGrid).clickRow(0);
        Assertions.assertEquals(0, doubleClicks.get(),
                "Click should not generate a double click event");

        test(view.basicGrid).doubleClickRow(0);
        Assertions.assertEquals(1, doubleClicks.get(),
                "Double click event should have fired");

    }

    @Test
    void getCellComponent_columnByKey_canClickAButton() {
        final Component cellComponent = test(view.basicGrid).getCellComponent(1,
                BasicGridView.BUTTON_KEY);
        Assertions.assertInstanceOf(Button.class, cellComponent);
        var button = (Button) cellComponent;
        test(button).click();
        var notification = $(Notification.class).last();
        Assertions.assertEquals("Clicked!", test(notification).getText());
    }

    @Test
    void getCellComponent_columnByKey_returnsInstantiatedComponent() {
        final Component cellComponent = test(view.basicGrid).getCellComponent(1,
                BasicGridView.SUBSCRIBER_KEY);
        Assertions.assertInstanceOf(CheckBox.class, cellComponent);
        Assertions.assertFalse(((CheckBox) cellComponent).isChecked());
    }

    @Test
    void getCellComponentByFaultyKey_throwsException() {
        GridTester<Grid<Person>, Person> grid_ = test(view.basicGrid);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> grid_.getCellComponent(1, "property"));
    }

    @Test
    void getCellComponent_columnByPosition_returnsInstantiatedComponent() {
        final Component cellComponent = test(view.basicGrid).getCellComponent(1,
                2);
        Assertions.assertInstanceOf(CheckBox.class, cellComponent);
        Assertions.assertFalse(((CheckBox) cellComponent).isChecked());
    }

    @Test
    void getCellComponent_columnByPosition_stringColumnThrows() {
        GridTester<Grid<Person>, Person> grid_ = test(view.basicGrid);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> grid_.getCellComponent(1, 1));
    }

    @Test
    void basicGrid_reorderColumns() {
        List<Grid.Column<Person>> columns = new ArrayList<>(
                view.basicGrid.getColumns());
        Collections.reverse(columns);
        view.basicGrid.setColumnOrder(columns);

        // person 1
        Assertions.assertEquals("Jorma",
                test(view.basicGrid).getCellText(0, 4));
        Assertions.assertEquals("46", test(view.basicGrid).getCellText(0, 3));

        // person 2
        Assertions.assertEquals("Maya", test(view.basicGrid).getCellText(1, 4));
        Assertions.assertEquals("18", test(view.basicGrid).getCellText(1, 3));
    }

    @Test
    void basicGrid_toggleLitRenderedColumn() {
        boolean deceased = test(view.basicGrid).getRow(0).getDeceased();

        Assertions.assertEquals(deceased,
                test(view.basicGrid).getLitRendererPropertyValue(0,
                        BasicGridView.DECEASED_KEY, "deceased", Boolean.class));

        test(view.basicGrid).invokeLitRendererFunction(0,
                BasicGridView.DECEASED_KEY, "onClick");

        Assertions.assertEquals(!deceased,
                test(view.basicGrid).getLitRendererPropertyValue(0,
                        BasicGridView.DECEASED_KEY, "deceased", Boolean.class));
    }

}
