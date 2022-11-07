/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

@file:Suppress("FunctionName")

package com.vaadin.testbench.unit.component

import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.CheckboxGroup
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.grid.AbstractGridMultiSelectionModel
import com.vaadin.flow.component.grid.ColumnPathRenderer
import com.vaadin.flow.component.grid.FooterRow
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridMultiSelectionModel
import com.vaadin.flow.component.grid.GridSingleSelectionModel
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.grid.HeaderRow
import com.vaadin.flow.component.grid.ItemClickEvent
import com.vaadin.flow.component.grid.ItemDoubleClickEvent
import com.vaadin.flow.component.grid.editor.Editor
import com.vaadin.flow.component.listbox.ListBoxBase
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.binder.HasItems
import com.vaadin.flow.data.provider.DataCommunicator
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.provider.QuerySortOrder
import com.vaadin.flow.data.provider.SortDirection
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.renderer.NativeButtonRenderer
import com.vaadin.flow.data.renderer.Renderer
import com.vaadin.flow.function.SerializablePredicate
import com.vaadin.flow.function.ValueProvider
import com.vaadin.testbench.unit.internal.DepthFirstTreeIterator
import com.vaadin.testbench.unit.internal.MockVaadin
import com.vaadin.testbench.unit.internal.PrettyPrintTree
import com.vaadin.testbench.unit.internal._fireEvent
import com.vaadin.testbench.unit.internal._getPresentationValue
import com.vaadin.testbench.unit.internal._saneFetchLimit
import com.vaadin.testbench.unit.internal.checkEditableByUser
import com.vaadin.testbench.unit.internal.filterNotBlank
import com.vaadin.testbench.unit.internal.size
import com.vaadin.testbench.unit.internal.template
import com.vaadin.testbench.unit.internal.toPrettyString
import java.lang.reflect.Method
import java.util.stream.Stream
import kotlin.reflect.KProperty1
import kotlin.streams.toList

/**
 * Returns the item on given row. Fails if the row index is invalid. The data provider is
 * sorted according to given [sortOrders] (empty by default) and filtered according
 * to given [filter] (null by default) first.
 * @param rowIndex the row, 0..size - 1
 * @return the item at given row.
 * @throws AssertionError if the row index is out of bounds.
 */
public fun <T, F> DataProvider<T, F>._get(rowIndex: Int, sortOrders: List<QuerySortOrder> = listOf(), inMemorySorting: Comparator<T>? = null, filter: F? = null): T {
    require(rowIndex >= 0) { "rowIndex must be 0 or greater: $rowIndex" }
    val fetched: Stream<T> = fetch(Query(rowIndex, 1, sortOrders, inMemorySorting, filter))
    return fetched.toList().firstOrNull()
            ?: throw AssertionError("Requested to get row $rowIndex but the data provider only has ${_size(filter)} rows matching filter $filter")
}

/**
 * Returns all items in given data provider, sorted according to given [sortOrders] (empty by default) and filtered according
 * to given [filter] (null by default).
 * @return the list of items.
 */
public fun <T, F> DataProvider<T, F>._findAll(sortOrders: List<QuerySortOrder> = listOf(), inMemorySorting: Comparator<T>? = null, filter: F? = null): List<T> {
    val fetched: Stream<T> = fetch(Query(0, Int.MAX_VALUE, sortOrders, inMemorySorting, filter))
    return fetched.toList()
}

/**
 * Returns the item on given row. Fails if the row index is invalid. Uses current Grid sorting.
 *
 * For [TreeGrid] this returns the x-th displayed row; skips children of collapsed nodes.
 * Uses [_rowSequence].
 *
 * WARNING: Very slow operation for [TreeGrid].
 * @param rowIndex the row, 0..size - 1
 * @return the item at given row, not null.
 */
public fun <T : Any> Grid<T>._get(rowIndex: Int): T {
    require(rowIndex >= 0) { "rowIndex must be 0 or greater: $rowIndex" }
    if (this !is TreeGrid && _dataProviderSupportsSizeOp) {
        // only perform this check for regular Grid. TreeGrid._fetch()'s Sequence consults size() internally.
        val size: Int = _size()
        if (rowIndex >= size) {
            throw AssertionError("Requested to get row $rowIndex but the data provider only has ${_size()} rows\n${_dump()}")
        }
    }
    return _getOrNull(rowIndex)
        ?: throw AssertionError("Requested to get row $rowIndex but the data provider returned 0 rows\n${_dump()}")
}

/**
 * Returns the item on given row, or null if the [rowIndex] is larger than the number
 * of items the data provider can provide. Uses current Grid sorting.
 *
 * For [TreeGrid] this returns the x-th displayed row; skips children of collapsed nodes.
 * Uses [_rowSequence].
 *
 * WARNING: Very slow operation for [TreeGrid].
 * @param rowIndex the row, 0 or larger.
 * @return the item at given row or null if the data provider provides less rows.
 */
public fun <T : Any> Grid<T>._getOrNull(rowIndex: Int): T? {
    require(rowIndex >= 0) { "rowIndex must be 0 or greater: $rowIndex" }
    if (this !is TreeGrid && _dataProviderSupportsSizeOp) {
        // only perform this check for regular Grid. TreeGrid._fetch()'s Sequence consults size() internally.
        val size: Int = _size()
        if (rowIndex >= size) {
            return null
        }
    }
    val fetched: List<T> = _fetch(rowIndex, 1)
    return fetched.firstOrNull()
}

/**
 * Vaadin 19+ Grids support setting data providers which do not support retrieving
 * the number of available rows. See `FetchCallback` for more details.
 * @return true if the current data provider supports [_size] retrieval, false
 * if not. Returns true for Vaadin 14.
 */
public val Grid<*>._dataProviderSupportsSizeOp: Boolean get() {
    val m = DataCommunicator::class.java.getDeclaredMethod("isDefinedSize")
    return m.invoke(dataCommunicator) as Boolean
}

/**
 * Returns items in given range from Grid's data provider. Uses current Grid sorting.
 *
 * For [TreeGrid] this walks the [_rowSequence].
 *
 * The Grid never sets any filters into the data provider, however any
 * ConfigurableFilterDataProvider will automatically apply its filters.
 *
 * WARNING: Very slow operation for [TreeGrid].
 */
public fun <T> Grid<T>._fetch(offset: Int, limit: Int): List<T> = when(this) {
    is TreeGrid<T> -> this._rowSequence().drop(offset).take(limit).toList()
    else -> dataCommunicator.fetch(offset, limit)
}

private val _DataCommunicator_fetchFromProvider: Method =
    DataCommunicator::class.java.getDeclaredMethod("fetchFromProvider", Int::class.java, Int::class.java).apply {
        isAccessible = true
    }

private val _DataCommunicator_setPagingEnabled: Method? =
    DataCommunicator::class.java.declaredMethods.firstOrNull { it.name == "setPagingEnabled" }


/**
 * Returns items in given range from this data communicator. Uses current Grid sorting.
 * Any ConfigurableFilterDataProvider will automatically apply its filters.
 *
 * This is an internal stuff, most probably you wish to call [_fetch].
 */
public fun <T> DataCommunicator<T>.fetch(offset: Int, limit: Int): List<T> {
    require(limit <= _saneFetchLimit) { "Vaadin doesn't handle fetching of many items very well unfortunately. The sane limit is $_saneFetchLimit but you asked for $limit" }

    // make sure the DataCommunicator is not in paged mode: https://github.com/mvysny/karibu-testing/issues/99
    _DataCommunicator_setPagingEnabled?.invoke(this, false)

    @Suppress("UNCHECKED_CAST")
    val fetched: Stream<T> = _DataCommunicator_fetchFromProvider.invoke(this, offset, limit) as Stream<T>
    return fetched.toList()
}

/**
 * Returns all items from this data communicator. Uses current Grid sorting.
 * Any ConfigurableFilterDataProvider will automatically apply its filters.
 *
 * This is an internal stuff, most probably you wish to call [_fetch].
 */
public fun <T> DataCommunicator<T>.fetchAll(): List<T> = fetch(0, _saneFetchLimit)

/**
 * Returns all items in given data provider. Uses current Grid sorting.
 *
 * For [TreeGrid] this returns all displayed rows; skips children of collapsed nodes.
 *
 * The Grid never sets any filters into the data provider, however any
 * ConfigurableFilterDataProvider will automatically apply its filters.
 *
 * @return the list of items.
 */
public fun <T> Grid<T>._findAll(): List<T> = _fetch(0, _saneFetchLimit)

/**
 * Returns the number of items in this data provider.
 *
 * In case of [HierarchicalDataProvider]
 * this returns the number of ALL items including all leafs.
 */
public fun <T, F> DataProvider<T, F>._size(filter: F? = null): Int {
    if (this is HierarchicalDataProvider<T, F>) {
        return this._size(null, filter)
    }
    return size(Query(filter))
}

/**
 * Returns the number of items in this data provider, including child items.
 * The function traverses recursively until all children are found; then a total size
 * is returned. The function uses [HierarchicalDataProvider.size] mostly, but
 * also uses [HierarchicalDataProvider.fetchChildren] to discover children.
 * Only children matching [filter] are considered for recursive computation of
 * the size.
 *
 * Note that this can differ to `Grid._size()` since `Grid._size()` ignores children
 * of collapsed tree nodes.
 * @param root start with this item; defaults to null to iterate all items
 * @param filter filter to pass to [HierarchicalQuery]
 */
@JvmOverloads
public fun <T, F> HierarchicalDataProvider<T, F>._size(root: T? = null, filter: F? = null): Int =
    _rowSequence(root, filter = filter).count()

private val _DataCommunicator_getDataProviderSize: Method =
    DataCommunicator::class.java.getDeclaredMethod("getDataProviderSize").apply { isAccessible = true }

/**
 * Returns the number of items in this Grid.
 *
 * For [TreeGrid] this computes the number of items the [TreeGrid] is actually showing on-screen,
 * ignoring children of collapsed nodes.
 *
 * A very slow operation for [TreeGrid] since it walks through all items returned by [_rowSequence].
 *
 * If [_dataProviderSupportsSizeOp] is false, this function will fetch all the data
 * and count the result returned, which is also very slow.
 */
public fun Grid<*>._size(): Int {
    if (this is TreeGrid<*>) {
        return this._size()
    }
    if (!_dataProviderSupportsSizeOp) {
        return _findAll().size
    }
    return _DataCommunicator_getDataProviderSize.invoke(dataCommunicator) as Int
}

/**
 * Gets a [Grid.Column] of this grid by its [columnKey].
 * @throws AssertionError if no such column exists.
 */
public fun <T> Grid<T>._getColumnByKey(columnKey: String): Grid.Column<T> = getColumnByKey(columnKey)
        ?: throw AssertionError("${toPrettyString()}: No such column with key '$columnKey'; available columns: ${columns.mapNotNull { it.key }}")

/**
 * Performs a click on a [ClickableRenderer] in given [Grid] cell. Only supports the following scenarios:
 * * [ClickableRenderer]
 * * [ComponentRenderer] which renders a [Button] or a [ClickNotifier].
 *
 * The `click` closure is no longer supported - please see https://github.com/mvysny/karibu-testing/issues/67 for more details.
 * @param rowIndex the row index, 0 or higher.
 * @param columnKey the column key [Grid.Column.getKey]
 * @throws AssertionError if the renderer is not [ClickableRenderer] nor [ComponentRenderer].
 */
//public fun <T : Any> Grid<T>._clickRenderer(rowIndex: Int, columnKey: String) {
//    checkEditableByUser()
//    val column: Grid.Column<T> = _getColumnByKey(columnKey)
//    val renderer: Renderer<T>? = column.renderer
//    val item: T = _get(rowIndex)
//    if (renderer is ClickableRenderer<*>) {
//        @Suppress("UNCHECKED_CAST")
//        (renderer as ClickableRenderer<T>).onClick(item)
//    } else if (renderer is ComponentRenderer<*, *>) {
//        val component: Component = (renderer as ComponentRenderer<*, T>).createComponent(item)
//        if (component is Button) {
//            component._click()
//        } else if (component is ClickNotifier<*>) {
//            component._click()
//        } else {
//            // don't try to do anything smart here since things will break silently for the customer as they upgrade Vaadin version
//            // https://github.com/mvysny/karibu-testing/issues/67
//            fail("${this.toPrettyString()} column $columnKey: ComponentRenderer produced ${component.toPrettyString()} which is not a button nor a ClickNotifier - please use _getCellComponent() instead")
//        }
//    } else {
//        fail("${this.toPrettyString()} column $columnKey has renderer $renderer which is not supported by this method")
//    }
//}

/**
 * Retrieves a component produced by [ComponentRenderer] in given [Grid] cell. Fails if the
 * renderer is not a [ComponentRenderer].
 * @param rowIndex the row index, 0 or higher.
 * @param columnKey the column key [Grid.Column.getKey]
 * @throws IllegalStateException if the renderer is not [ComponentRenderer].
 */
public fun <T : Any> Grid<T>._getCellComponent(
    rowIndex: Int,
    columnKey: String
): Component {
    val column: Grid.Column<T> = _getColumnByKey(columnKey)
    val renderer: Renderer<T>? = column.renderer
    if (renderer !is ComponentRenderer<*, *>) {
        throw java.lang.IllegalArgumentException("${this.toPrettyString()} column $columnKey uses renderer $renderer but we expect a ComponentRenderer here")
    }
    if (renderer is NativeButtonRenderer<*>) {
        throw java.lang.IllegalArgumentException("${this.toPrettyString()} column $columnKey uses NativeButtonRenderer which is not supported by this function")
    }
    val item: T = _get(rowIndex)
    val component: Component = (renderer as ComponentRenderer<*, T>).createComponent(item)
    return component
}

/**
 * Returns the formatted value of given column as a String. Uses [getPresentationValue]
 * and converts the result to string (even if the result is a [Component]).
 * @param rowIndex the row index, 0 or higher.
 * @param columnKey the column ID.
 */
@Suppress("UNCHECKED_CAST")
public fun <T : Any> Grid<T>._getFormatted(rowIndex: Int, columnKey: String): String {
    val rowObject: T = _get(rowIndex)
    val column: Grid.Column<T> = _getColumnByKey(columnKey)
    return column._getFormatted(rowObject)
}

/**
 * Returns the formatted value as a String. Uses [getPresentationValue]
 * and converts the result to string (even if the result is a [Component]).
 * @param rowObject the bean
 */
@Suppress("UNCHECKED_CAST")
public fun <T : Any> Grid.Column<T>._getFormatted(rowObject: T): String =
        getPresentationValue(rowObject).toString()

/**
 * Returns the formatted row as a list of Strings, one for every visible column.
 * Uses [_getFormatted].
 * @param rowObject the bean
 */
public fun <T : Any> Grid<T>._getFormattedRow(rowObject: T): List<String> =
        columns.filter { it.isVisible }.map { it._getFormatted(rowObject) }

/**
 * Returns the formatted row as a list of Strings, one for every visible column.
 * Uses [_getFormatted]. Fails if the [rowIndex] is not within the limits.
 * @param rowIndex the index of the row, 0..size-1.
 */
public fun <T : Any> Grid<T>._getFormattedRow(rowIndex: Int): List<String> {
    val rowObject: T = _get(rowIndex)
    return _getFormattedRow(rowObject)
}

/**
 * Returns the formatted row as a list of Strings, one for every visible column.
 * Uses [_getFormatted]. Returns null if the [rowIndex] is not within the limits.
 * @param rowIndex the index of the row, 0-based.
 */
public fun <T : Any> Grid<T>._getFormattedRowOrNull(rowIndex: Int): List<String>? {
    val rowObject: T = _getOrNull(rowIndex) ?: return null
    return _getFormattedRow(rowObject)
}

/**
 * Returns the output of renderer set for this column for given [rowObject] formatted as close as possible
 * to the client-side output, using [Grid.Column.renderer].
 */
@Suppress("UNCHECKED_CAST")
public fun <T : Any> Grid.Column<T>.getPresentationValue(rowObject: T): Any? {
    val renderer: Renderer<T> = this.renderer
    if (renderer is ColumnPathRenderer) {
        val valueProviders: MutableMap<String, ValueProvider<T, *>> = renderer.valueProviders
        val valueProvider: ValueProvider<T, *> = valueProviders[_internalId]
                ?: return null
        val value: Any? = valueProvider.apply(rowObject)
        return value.toString()
    }
    return renderer._getPresentationValue(rowObject)
}

private fun <T> Grid<T>.getSortIndicator(column: Grid.Column<T>): String {
    val so: GridSortOrder<T>? = sortOrder.firstOrNull { it.sorted == column }
    return when {
        so == null -> ""
        so.direction == SortDirection.ASCENDING -> "v"
        else -> "^"
    }
}

/**
 * Dumps given range of [rows] of the Grid, formatting the values using the [_getFormatted] function. The output example:
 * ```
 * --[Name]--[Age]--[Occupation]--
 * 0: John, 25, Service Worker
 * 1: Fred, 40, Supervisor
 * --and 198 more
 * ```
 */
@JvmOverloads
public fun <T : Any> Grid<T>._dump(rows: IntRange = 0..9): String = buildString {
    val visibleColumns: List<Grid.Column<T>> = columns.filter { it.isVisible }
    visibleColumns.map { "[${it.header2}]${getSortIndicator(it)}" }.joinTo(this, prefix = "--", separator = "-", postfix = "--\n")
    val dsIndices: IntRange
    val displayIndices: Set<Int>
    if (this@_dump is TreeGrid<T>) {
        val tree: PrettyPrintTree = this@_dump._dataSourceToPrettyTree()
        val lines = tree.print().split('\n').filterNotBlank().drop(1)
        dsIndices = lines.indices
        displayIndices = rows.intersect(dsIndices)
        for (i in displayIndices) {
            append("$i: ${lines[i]}\n")
        }
        val andMore = dsIndices.size - displayIndices.size
        if (andMore > 0) {
            append("--and $andMore more\n")
        }
    } else if (_dataProviderSupportsSizeOp) {
        dsIndices = 0 until _size()
        displayIndices = rows.intersect(dsIndices)
        for (i in displayIndices) {
            _getFormattedRow(i).joinTo(this, prefix = "$i: ", postfix = "\n")
        }
        val andMore = dsIndices.size - displayIndices.size
        if (andMore > 0) {
            append("--and $andMore more\n")
        }
    } else {
        var rowsOutputted = 0
        for (i in rows) {
            val row = _getFormattedRowOrNull(i) ?: break
            row.joinTo(this, prefix = "$i: ", postfix = "\n")
            rowsOutputted++
        }
        if (rowsOutputted == rows.size) {
            append("--and possibly more\n")
        } else {
            append("--\n")
        }
    }
}

/**
 * Asserts that this grid's provider returns given [count] of items. If not,
 * an [AssertionError] is thrown with the Grid [_dump].
 */
public fun Grid<*>.expectRows(count: Int) {
    val actual = _size()
    if (actual != count) {
        throw AssertionError("${this.toPrettyString()}: expected $count rows but got $actual\n${_dump()}")
    }
}

/**
 * Asserts that this grid's [rowIndex] row is formatted as expected.
 * @param row the expected row formatting.
 */
public fun Grid<*>.expectRow(rowIndex: Int, vararg row: String) {
    val expected: List<String> = row.toList()
    val actual: List<String> = _getFormattedRow(rowIndex)
    if (expected != actual) {
        throw AssertionError("${this.toPrettyString()} at $rowIndex: expected $expected but got $actual\n${_dump()}")
    }
}

/**
 * Returns `com.vaadin.flow.component.grid.AbstractColumn`
 */
@Suppress("ConflictingExtensionProperty")  // conflicting property is "protected"
internal val HeaderRow.HeaderCell.column: Any
    get() = _AbstractCell_getColumn.invoke(this)

private val abstractCellClass: Class<*> = Class.forName("com.vaadin.flow.component.grid.AbstractRow\$AbstractCell")
private val abstractColumnClass: Class<*> = Class.forName("com.vaadin.flow.component.grid.AbstractColumn")
private val _AbstractCell_getColumn: Method by lazy(LazyThreadSafetyMode.PUBLICATION) {
    val m: Method = abstractCellClass.getDeclaredMethod("getColumn")
    m.isAccessible = true
    m
}

/**
 * Returns `com.vaadin.flow.component.grid.AbstractColumn`
 */
@Suppress("ConflictingExtensionProperty")  // conflicting property is "protected"
private val FooterRow.FooterCell.column: Any
    get() = _AbstractCell_getColumn.invoke(this)

/**
 * Retrieves the cell for given [property]; it matches [Grid.Column.getKey] to [KProperty1.name].
 * @return the corresponding cell
 * @throws IllegalArgumentException if no such column exists.
 */
public fun HeaderRow.getCell(property: KProperty1<*, *>): HeaderRow.HeaderCell =
        getCell(property.name)

/**
 * Retrieves the cell for given [Grid.Column.getKey].
 * @return the corresponding cell
 * @throws IllegalArgumentException if no such column exists.
 */
public fun HeaderRow.getCell(key: String): HeaderRow.HeaderCell {
    val cell: HeaderRow.HeaderCell? = cells.firstOrNull { (it.column as Grid.Column<*>).key == key }
    require(cell != null) { "This grid has no property named ${key}: $cells" }
    return cell
}

private val _AbstractColumn_getBottomLevelColumn: Method by lazy(LazyThreadSafetyMode.PUBLICATION) {
    val method: Method = abstractColumnClass.getDeclaredMethod("getBottomLevelColumn")
    method.isAccessible = true
    method
}

/**
 * Retrieves column key from the `AbstractColumn` receiver. The problem here is that receiver can be `ColumnGroup` which doesn't have
 * a key.
 */
private val Any.columnKey: String?
    get() {
        abstractColumnClass.cast(this)
        val gridColumn: Grid.Column<*> = _AbstractColumn_getBottomLevelColumn.invoke(this) as Grid.Column<*>
        return gridColumn.key
    }

/**
 * Retrieves the cell for given [property]; it matches [Grid.Column.getKey] to [KProperty1.name].
 * @return the corresponding cell
 * @throws IllegalArgumentException if no such column exists.
 */
public fun FooterRow.getCell(property: KProperty1<*, *>): FooterRow.FooterCell =
        getCell(property.name)

/**
 * Retrieves the cell for given [Grid.Column.getKey].
 * @return the corresponding cell
 * @throws IllegalArgumentException if no such column exists.
 */
public fun FooterRow.getCell(key: String): FooterRow.FooterCell {
    val cell: FooterRow.FooterCell? = cells.firstOrNull { it.column.columnKey == key }
    require(cell != null) { "This grid has no property named ${key}: $cells" }
    return cell
}

/**
 * Sorts given grid. Affects [_findAll], [_get] and other data-fetching functions.
 */
public fun <T> Grid<T>.sort(vararg sortOrder: QuerySortOrder) {
    sort(sortOrder.map { GridSortOrder(_getColumnByKey(it.sorted), it.direction) })
}

/**
 * Fires the [ItemClickEvent] event for given [rowIndex] which invokes all item click listeners registered via
 * [Grid.addItemClickListener].
 * @param button the id of the pressed mouse button (0 is the default button, see ClickEvent.getButton)
 * @param ctrlKey `true` if the control key was down when the event was fired, `false` otherwise
 * @param shiftKey `true` if the shift key was down when the event was fired, `false` otherwise
 * @param altKey `true` if the alt key was down when the event was fired, `false` otherwise
 * @param metaKey `true` if the meta key was down when the event was fired, `false` otherwise
 */
@JvmOverloads
public fun <T : Any> Grid<T>._clickItem(rowIndex: Int, button: Int = 0, ctrlKey: Boolean = false,
                           shiftKey: Boolean = false, altKey: Boolean = false, metaKey: Boolean = false) {
    _clickItem(rowIndex, null, button, ctrlKey, shiftKey, altKey, metaKey)
}

/**
 * Fires the [ItemClickEvent] event for given [rowIndex] and a [column] which invokes all item click listeners
 * registered via [Grid.addItemClickListener].
 * @param button the id of the pressed mouse button
 * @param column optional column to be clicked
 * @param ctrlKey `true` if the control key was down when the event was fired, `false` otherwise
 * @param shiftKey `true` if the shift key was down when the event was fired, `false` otherwise
 * @param altKey `true` if the alt key was down when the event was fired, `false` otherwise
 * @param metaKey `true` if the meta key was down when the event was fired, `false` otherwise
 */
@JvmOverloads
public fun <T : Any> Grid<T>._clickItem(rowIndex: Int, column: Grid.Column<*>?, button: Int = 1, ctrlKey: Boolean = false,
                           shiftKey: Boolean = false, altKey: Boolean = false, metaKey: Boolean = false) {
    checkEditableByUser()
    // fire SelectionEvent if need be: https://github.com/mvysny/karibu-testing/issues/96
    val item: T = _get(rowIndex)
    if (selectionModel is GridSingleSelectionModel) {
        val selectedItem: T? = selectedItems.firstOrNull()
        if (selectedItem != item) {
            select(item)
        } else {
            // clicking on a selected item will de-select it.
            deselectAll()
        }
    }

    // fire ItemClickEvent
    val itemKey: String = dataCommunicator.keyMapper.key(item)
    val internalColumnId = column?._internalId
    val event = ItemClickEvent<T>(this, true, itemKey, internalColumnId, -1, -1, -1, -1, 1, button, ctrlKey, shiftKey, altKey, metaKey)
    _fireEvent(event)
}

/**
 * Fires the [ItemClickEvent] event for given [rowIndex] and a [columnKey] which invokes all item click listeners
 * registered via [Grid.addItemClickListener].
 * @param button the id of the pressed mouse button
 * @param columnKey the key of the column to be clicked
 * @param ctrlKey `true` if the control key was down when the event was fired, `false` otherwise
 * @param shiftKey `true` if the shift key was down when the event was fired, `false` otherwise
 * @param altKey `true` if the alt key was down when the event was fired, `false` otherwise
 * @param metaKey `true` if the meta key was down when the event was fired, `false` otherwise
 */
@JvmOverloads
public fun <T : Any> Grid<T>._clickItem(rowIndex: Int, columnKey: String, button: Int = 1, ctrlKey: Boolean = false,
                           shiftKey: Boolean = false, altKey: Boolean = false, metaKey: Boolean = false) {
    _clickItem(rowIndex, _getColumnByKey(columnKey), button, ctrlKey, shiftKey, altKey, metaKey)
}

/**
 * Fires the [ItemDoubleClickEvent] event for given [rowIndex] which invokes all item click listeners registered via
 * [Grid.addItemDoubleClickListener].
 * @param button the id of the pressed mouse button
 * @param ctrlKey `true` if the control key was down when the event was fired, `false` otherwise
 * @param shiftKey `true` if the shift key was down when the event was fired, `false` otherwise
 * @param altKey `true` if the alt key was down when the event was fired, `false` otherwise
 * @param metaKey `true` if the meta key was down when the event was fired, `false` otherwise
 */
@JvmOverloads
public fun <T : Any> Grid<T>._doubleClickItem(rowIndex: Int, button: Int = 1, ctrlKey: Boolean = false,
                                        shiftKey: Boolean = false, altKey: Boolean = false, metaKey: Boolean = false) {
    checkEditableByUser()
    val itemKey: String = dataCommunicator.keyMapper.key(_get(rowIndex))
    val event = ItemDoubleClickEvent<T>(this, true, itemKey, null, -1, -1, -1, -1, 2, button, ctrlKey, shiftKey, altKey, metaKey)
    _fireEvent(event)
}

/**
 * Returns a sequence which walks over all rows the [TreeGrid] is actually showing.
 * The sequence will *skip* children of collapsed nodes.
 *
 * Iterating the entire sequence is a very slow operation since it will repeatedly
 * poll [HierarchicalDataProvider] for list of children.
 *
 * Honors current grid ordering.
 */
public fun <T> TreeGrid<T>._rowSequence(filter: SerializablePredicate<T>? = null): Sequence<T> {
    val isExpanded: (T) -> Boolean = { item: T -> isExpanded(item) }
    return dataProvider._rowSequence(null, isExpanded, filter)
}

/**
 * Returns a sequence which walks over all rows the [TreeGrid] is actually showing.
 * The sequence will *skip* children of collapsed nodes.
 *
 * Iterating the entire sequence is a very slow operation since it will repeatedly
 * poll [HierarchicalDataProvider] for list of children.
 *
 * Honors current grid ordering.
 * @param root start with this item; defaults to null to iterate all items
 * @param isExpanded if returns null for an item, children of that item are skipped
 * @param filter filter to pass to [HierarchicalQuery]
 */
@JvmOverloads
public fun <T, F> HierarchicalDataProvider<T, F>._rowSequence(root: T? = null,
                                                       isExpanded: (T)->Boolean = { true },
                                                       filter: F? = null): Sequence<T> {

    fun getChildrenOf(item: T?): List<T> = if (item == null || isExpanded(item)) {
        checkedFetch(HierarchicalQuery<T, F>(filter, item))
    } else {
        listOf<T>()
    }

    fun itemSubtreeSequence(item: T): Sequence<T> =
            DepthFirstTreeIterator(item) { getChildrenOf(it) } .asSequence()

    val roots: List<T> = getChildrenOf(root)
    return roots.map { itemSubtreeSequence(it) } .asSequence().flatten()
}

/**
 * Returns the number of items the [TreeGrid] is actually showing. For example
 * it doesn't count in children of collapsed nodes.
 *
 * A very slow operation since it walks through all items returned by [_rowSequence].
 */
public fun TreeGrid<*>._size(): Int = _rowSequence().count()

private fun <T, F> HierarchicalDataProvider<T, F>.checkedSize(query: HierarchicalQuery<T, F>): Int {
    if (query.parent != null && !hasChildren(query.parent)) return 0
    val result: Int = size(query)
    check(result >= 0) { "size($query) returned negative count: $result" }
    return result
}
private fun <T, F> HierarchicalDataProvider<T, F>.checkedFetch(query: HierarchicalQuery<T, F>): List<T> = when {
    checkedSize(query) == 0 -> listOf()
    else -> fetchChildren(query).toList()
}

public fun <T : Any> TreeGrid<T>._dataSourceToPrettyTree(): PrettyPrintTree {
    fun getChildrenOf(item: T?): List<T> =
            if (item == null || isExpanded(item)) {
                dataProvider.checkedFetch(HierarchicalQuery<T, SerializablePredicate<T>?>(null, item))
            } else {
                listOf<T>()
            }

    fun toPrettyTree(item: T): PrettyPrintTree {
        val self: String = _getFormattedRow(item).joinToString(postfix = "\n")
        val children: List<T> = getChildrenOf(item)
        return PrettyPrintTree(self, children.map { toPrettyTree(it) } .toMutableList())
    }

    val roots: List<T> = getChildrenOf(null)
    return PrettyPrintTree("TreeGrid", roots.map { toPrettyTree(it) } .toMutableList())
}

@Suppress("UNCHECKED_CAST")
public fun <T> TreeGrid<T>._getRootItems(): List<T> =
        dataProvider.fetch(HierarchicalQuery(null, null)).toList()

/**
 * Expands all nodes. May invoke massive data loading.
 */
@JvmOverloads
public fun <T> TreeGrid<T>._expandAll(depth: Int = 100) {
    expandRecursively(_getRootItems(), depth)
}

/**
 * Returns the data provider currently set to this [HasItems].
 */
//public val <T> HasItems<T>.dataProvider: DataProvider<T, *>? get() = when (this) {
//    // until https://github.com/vaadin/flow/issues/6296 is resolved
//    is Grid<T> -> this.dataProvider
//    is IronList<T> -> this.dataProvider
//    is Select<T> -> this.dataProvider
//    is ListBoxBase<*, T, *> -> this.getDataProvider()
//    is RadioButtonGroup<T> -> this.dataProvider
//    is CheckboxGroup<T> -> this.dataProvider
//    is ComboBox<T> -> this.dataProvider
//    else -> null
//}

/**
 * Returns the data provider currently set to this Component.
 *
 * Works both with Vaadin 16 and Vaadin 17: Vaadin 17 components no longer implement HasItems.
 */
public val Component.dataProvider: DataProvider<*, *>? get() = when (this) {
    // until https://github.com/vaadin/flow/issues/6296 is resolved
    is Grid<*> -> this.dataProvider
    is Select<*> -> this.dataProvider
    is ListBoxBase<*, *, *> -> this.getDataProvider()
    is RadioButtonGroup<*> -> this.dataProvider
    is CheckboxGroup<*> -> this.dataProvider
    is ComboBox<*> -> this.dataProvider
    else -> null
}

/**
 * Call this instead of [Editor.editItem] - this function makes sure that the editor opening is
 * mocked properly, calls the editor bindings, and fires the editor-open-event.
 */
public fun <T> Editor<T>._editItem(item: T) {
//    expect(true, "${grid.toPrettyString()} is not attached, editItem() would do nothing. Make sure the Grid is attached to an UI") {
//        grid.isAttached()
//    }
    if(!grid.isAttached()) throw java.lang.IllegalStateException("Grid is not attached so can not edit")
    editItem(item)
    MockVaadin.clientRoundtrip()
}

//////////////
private val _Column_getInternalId: Method by lazy(LazyThreadSafetyMode.PUBLICATION) {
    val m = Grid.Column::class.java.getDeclaredMethod("getInternalId")
    m.isAccessible = true
    m
}
/**
 * In single select clears the selection and select only given [item], for multiselect add to selection.
 */
public fun <T: Any> Grid<T>._select(item: T) {
    checkEditableByUser()
    if(selectionModel is GridSingleSelectionModel) {
        deselectAll()
    }
    // fails properly if the Grid doesn't support selection.
    selectionModel.selectFromClient(item)
}

/**
 * Selects all items in the Grid; runs the same code as when the "select all" checkbox is checked.
 * Fails if the grid is not multi-select or the "select all" checkbox is hidden.
 */
public fun <T> Grid<T>._selectAll() {
    checkEditableByUser()
    if(selectionModel !is GridMultiSelectionModel) {
        throw IllegalStateException("Select all requires multi selection mode")
    }
    val clientSelectAllMethod = AbstractGridMultiSelectionModel::class.java.getDeclaredMethod("clientSelectAll")
    clientSelectAllMethod.isAccessible = true
    clientSelectAllMethod.invoke(selectionModel)
}

/**
 * Returns the column's Internal ID. Not related to [Grid.Column.getKey] in any way.
 * Auto-generated by the Grid. Not really useful; mostly used internally by Vaadin.
 */
public val Grid.Column<*>._internalId: String
    get() = _Column_getInternalId.invoke(this) as String

private val _AbstractColumn_getHeaderRenderer: Method by lazy(LazyThreadSafetyMode.PUBLICATION) {
    val method: Method = abstractColumnClass.getDeclaredMethod("getHeaderRenderer")
    method.isAccessible = true
    method
}

private fun Any.gridAbstractHeaderGetHeader(): String {
    // nasty reflection. Added a feature request to have this: https://github.com/vaadin/vaadin-grid-flow/issues/567
    val e: Renderer<*>? = _AbstractColumn_getHeaderRenderer.invoke(this) as Renderer<*>?
    return e?.template ?: ""
}

/**
 * Sets and retrieves the column header as set by [Grid.Column.setHeader] (String).
 * The result value is undefined if a component has been set as the header.
 */
var <T> Grid.Column<T>.header2: String
    get() {
        var result: String = gridAbstractHeaderGetHeader()
        if (result.isEmpty()) {
            // in case of grouped cells, the header is stored in a parent ColumnGroup.
            val parent: Component? = parent.orElse(null)
            if (parent != null && parent.javaClass.name == "com.vaadin.flow.component.grid.ColumnGroup" && parent.children.count() == 1L) {
                result = parent.gridAbstractHeaderGetHeader()
            }
        }
        return result
    }
    set(value) {
        setHeader(value)
    }