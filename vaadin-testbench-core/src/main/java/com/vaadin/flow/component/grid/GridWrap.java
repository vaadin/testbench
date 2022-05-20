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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.MetaKeys;
import com.vaadin.testbench.unit.Wraps;
import com.vaadin.testbench.unit.component.GridKt;
import com.vaadin.testbench.unit.internal.PrettyPrintTreeKt;

@Wraps(Grid.class)
public class GridWrap<T extends Grid<Y>, Y> extends ComponentWrap<T> {
    /**
     * Wrap grid for testing.
     *
     * @param component
     *            target grid
     */
    public GridWrap(T component) {
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
     *
     * @param row
     *            row to click
     */
    public void clickRow(int row) {
        clickRow(row, 0);
    }

    /**
     * Click on grid row with given button.
     *
     * @param row
     *            row to click
     * @see {@link com.vaadin.flow.component.ClickEvent#getButton()}
     */
    public void clickRow(int row, int button) {
        clickRow(row, button, new MetaKeys());
    }

    /**
     * Click on grid row with given meta keys pressed.
     *
     * @param row
     *            row to click
     */
    public void clickRow(int row, MetaKeys metaKeys) {
        clickRow(row, 0, metaKeys);
    }

    /**
     * Click on grid row with given button and meta keys pressed.
     *
     * @param row
     *            row to click
     * @see {@link com.vaadin.flow.component.ClickEvent#getButton()}
     */
    public void clickRow(int row, int button, MetaKeys metaKeys) {
        ensureComponentIsUsable();
        GridKt._clickItem(getComponent(), row, button, metaKeys.isCtrl(),
                metaKeys.isShift(), metaKeys.isAlt(), metaKeys.isMeta());
    }

    /**
     * Select the item on given row.
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
     * For the default renderer ColumnPathRenderer the result is the sent text
     * for defined object path.
     * <p/>
     * For a ComponentRenderer the result is the rendered component as
     * prettyString.
     * <p/>
     * TODO: to be added as we find other renderers that need handling.
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
        final Grid.Column targetColumn = getComponent().getColumns()
                .get(column);
        if (targetColumn.getRenderer() instanceof ComponentRenderer) {
            return PrettyPrintTreeKt.toPrettyString(
                    ((ComponentRenderer<?, Y>) targetColumn.getRenderer())
                            .createComponent(getRow(row)));
        } else if (targetColumn.getRenderer() instanceof ColumnPathRenderer) {
            // This renderer just writes the object text using a path
            return getValueProviderString(row, targetColumn);
        }
        return null;
    }

    /**
     * Get content in header for given column.
     *
     * @param column
     *            column to get header for
     * @return header contents
     * @throws IllegalStateException
     *             if component is not visible
     */
    public String getHeaderCell(int column) {
        ensureVisible();
        final Grid.Column<Y> targetColumn = getComponent().getColumns()
                .get(column);
        return GridKt.getHeader2(targetColumn);
    }

    /**
     * Get content in footer for given column. TODO: How to get the value for
     * footer. Is it possible?
     *
     * @param column
     *            column to get footer for
     * @return footer contents
     * @throws IllegalStateException
     *             if component is not visible
     */
    public ValueProvider<?, ?> getFooterCell(int column) {
        ensureVisible();
        final Grid.Column<Y> targetColumn = getComponent().getColumns()
                .get(column);
        return targetColumn.getFooterRenderer().getValueProviders()
                .get(getColumnInternalId(targetColumn));
    }

    private void ensureVisible() {
        if (!getComponent().isVisible()) {
            throw new IllegalStateException("Grid is not visible!");
        }
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

    private String getValueProviderString(int row, Grid.Column targetColumn)
            throws IllegalArgumentException {
        final String columnId = getColumnInternalId(targetColumn);

        final ValueProvider columnValueProvider = (ValueProvider) targetColumn
                .getRenderer().getValueProviders().get(columnId);

        return columnValueProvider.apply(getRow(row)).toString();
    }

    private String getColumnInternalId(Grid.Column targetColumn) {
        try {
            final Method getInternalId = Grid.Column.class
                    .getDeclaredMethod("getInternalId");
            boolean state = getInternalId.canAccess(targetColumn);
            getInternalId.setAccessible(true);
            final String columnId = (String) getInternalId.invoke(targetColumn);
            getInternalId.setAccessible(state);
            return columnId;

        } catch (NoSuchMethodException | InvocationTargetException
                | IllegalAccessException e) {
            throw new RuntimeException("Failed to get internal id for column",
                    e);
        }
    }

}
