/**
 * Copyright 2000-2026 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See {@literal <https://vaadin.com/commercial-license-and-service-terms>} for the full
 * license.
 */
package com.vaadin.flow.component.spreadsheet.testbench;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.vaadin.testbench.By;
import com.vaadin.testbench.Dimension;
import com.vaadin.testbench.NoSuchElementException;
import com.vaadin.testbench.Point;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;

/**
 * This is the base element class for accessing a Vaadin Spreadsheet component
 * for TestBench testing.
 *
 * @author Vaadin Ltd.
 */
@Element("vaadin-spreadsheet")
public class SpreadsheetElement extends TestBenchElement {

    private Optional<TestBenchElement> getSpreadsheetInShadowRoot() {
        return this.$(TestBenchElement.class).all().stream()
                .filter(el -> el.hasClassName("v-spreadsheet")).findFirst();
    }

    private TestBenchElement findElementInShadowRoot(By by) {
        return getSpreadsheetInShadowRoot().map(el -> el.findElement(by))
                .orElse(null);
    }

    private List<TestBenchElement> findElementsInShadowRoot(By by) {
        return getSpreadsheetInShadowRoot().map(el -> el.findElements(by))
                .orElse(Collections.emptyList());
    }

    /**
     * Gets the cell element at the given coordinates for the currently active
     * sheet. Throws NoSuchElementException if the cell is outside the visible
     * area.
     *
     * @param row
     *            Row index, 1-based
     * @param column
     *            Column index, 1-based
     * @return Cell element at the given index.
     * @throws NoSuchElementException
     *             if the cell at (row, column) is not found.
     */
    public SheetCellElement getCellAt(int row, int column) {
        // Cells are divs, while the inline editor is an input that carries
        // the same col/row/cell class names as the cell being edited. The
        // div prefix keeps the editor from being matched as a second cell.
        String selector = String.format("div.col%d.row%d.cell", column, row);
        String mergedSelector = selector + ".merged-cell";
        // Single async JS call to find the cell, preferring merged cell
        // when multiple matches exist. Polls to handle cases where the
        // cell hasn't been rendered yet (e.g. after scrolling).
        TestBenchElement cell = (TestBenchElement) getCommandExecutor()
                .executeAsyncScript(
                        """
                                const root = arguments[0].shadowRoot
                                    .querySelector('.v-spreadsheet');
                                const selector = arguments[1];
                                const mergedSelector = arguments[2];
                                const callback = arguments[arguments.length - 1];

                                const MAX_ATTEMPTS = 50;
                                const delay = () => new Promise(r => setTimeout(r, 100));

                                for (let i = 0; i < MAX_ATTEMPTS; i++) {
                                    const cells = root.querySelectorAll(selector);
                                    if (cells.length > 1) {
                                        callback(root.querySelector(mergedSelector));
                                        return;
                                    } else if (cells.length === 1) {
                                        callback(cells[0]);
                                        return;
                                    }
                                    await delay();
                                }
                                callback(null);
                                """,
                        this, selector, mergedSelector);
        if (cell == null) {
            throw new NoSuchElementException(
                    "Cell not found: row=" + row + ", column=" + column);
        }
        SheetCellElement cellElement = cell.wrap(SheetCellElement.class);
        cellElement.setParent(this);
        return cellElement;
    }

    /**
     * Gets the cell element at the given cell address for the currently active
     * sheet. Throws NoSuchElementException if the cell is outside the visible
     * area.
     *
     * @param cellAddress
     *            Target address, e.g. A3
     * @return Cell element at the given index.
     * @throws NoSuchElementException
     *             if the cell at (cellAddress) is not found.
     */
    public SheetCellElement getCellAt(String cellAddress) {
        Point point = AddressUtil.addressToPoint(cellAddress);
        return getCellAt(point.getY(), point.getX());
    }

    /**
     * Gets the row header element at the given index.
     *
     * @param rowIndex
     *            Index of target row, 1-based
     * @return Header of the row at the given index
     */
    public SheetHeaderElement getRowHeader(int rowIndex) {
        TestBenchElement cell = (TestBenchElement) findElementInShadowRoot(
                By.cssSelector(String.format(".rh.row%d", rowIndex)));
        return cell.wrap(SheetHeaderElement.class);
    }

    /**
     * Gets the column header element at the given index.
     *
     * @param columnIndex
     *            Index of target column, 1-based
     * @return Header of the column at the given index
     */
    public SheetHeaderElement getColumnHeader(int columnIndex) {
        TestBenchElement cell = (TestBenchElement) findElementInShadowRoot(
                By.cssSelector(String.format(".ch.col%d", columnIndex)));
        return cell.wrap(SheetHeaderElement.class);
    }

    /**
     * Gets the address field. The address field contains the address of the
     * cell that was last clicked, or A1 if no clicks have yet been made.
     *
     * @return Address field element
     */
    public TestBenchElement getAddressField() {
        return (TestBenchElement) findElementInShadowRoot(
                By.className("addressfield"));
    }

    /**
     * Gets the formula field. This field is where the user can input data for
     * the cell whose address the address field currently contains.
     *
     * @return Formula field element
     */
    public TestBenchElement getFormulaField() {
        return (TestBenchElement) findElementInShadowRoot(
                By.className("functionfield"));
    }

    /**
     * Gets the info label. Info label is the small text at the bottom right
     * corner of the Spreadsheet.
     *
     * @return Info label element
     */
    public TestBenchElement getInfoLabel() {
        return (TestBenchElement) findElementInShadowRoot(
                By.className("sheet-tabsheet-infolabel"));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.testbench.TestBenchElement#scroll(int)
     */
    @Override
    public void scroll(int scrollTop) {
        getBottomRightPane().scroll(scrollTop);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.testbench.TestBenchElement#scrollLeft(int)
     */
    @Override
    public void scrollLeft(int scrollLeft) {
        getBottomRightPane().scrollLeft(scrollLeft);
    }

    /**
     * Scrolls the sheet selector to the beginning.
     *
     * Has no effect if there are not enough sheets to require scrolling.
     */
    public void scrollSheetsToStart() {
        findElementInShadowRoot(By.className("scroll-tabs-beginning")).click();
    }

    /**
     * Scrolls the sheet selector to the end.
     *
     * Has no effect if there are not enough sheets to require scrolling.
     */
    public void scrollSheetsToEnd() {
        findElementInShadowRoot(By.className("scroll-tabs-end")).click();
    }

    /**
     * Scrolls the sheet selector left or right by the given amount.
     *
     * Has no effect if there are not enough sheets to require scrolling.
     *
     * @param amount
     *            Amount to scroll. Positive numbers scroll to the right and
     *            negative numbers scroll to the left.
     */
    public void scrollSheets(int amount) {
        TestBenchElement target = findElementInShadowRoot(By.className(
                amount > 0 ? "scroll-tabs-right" : "scroll-tabs-left"));
        for (int i = 0; i < amount; i++) {
            target.click();
        }
    }

    /**
     * Selects the sheet at the given index. Indexes are counted only for
     * visible sheets.
     *
     * @param sheetIndex
     *            Index of sheet to select, 0-based
     */
    public void selectSheetAt(int sheetIndex) {
        TestBenchElement tabContainer = findElementInShadowRoot(
                By.className("sheet-tabsheet-container"));
        List<TestBenchElement> tabs = tabContainer.findElements(By.cssSelector("*"));
        TestBenchElement target = tabs.get(sheetIndex);
        scrollSheetVisible(target);
        target.click();
    }

    /**
     * Selects the sheet with the given name. Only visible sheets can be
     * selected.
     *
     * @param sheetName
     *            Name of sheet to select
     */
    public void selectSheet(String sheetName) {
        TestBenchElement tabContainer = findElementInShadowRoot(
                By.className("sheet-tabsheet-container"));
        List<TestBenchElement> tabs = tabContainer.findElements(By.cssSelector("*"));
        for (TestBenchElement tab : tabs) {
            if (tab.getText().equals(sheetName)) {
                scrollSheetVisible(tab);
                tab.click();
                break;
            }
        }
    }

    /**
     * Adds a new sheet.
     */
    public void addSheet() {
        findElementInShadowRoot(By.className("add-new-tab")).click();
    }

    /**
     * Fetches the context menu for the spreadsheet
     *
     * @return {@link SpreadsheetElement.ContextMenuElement}
     * @throws java.util.NoSuchElementException
     *             if the menu isn't open
     */
    public ContextMenuElement getContextMenu() {
        try {
            return $("*").withClassName("v-contextmenu").onPage().get(0)
                    .wrap(ContextMenuElement.class);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Context menu not found", e);
        }
    }

    public static class ContextMenuElement extends TestBenchElement {

        /**
         * @throws NoSuchElementException
         *             if no item's text contains {@code text}
         */
        public TestBenchElement getItem(String text) {
            // The XPath .//table//tr[*]//td//div[contains(text(), "...")]:
            // contains() sees only the div's first text node
            TestBenchElement item = (TestBenchElement) executeScript("""
                    for (const div of arguments[0].querySelectorAll('table tr td div')) {
                      const textNode = Array.from(div.childNodes)
                          .find(node => node.nodeType === Node.TEXT_NODE);
                      if ((textNode ? textNode.nodeValue : '').includes(arguments[1])) {
                        return div;
                      }
                    }
                    return null;
                    """, this, text);
            if (item == null) {
                throw new NoSuchElementException(
                        "No context menu item contains " + text);
            }
            return item;
        }

    }

    private void scrollSheetVisible(TestBenchElement targetSheet) {
        // Make sure the target sheet is visible
        if (!targetSheet.isDisplayed()) {
            scrollSheetsToStart();
            while (!targetSheet.isDisplayed()) {
                scrollSheets(1);
            }
        }
    }

    // Current selection
    private TestBenchElement sTop;
    private TestBenchElement sBottom;
    private TestBenchElement sRight;
    private Point sLocation;
    private Dimension sSize;

    boolean isElementSelected(TestBenchElement element) {
        updateSelectionLocationAndSize();
        Point location = element.getLocation();
        int centerX = location.x + element.getSize().getWidth() / 2;
        int centerY = location.y + element.getSize().getHeight() / 2;
        return isInSelection(centerX, centerY)
                || isNonCoherentlySelected(element);
    }

    private void findSelectionOutline() {
        // sometimes the spreadsheet takes so long to load that the selection
        // widget elements are not found
        waitUntil(e -> findElementInShadowRoot(By.className("s-top")) != null);
        sTop = findElementInShadowRoot(By.className("s-top"));
        sBottom = findElementInShadowRoot(By.className("s-bottom"));
        // Just to make sure the left element is present
        findElementInShadowRoot(By.className("s-left"));
        sRight = findElementInShadowRoot(By.className("s-right"));
    }

    private boolean isNonCoherentlySelected(TestBenchElement element) {
        // an element is non-coherently selected if the class attribute
        // contains "cell-range"
        return Optional.ofNullable(element.getDomAttribute("class")).orElse("")
                .contains("cell-range")
                || "solid".equals(element.getCssValue("outline-style"));
    }

    private void updateSelectionLocationAndSize() {
        if (sTop == null) {
            findSelectionOutline();
        }
        sLocation = sTop.getLocation();
        int bottomY = sBottom.getLocation().getY();
        int bottomH = sBottom.getSize().getHeight();
        int rightX = sRight.getLocation().getX();
        int rightW = sRight.getSize().getWidth();
        sSize = new Dimension(rightX + rightW - sLocation.getX(),
                bottomY + bottomH - sLocation.getY());
    }

    private boolean isInSelection(int locationX, int locationY) {
        // Test top left corner
        if (locationX < sLocation.getX() || locationY < sLocation.getY()) {
            return false;
        }
        // Test lower right corner
        if (locationX - sLocation.getX() > sSize.getWidth()
                || locationY - sLocation.getY() > sSize.getHeight()) {
            return false;
        }
        // Everything is inside the selection
        return true;
    }

    public TestBenchElement getCellValueInput() {
        return findElementInShadowRoot(By.id("cellinput"));
    }

    /**
     * Determine if the pop-up of PopupuButton is currently visible
     */
    public boolean isPopupButtonPopupVisible() {
        return $("*").withClassName("v-spreadsheet-popupbutton-overlay")
                .onPage().exists();
    }

    private TestBenchElement getBottomRightPane() {
        return findElementInShadowRoot(By.className("bottom-right-pane"));
    }
}
