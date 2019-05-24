package com.vaadin.testbench.tests.component.grid;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.grid.testbench.GridColumnElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.grid.testbench.GridTHTDElement;
import com.vaadin.flow.component.grid.testbench.GridTRElement;
import com.vaadin.flow.component.grid.testbench.test.GridView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.junit5.pageobject.VaadinPageObject;
import com.vaadin.testbench.addons.webdriver.SkipBrowsers;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.openqa.selenium.NoSuchElementException;

import java.util.List;

import static com.vaadin.testbench.addons.webdriver.BrowserType.IE;

public class GridIT extends AbstractIT {

    public static final String FIRST_NAME = "First name";
    public static final String LAST_NAME = "Last name";
    public static final String AGE = "Age";

    @VaadinTest
    public void scrollToRow(VaadinPageObject po) {
        final GridElement header = po.$(GridElement.class).id(GridView.HEADER_MULTISELECT);
        final GridElement noHeader = po.$(GridElement.class).id(GridView.NO_HEADER);
        final GridElement tenMillion = po.$(GridElement.class).id(GridView.TEN_MILLION);

        for (int rowIndex : new int[]{0, 15, 82}) {
            header.scrollToRow(rowIndex);
            Assertions.assertEquals(rowIndex, header.getFirstVisibleRowIndex());
            noHeader.scrollToRow(rowIndex);
            Assertions.assertEquals(rowIndex, noHeader.getFirstVisibleRowIndex());
            tenMillion.scrollToRow(rowIndex);
            Assertions.assertEquals(rowIndex, tenMillion.getFirstVisibleRowIndex());
        }

        // Requires new grid release
        // tenMillion.scrollToRow(9000000);
        // Assertions.assertEquals(9000000, tenMillion.getFirstVisibleRowIndex());
    }

    @VaadinTest
    public void getPageSize(VaadinPageObject po) {
        final GridElement header = po.$(GridElement.class).id(GridView.HEADER_MULTISELECT);
        final GridElement noHeader = po.$(GridElement.class).id(GridView.NO_HEADER);
        final GridElement tenMillion = po.$(GridElement.class).id(GridView.TEN_MILLION);
        Assertions.assertEquals(50, header.getPageSize());
        Assertions.assertEquals(50, noHeader.getPageSize());
        Assertions.assertEquals(50, tenMillion.getPageSize());
    }

    @VaadinTest
    public void getFirstVisibleRowIndex(VaadinPageObject po) {
        final GridElement header = po.$(GridElement.class).id(GridView.HEADER_MULTISELECT);
        final GridElement noHeader = po.$(GridElement.class).id(GridView.NO_HEADER);
        final GridElement tenMillion = po.$(GridElement.class).id(GridView.TEN_MILLION);

        Assertions.assertEquals(0, header.getFirstVisibleRowIndex());
        Assertions.assertEquals(0, noHeader.getFirstVisibleRowIndex());
        Assertions.assertEquals(0, tenMillion.getFirstVisibleRowIndex());

        header.scrollToRow(50);
        noHeader.scrollToRow(50);
        tenMillion.scrollToRow(100);

        Assertions.assertEquals(50, header.getFirstVisibleRowIndex());
        Assertions.assertEquals(50, noHeader.getFirstVisibleRowIndex());
        Assertions.assertEquals(100, tenMillion.getFirstVisibleRowIndex());
    }

    @VaadinTest
    public void getRowCount(VaadinPageObject po) {
        final GridElement header = po.$(GridElement.class).id(GridView.HEADER_MULTISELECT);
        final GridElement noHeader = po.$(GridElement.class).id(GridView.NO_HEADER);
        final GridElement tenMillion = po.$(GridElement.class).id(GridView.TEN_MILLION);

        Assertions.assertEquals(100, header.getRowCount());
        Assertions.assertEquals(1000, noHeader.getRowCount());
        Assertions.assertEquals(10000000, tenMillion.getRowCount());
    }

    @VaadinTest
    public void getAllColumns(VaadinPageObject po) {
        final GridElement header = po.$(GridElement.class).id(GridView.HEADER_MULTISELECT);
        final GridElement noHeader = po.$(GridElement.class).id(GridView.NO_HEADER);
        final GridElement tenMillion = po.$(GridElement.class).id(GridView.TEN_MILLION);

        List<GridColumnElement> headerColumns = header.getAllColumns();
        Assertions.assertEquals(4, headerColumns.size());
        List<GridColumnElement> noHeaderColumns = noHeader.getAllColumns();
        Assertions.assertEquals(2, noHeaderColumns.size());
        List<GridColumnElement> tenMillionColumns = tenMillion.getAllColumns();
        Assertions.assertEquals(2, tenMillionColumns.size());

        Assertions.assertEquals("", headerColumns.get(0).getHeaderCell().getText());
        Assertions.assertEquals(FIRST_NAME,
                headerColumns.get(1).getHeaderCell().getText());
        Assertions.assertEquals("Last name",
                headerColumns.get(2).getHeaderCell().getText());
        Assertions.assertEquals("Age",
                headerColumns.get(3).getHeaderCell().getText());

        Assertions.assertEquals(headerColumns.get(0), header.getColumn(""));
    }

    @VaadinTest
    public void getHeaderCell(VaadinPageObject po) {
        final GridElement header = po.$(GridElement.class).id(GridView.HEADER_MULTISELECT);
        final GridElement noHeader = po.$(GridElement.class).id(GridView.NO_HEADER);
        final GridElement tenMillion = po.$(GridElement.class).id(GridView.TEN_MILLION);

        Assertions.assertEquals(FIRST_NAME, header.getHeaderCell(1).getText());
        Assertions.assertEquals(LAST_NAME, header.getHeaderCell(2).getText());
        Assertions.assertEquals(AGE, header.getHeaderCell(3).getText());

        // Header always exists but is hidden
        Assertions.assertEquals("", noHeader.getHeaderCell(0).getText());
        Assertions.assertEquals("", noHeader.getHeaderCell(1).getText());

        Assertions.assertEquals(FIRST_NAME,
                tenMillion.getHeaderCell(0).getText());
        Assertions.assertEquals(LAST_NAME, tenMillion.getHeaderCell(1).getText());
    }

    @VaadinTest
    public void getFooterCell(VaadinPageObject po) {
        final GridElement header = po.$(GridElement.class).id(GridView.HEADER_MULTISELECT);
        final GridElement noHeader = po.$(GridElement.class).id(GridView.NO_HEADER);
        final GridElement tenMillion = po.$(GridElement.class).id(GridView.TEN_MILLION);

        Assertions.assertEquals("First Footer",
                tenMillion.getFooterCell(0).getText());
        Assertions.assertEquals("Last Footer",
                tenMillion.getFooterCell(1).getText());

        // Footer always exists but is hidden
        Assertions.assertEquals("", header.getFooterCell(0).getText());
        Assertions.assertEquals("", header.getFooterCell(1).getText());
        Assertions.assertEquals("", header.getFooterCell(2).getText());

        Assertions.assertEquals("", noHeader.getFooterCell(0).getText());
        Assertions.assertEquals("", noHeader.getFooterCell(1).getText());
    }

    @VaadinTest
    public void getCell(VaadinPageObject po) {
        final GridElement header = po.$(GridElement.class).id(GridView.HEADER_MULTISELECT);
        final GridElement noHeader = po.$(GridElement.class).id(GridView.NO_HEADER);
        final GridElement tenMillion = po.$(GridElement.class).id(GridView.TEN_MILLION);

        Assertions.assertEquals("First Name 0", header.getCell(0, 1).getText());
        Assertions.assertEquals("First Name 0", noHeader.getCell(0, 0).getText());
        Assertions.assertEquals("First Name 0", tenMillion.getCell(0, 0).getText());

        Assertions.assertEquals("Last name 20", header.getCell(20, 2).getText());
        Assertions.assertEquals("Last name 100",
                noHeader.getCell(100, 1).getText());
        Assertions.assertEquals("Last name 1000",
                tenMillion.getCell(1000, 1).getText());
        Assertions.assertEquals("Last name 1000000",
                tenMillion.getCell(1000000, 1).getText());
    }

    @VaadinTest
    public void getRow(VaadinPageObject po) {
        final GridElement header = po.$(GridElement.class).id(GridView.HEADER_MULTISELECT);

        GridTRElement row = header.getRow(5);
        GridColumnElement headerColumn = header.getAllColumns().get(2);
        GridTHTDElement cell = row.getCell(headerColumn);
        Assertions.assertEquals("Last name 5", cell.getText());
    }

    @VaadinTest
    @Disabled
    public void singleSelect(VaadinPageObject po) {
        final GridElement noHeader = po.$(GridElement.class).id(GridView.NO_HEADER);

        Assertions.assertFalse(noHeader.getRow(4).isSelected());
        noHeader.getRow(4).select();
        Assertions.assertTrue(noHeader.getRow(4).isSelected());
        Assertions.assertEquals(
                "1. Grid 'noheader' selection changed to 'Person [firstName=First Name 4, lastName=Last name 4, age=4]'",
                getLogRow(po, 0));

        noHeader.select(3);
        Assertions.assertFalse(noHeader.getRow(4).isSelected());
        Assertions.assertTrue(noHeader.getRow(3).isSelected());
        Assertions.assertEquals(
                "2. Grid 'noheader' selection changed to 'Person [firstName=First Name 3, lastName=Last name 3, age=3]'",
                getLogRow(po, 0));

        noHeader.select(3); // NO-OP
        Assertions.assertTrue(noHeader.getRow(3).isSelected());
        Assertions.assertEquals(
                "2. Grid 'noheader' selection changed to 'Person [firstName=First Name 3, lastName=Last name 3, age=3]'",
                getLogRow(po, 0));

        noHeader.deselect(2); // NO-OP
        Assertions.assertTrue(noHeader.getRow(3).isSelected());
        Assertions.assertEquals(
                "2. Grid 'noheader' selection changed to 'Person [firstName=First Name 3, lastName=Last name 3, age=3]'",
                getLogRow(po, 0));

        noHeader.deselect(3);
        Assertions.assertFalse(noHeader.getRow(3).isSelected());
        Assertions.assertEquals("3. Grid 'noheader' selection changed to ''",
                getLogRow(po, 0));
    }

    @VaadinTest
    public void multiSelect(VaadinPageObject po) {
        final GridElement header = po.$(GridElement.class).id(GridView.HEADER_MULTISELECT);

        header.select(0);
        Assertions.assertTrue(header.getRow(0).isSelected());

        // https://github.com/vaadin/vaadin-grid-flow/issues/74
        // Assertions.assertEquals(
        // "1. Grid 'header' selection changed to 'Person [firstName=First Name
        // 0, lastName=Last name 0, age=0]'",
        // getLogRow(0));

        header.select(1);
        Assertions.assertTrue(header.getRow(0).isSelected());
        Assertions.assertTrue(header.getRow(1).isSelected());
        // https://github.com/vaadin/vaadin-grid-flow/issues/74
        // Assertions.assertEquals(
        // "2. Grid 'header' selection changed to 'Person [firstName=First Name
        // 0, lastName=Last name 0, age=0], Person [firstName=First Name 1,
        // lastTName=Last name 0, age=0]'",
        // getLogRow(0));

        header.deselect(0);
        Assertions.assertFalse(header.getRow(0).isSelected());
        Assertions.assertTrue(header.getRow(1).isSelected());
        // https://github.com/vaadin/vaadin-grid-flow/issues/74
        // Assertions.assertEquals(
        // "3. Grid 'header' selection changed to 'Person [firstName=First Name
        // 1, lastName=Last name 1, age=1]'",
        // getLogRow(0));
    }

    @VaadinTest
    public void getCellByContents(VaadinPageObject po) {
        final GridElement header = po.$(GridElement.class).id(GridView.HEADER_MULTISELECT);
        final GridElement noHeader = po.$(GridElement.class).id(GridView.NO_HEADER);

        GridTHTDElement cell = header.getCell("Last name 2");
        Assertions.assertEquals(LAST_NAME,
                cell.getColumn().getHeaderCell().getText());
        Assertions.assertEquals(2, cell.getRow());

        cell = header.getCell("15");
        Assertions.assertEquals("Age", cell.getColumn().getHeaderCell().getText());
        Assertions.assertEquals(15, cell.getRow());

        cell = noHeader.getCell("First Name 12");
        Assertions.assertEquals("", cell.getColumn().getHeaderCell().getText());
        Assertions.assertEquals(12, cell.getRow());
    }

    @VaadinTest
    public void interactWithComponentsInGrid(VaadinPageObject po) {
        GridElement components = po.$(GridElement.class).id(GridView.COMPONENTS);
        GridTHTDElement cell = components.getCell(0, 0);
        ButtonElement button = cell.$(ButtonElement.class).first();
        button.click();
        Assertions.assertEquals("Click on button 'First Name 0'",
                getLogRowWithoutNumber(po, 0));
    }

    /**
     * https://github.com/vaadin/vaadin-grid-flow/issues/136
     *
     * @param po
     */
    @VaadinTest
    @SkipBrowsers(IE)
    public void detailsRows(VaadinPageObject po) {
        GridElement details = po.$(GridElement.class).id(GridView.DETAILS);
        GridTHTDElement cell = details.getCell(9, 0);
        cell.click();
        GridTRElement rowElement = cell.getRowElement();
        GridTHTDElement detailsRow = rowElement.getDetailsRow();
        List<TestBenchElement> texts = detailsRow.$("span").all();
        Assertions.assertEquals(2, texts.size());
        Assertions.assertEquals("First Name 9", texts.get(0).getText());
        Assertions.assertEquals("Last name 9", texts.get(1).getText());

        ButtonElement button = detailsRow.$(ButtonElement.class).first();
        button.click();
        Assertions.assertEquals(
                "Hello Person [firstName=First Name 9, lastName=Last name 9, age=9]",
                getLogRowWithoutNumber(po, 0));
    }

    @VaadinTest
    public void getCellByContentsOutsideView(VaadinPageObject po) {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            po.$(GridElement.class).id(GridView.TEN_MILLION).getCell("Last name 2000");
        });
    }
}
