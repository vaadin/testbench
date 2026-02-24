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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import tools.jackson.databind.node.ArrayNode;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.internal.JacksonUtils;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.LitRendererTestUtil;
import com.vaadin.testbench.unit.MetaKeys;
import com.vaadin.testbench.unit.MouseButton;
import com.vaadin.testbench.unit.Tests;
import com.vaadin.testbench.unit.component.GridKt;

/**
 * Tester for Grid components.
 *
 * @param <T>
 *            component type
 * @param <Y>
 *            item type
 */
@Tests(fqn = { "com.vaadin.flow.component.grid.Grid" })
public class GridTester<T extends Grid<Y>, Y> extends ComponentTester<T> {
    /**
     * Wrap grid for testing.
     *
     * @param component
     *            target grid
     */
    public GridTester(T component) {
        super(component);
    }

    /**
     * Get the amount of items in the grid.
     *
     * @return items in grid
     */
    public int size() {
        return GridKt._size(getComponent());
    }

    /**
     * Get the item at the given row index.
     * <p/>
     * The index is 0 based.
     *
     * @param row
     *            row index of to get
     * @return grid item on row
     */
    public Y getRow(int row) {
        return GridKt._get(getComponent(), row);
    }

    /**
     * Click on grid row.
     * <p/>
     * The index is 0 based.
     *
     * @param row
     *            row to click
     */
    public void clickRow(int row) {
        clickRow(row, MouseButton.LEFT);
    }

    /**
     * Click on grid row with given button.
     * <p/>
     * The index is 0 based.
     *
     * @param row
     *            row to click
     * @param button
     *            MouseButton that was clicked
     * @see com.vaadin.flow.component.ClickEvent#getButton()
     */
    public void clickRow(int row, MouseButton button) {
        clickRow(row, button, new MetaKeys());
    }

    /**
     * Click on grid row with given meta keys pressed.
     * <p/>
     * The index is 0 based.
     *
     * @param row
     *            row to click
     * @param metaKeys
     *            meta key statuses for click
     */
    public void clickRow(int row, MetaKeys metaKeys) {
        clickRow(row, MouseButton.LEFT, metaKeys);
    }

    /**
     * Click on grid row with given button and meta keys pressed.
     * <p/>
     * The index is 0 based.
     *
     * @param row
     *            row to click
     * @param button
     *            MouseButton that was clicked
     * @param metaKeys
     *            meta key statuses for click
     * @see com.vaadin.flow.component.ClickEvent#getButton()
     */
    public void clickRow(int row, MouseButton button, MetaKeys metaKeys) {
        ensureComponentIsUsable();
        GridKt._clickItem(getComponent(), row, button.getButton(),
                metaKeys.isCtrl(), metaKeys.isShift(), metaKeys.isAlt(),
                metaKeys.isMeta());
    }

    /**
     * Double-click on grid row.
     * <p/>
     * The index is 0 based.
     *
     * @param row
     *            row to click
     */
    public void doubleClickRow(int row) {
        doubleClickRow(row, MouseButton.LEFT);
    }

    /**
     * Double-click on grid row with given button.
     * <p/>
     * The index is 0 based.
     *
     * @param row
     *            row to click
     * @param button
     *            MouseButton that was clicked
     * @see com.vaadin.flow.component.ClickEvent#getButton()
     */
    public void doubleClickRow(int row, MouseButton button) {
        doubleClickRow(row, button, new MetaKeys());
    }

    /**
     * Double-click on grid row with given meta keys pressed.
     * <p/>
     * The index is 0 based.
     *
     * @param row
     *            row to click
     * @param metaKeys
     *            meta key statuses for click
     */
    public void doubleClickRow(int row, MetaKeys metaKeys) {
        doubleClickRow(row, MouseButton.LEFT, metaKeys);
    }

    /**
     * Double-click on grid row with given button and meta keys pressed.
     * <p/>
     * The index is 0 based.
     *
     * @param row
     *            row to click
     * @param button
     *            MouseButton that was clicked
     * @param metaKeys
     *            meta key statuses for click
     * @see com.vaadin.flow.component.ClickEvent#getButton()
     */
    public void doubleClickRow(int row, MouseButton button, MetaKeys metaKeys) {
        ensureComponentIsUsable();
        GridKt._doubleClickItem(getComponent(), row, button.getButton(),
                metaKeys.isCtrl(), metaKeys.isShift(), metaKeys.isAlt(),
                metaKeys.isMeta());
    }

    /**
     * Select the item on given row.
     * <p/>
     * The index is 0 based.
     * <p/>
     * Single select will clear any old selections. Multi select will add to
     * selection.
     *
     * @param row
     *            row to select
     * @throws IllegalStateException
     *             if not usable
     */
    public void select(int row) {
        ensureComponentIsUsable();
        final Y item = getRow(row);
        GridKt._select(getComponent(), item);
    }

    /**
     * Select all items in grid.
     * <p/>
     * Only works for multi select.
     *
     * @throws IllegalStateException
     *             if not usable or not multi select
     */
    public void selectAll() {
        ensureComponentIsUsable();
        GridKt._selectAll(getComponent());
    }

    /**
     * Get the text that is shown on the client for the cell in the given
     * position.
     * <p/>
     * The indexes for row and column are 0 based.
     * <p/>
     * For the default renderer ColumnPathRenderer the result is the sent text
     * for defined object path.
     * <p/>
     * For a ComponentRenderer the result is the rendered component as
     * prettyString.
     * <p/>
     * More to be added as we find other renderers that need handling.
     *
     * @param row
     *            row of cell
     * @param column
     *            column of cell
     * @return cell content that is sent to the client
     * @throws IllegalStateException
     *             if component is not visible
     */
    public String getCellText(int row, int column) {
        ensureVisible();
        final Grid.Column<Y> targetColumn = getColumns().get(column);
        if (targetColumn.getRenderer() instanceof ComponentRenderer) {
            Component component = getCellComponent(row, column);
            if (component == null) {
                return null;
            }
            return component.getElement().getTextRecursively();
        } else if (targetColumn.getRenderer() instanceof ColumnPathRenderer) {
            // This renderer just writes the object text using a path
            return getValueProviderString(row, targetColumn);
        }
        return null;
    }

    /**
     * Get component for item in cell.
     *
     * @param row
     *            item row
     * @param column
     *            column to get
     * @return initialized component for the targeted cell
     * @throws IllegalArgumentException
     *             when the target column of the cell is not a component
     *             renderer
     */
    public Component getCellComponent(int row, int column) {
        ensureVisible();
        final Grid.Column<Y> yColumn = getColumns().get(column);
        return getRendererItem(row, yColumn);
    }

    /**
     * Get component for item in column.
     *
     * @param row
     *            item row
     * @param columnName
     *            key/property of column
     * @return initialized component for the target cell
     * @throws IllegalArgumentException
     *             when column for property doesn't exist or the target column
     *             of the cell is not a component renderer
     */
    public Component getCellComponent(int row, String columnName) {
        ensureVisible();
        if (getComponent().getColumnByKey(columnName) == null) {
            throw new IllegalArgumentException(
                    "No column for property '" + columnName + "' exists");
        }

        final Grid.Column<Y> yColumn = getComponent()
                .getColumnByKey(columnName);
        return getRendererItem(row, yColumn);
    }

    private Component getRendererItem(int row, Grid.Column<Y> yColumn) {
        if (yColumn.getRenderer() instanceof ComponentRenderer) {
            final Y item = getRow(row);
            var component = ((ComponentRenderer<?, Y>) yColumn.getRenderer())
                    .createComponent(item);
            if (component != null) {
                getComponent().getElement().appendChild(component.getElement());
            }
            return component;
        }
        throw new IllegalArgumentException(
                "Target column doesn't have a ComponentRenderer.");
    }

    private <V> V getLitRendererPropertyValue(int row, Grid.Column<Y> column,
            String propertyName, Class<V> propertyClass) {
        ensureVisible();

        if (column.getRenderer() instanceof LitRenderer<Y> litRenderer) {
            return LitRendererTestUtil.getPropertyValue(litRenderer,
                    this::getField, this::getRow, row, propertyName,
                    propertyClass);
        } else {
            throw new IllegalArgumentException(
                    "Target column doesn't have a LitRenderer.");
        }
    }

    /**
     * Get property value for item's LitRenderer in column.
     *
     * @param row
     *            item row
     * @param columnName
     *            key/property of column
     * @param propertyName
     *            the name of the LitRenderer property
     * @param propertyClass
     *            the class of the value of the LitRenderer property
     * @param <V>
     *            the type of the LitRenderer property
     * @return value of renderer's property for the target cell
     * @throws IllegalArgumentException
     *             when column for property doesn't exist or the target column
     *             of the cell is not a LitRenderer or when the given type of
     *             the property does not match the actual property type
     */
    public <V> V getLitRendererPropertyValue(int row, String columnName,
            String propertyName, Class<V> propertyClass) {
        return getLitRendererPropertyValue(row, getColumn(columnName),
                propertyName, propertyClass);
    }

    /**
     * Get property value for item's LitRenderer in column.
     *
     * @param row
     *            item row
     * @param column
     *            column to get
     * @param propertyName
     *            the name of the LitRenderer property
     * @param propertyClass
     *            the class of the value of the LitRenderer property
     * @param <V>
     *            the type of the LitRenderer property
     * @return value of renderer's property for the target cell
     * @throws IllegalArgumentException
     *             when column for property doesn't exist or the target column
     *             of the cell is not a LitRenderer or when the given type of
     *             the property does not match the actual property type
     */
    public <V> V getLitRendererPropertyValue(int row, int column,
            String propertyName, Class<V> propertyClass) {
        return getLitRendererPropertyValue(row, getColumns().get(column),
                propertyName, propertyClass);
    }

    private void invokeLitRendererFunction(int row, Grid.Column<Y> column,
            String functionName, ArrayNode jsonArray) {
        ensureVisible();

        if (column.getRenderer() instanceof LitRenderer<Y> litRenderer) {
            LitRendererTestUtil.invokeFunction(litRenderer, this::getField,
                    this::getRow, row, functionName, jsonArray);
        } else {
            throw new IllegalArgumentException(
                    "Target column doesn't have a LitRenderer.");
        }
    }

    /**
     * Invoke named function for item's LitRenderer in column using the supplied
     * JSON arguments.
     *
     * @param row
     *            item row
     * @param columnName
     *            key/property of column
     * @param functionName
     *            the name of the LitRenderer function to invoke
     * @param jsonArray
     *            the arguments to pass to the function
     */
    public void invokeLitRendererFunction(int row, String columnName,
            String functionName, ArrayNode jsonArray) {
        invokeLitRendererFunction(row, getColumn(columnName), functionName,
                jsonArray);
    }

    /**
     * Invoke named function for item's LitRenderer in column.
     *
     * @param row
     *            item row
     * @param columnName
     *            key/property of column
     * @param functionName
     *            the name of the LitRenderer function to invoke
     */
    public void invokeLitRendererFunction(int row, String columnName,
            String functionName) {
        invokeLitRendererFunction(row, columnName, functionName,
                JacksonUtils.createArrayNode());
    }

    /**
     * Invoke named function for item's LitRenderer in column using the supplied
     * JSON arguments.
     *
     * @param row
     *            item row
     * @param column
     *            column to get
     * @param functionName
     *            the name of the LitRenderer function to invoke
     * @param jsonArray
     *            the arguments to pass to the function
     */
    public void invokeLitRendererFunction(int row, int column,
            String functionName, ArrayNode jsonArray) {
        invokeLitRendererFunction(row, getColumns().get(column), functionName,
                jsonArray);
    }

    /**
     * Invoke named function for item's LitRenderer in column.
     *
     * @param row
     *            item row
     * @param column
     *            column to get
     * @param functionName
     *            the name of the LitRenderer function to invoke
     */
    public void invokeLitRendererFunction(int row, int column,
            String functionName) {
        invokeLitRendererFunction(row, column, functionName,
                JacksonUtils.createArrayNode());
    }

    /**
     * Get content in header for given column.
     *
     * @param column
     *            column to get header for
     * @return header contents
     * @throws IllegalStateException
     *             if component is not visible
     *
     * @deprecated Use {@link Grid.Column#getHeaderText()} or
     *             {@link Grid.Column#getHeaderComponent()}
     */
    @Deprecated
    public String getHeaderCell(int column) {
        ensureVisible();
        return getColumns().get(column).getHeaderText();
    }

    private List<Grid.Column<Y>> getColumns() {
        return getComponent().getColumns().stream().filter(Component::isVisible)
                .toList();
    }

    /**
     * Get the column position by column property.
     *
     * @param property
     *            the property name of the column, not null
     * @return int position of column
     */
    public int getColumnPosition(String property) {
        Objects.requireNonNull(property, "property name must not be null");
        return getColumns().indexOf(getColumn(property));
    }

    /**
     * Gets the grid column by column property.
     *
     * @param property
     *            the property name of the column, not null
     * @return Grid.Column for property
     */
    public Grid.Column<Y> getColumn(String property) {
        Objects.requireNonNull(property, "property name must not be null");
        return getComponent().getColumnByKey(property);
    }

    /**
     * Get content in footer for given column.
     *
     * @param column
     *            column to get footer for
     * @return footer contents
     * @throws IllegalStateException
     *             if component is not visible
     * @deprecated Use {@link Grid.Column#getFooterText()} or
     *             {@link Grid.Column#getFooterComponent()} directly
     */
    @Deprecated(forRemoval = true)
    public String getFooterCell(int column) {
        ensureVisible();
        return getColumns().get(column).getFooterText();
    }

    /**
     * Get selected items.
     *
     * @return selected items
     */
    public Collection<Y> getSelected() {
        ensureComponentIsUsable();
        return getComponent().getSelectedItems();
    }

    /**
     * Checks if the column at the given index is sortable.
     * <p/>
     * The index is 0 based.
     *
     * @param column
     *            column index to check for sort feature
     * @return {@literal true} if the column is sortable, otherwise
     *         {@literal false}
     * @throws IndexOutOfBoundsException
     *             if column index is invalid
     */
    public boolean isColumnSortable(int column) {
        return getColumns().get(column).isSortable();
    }

    /**
     * Checks if the column for the given property is sortable.
     *
     * @param property
     *            the property name of the column, not null
     * @return {@literal true} if the column is sortable, otherwise
     *         {@literal false}
     * @throws IllegalArgumentException
     *             if property name does not identify a column
     */
    public boolean isColumnSortable(String property) {
        Grid.Column<Y> column = getColumn(property);
        if (column == null) {
            throw new IllegalArgumentException(
                    "No column found for property " + property);
        }
        return column.isSortable();
    }

    /**
     * Gets the current sort direction for column at the given index.
     *
     * Throws an exception if the column does not exist or is not sortable.
     *
     * @param column
     *            column index to get sort direction
     * @return sort direction for the column, or {@literal null} if grid is not
     *         sorted by given column
     * @throws IllegalArgumentException
     *             if the column at given index is not sortable
     * @throws IndexOutOfBoundsException
     *             if column index is invalid
     */
    public SortDirection getSortDirection(int column) {
        if (isColumnSortable(column)) {
            Grid.Column<Y> col = getColumns().get(column);
            return getComponent().getSortOrder().stream()
                    .filter(order -> col.equals(order.getSorted()))
                    .map(SortOrder::getDirection).findFirst().orElse(null);
        }
        throw new IllegalArgumentException(
                "Column at index " + column + " is not sortable");
    }

    /**
     * Gets the current sort direction for column corresponding to the at the
     * given property.
     *
     * Throws an exception if the column does not exist or is not sortable.
     *
     * @param property
     *            the property name of the column, not null
     * @return sort direction for the column, or {@literal null} if grid is not
     *         sorted by given column
     * @throws IllegalArgumentException
     *             if property name does not identify a column or if the column
     *             is not sortable
     */
    public SortDirection getSortDirection(String property) {
        if (isColumnSortable(property)) {
            Grid.Column<Y> col = getColumn(property);
            return getComponent().getSortOrder().stream()
                    .filter(order -> col.equals(order.getSorted()))
                    .map(SortOrder::getDirection).findFirst().orElse(null);
        }
        throw new IllegalArgumentException(
                "Column for property " + property + " is not sortable");
    }

    /**
     * Sorts the grid by the given column and sort direction, as if the column
     * header is pressed in the browser until the requested direction is
     * reached.
     *
     * Throws an exception if the column is not sortable or not visible.
     *
     * @param column
     *            column index
     * @param direction
     *            sort direction
     */
    public void sortByColumn(int column, SortDirection direction) {
        while (getSortDirection(column) != direction) {
            sortByColumn(column);
        }
    }

    /**
     * Sorts the grid according to the given column sort status, as if the
     * column header is pressed in the browser.
     *
     * Throws an exception if the column is not sortable or not visible.
     *
     * @param column
     *            column index
     */
    public void sortByColumn(int column) {
        SortDirection currentDirection = getSortDirection(column);
        Grid.Column<Y> col = getColumns().get(column);
        doSort(currentDirection, col);
    }

    /**
     * Sorts the grid according to sort status ot the column identified by the
     * given property, as if the column header is pressed in the browser.
     *
     * Throws an exception if the column is not sortable or not visible.
     *
     * @param property
     *            the property name of the column, not null
     */
    public void sortByColumn(String property) {
        SortDirection currentDirection = getSortDirection(property);
        Grid.Column<Y> col = getColumn(property);
        doSort(currentDirection, col);
    }

    /**
     * Sorts the grid by the given column and sort direction, as if the column
     * header is pressed in the browser until the requested direction is
     * reached.
     *
     * Throws an exception if the column is not sortable or not visible.
     *
     * @param property
     *            the property name of the column, not null
     * @param direction
     *            sort direction
     */
    public void sortByColumn(String property, SortDirection direction) {
        while (getSortDirection(property) != direction) {
            sortByColumn(property);
        }
    }

    private Grid.MultiSortPriority getMultiSortPriority() {
        return "append".equals(
                getComponent().getElement().getAttribute("multi-sort-priority"))
                        ? Grid.MultiSortPriority.APPEND
                        : Grid.MultiSortPriority.PREPEND;
    }

    private void doSort(SortDirection currentDirection, Grid.Column<Y> col) {
        List<GridSortOrder<Y>> sortOrders = new ArrayList<>(
                getComponent().getSortOrder());
        if (getComponent().isMultiSort()) {
            sortOrders.removeIf(so -> so.getSorted() == col);
        } else {
            sortOrders.clear();
        }
        final Grid.MultiSortPriority multiSortPriority = getMultiSortPriority();
        final int insertIndex = multiSortPriority == Grid.MultiSortPriority.PREPEND
                ? 0
                : sortOrders.size();
        if (currentDirection == null) {
            sortOrders.add(insertIndex, GridSortOrder.asc(col).build().get(0));
        } else if (currentDirection == SortDirection.ASCENDING) {
            sortOrders.add(insertIndex, GridSortOrder.desc(col).build().get(0));
        }
        getComponent().sort(sortOrders);
    }

    private String getValueProviderString(int row, Grid.Column<Y> targetColumn)
            throws IllegalArgumentException {
        try {
            ColumnPathRenderer<Y> renderer = (ColumnPathRenderer<Y>) targetColumn
                    .getRenderer();

            Field f = ColumnPathRenderer.class.getDeclaredField("provider");
            f.setAccessible(true);

            @SuppressWarnings("unchecked")
            final ValueProvider<Y, ?> columnValueProvider = (ValueProvider<Y, ?>) f
                    .get(renderer);

            return columnValueProvider.apply(getRow(row)).toString();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(
                    "Failed to get value provider for column", e);
        }
    }

}
