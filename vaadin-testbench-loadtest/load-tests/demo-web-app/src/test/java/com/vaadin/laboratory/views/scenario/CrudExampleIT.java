package com.vaadin.laboratory.views.scenario;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.datepicker.testbench.DatePickerElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.laboratory.views.AbstractIT;
import com.vaadin.testbench.BrowserTest;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.util.Random;

/**
 * User scenario for the CRUD Example view.
 * <p>
 * This TestBench test simulates a typical CRUD workflow:
 * <ol>
 *   <li>Select a random item in the grid</li>
 *   <li>Create a new item</li>
 *   <li>Select the newly created item</li>
 *   <li>Delete the item</li>
 * </ol>
 */
public class CrudExampleIT extends AbstractIT {

    private static final String TEST_FIRST_NAME = "TestFirstName";
    private static final String TEST_LAST_NAME = "TestLastName";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PHONE = "123456789";
    private static final LocalDate TEST_BIRTHDAY = LocalDate.of(1934, 1, 1);
    private static final String TEST_OCCUPATION = "Vaadin Expert";
    private static final String TEST_ROLE = "Model";

    @BrowserTest
    public void crudWorkflow() {
        GridElement grid = $(GridElement.class).waitForFirst();

        // 1. Select random item in grid
        selectRandomItemInGrid(grid);

        // 2. Create new item in grid
        createNewItemInGrid();

        // 3. Select the latest new item in grid
        grid = $(GridElement.class).first();
        selectLatestItemInGrid(grid);

        // 4. Delete the latest item in grid
        deleteLatestItemInGrid();
    }

    private void selectRandomItemInGrid(GridElement grid) {
        // Wait for grid rows to be loaded
        grid.scrollToRow(0);

        int rowCount = grid.getRowCount();
        Assertions.assertTrue(rowCount > 0, "Grid should have at least one row");

        int randomRow = new Random().nextInt(99);
        // Scroll to row to ensure it's rendered
        grid.scrollToRow(randomRow);
        grid.getRow(randomRow).select();

        // Verify selection by checking form is populated
        $(TextFieldElement.class).waitForFirst();
        TextFieldElement firstNameField = $(TextFieldElement.class).withCaption("First Name").single();
        Assertions.assertFalse(firstNameField.getValue().isEmpty(),
                "First name field should be populated after selection");
    }

    private void createNewItemInGrid() {
        // Click cancel to clear any existing selection
        $(ButtonElement.class).withCaption("Cancel").single().click();

        // Fill form with new person data
        $(TextFieldElement.class).waitForFirst();
        $(TextFieldElement.class).withCaption("First Name").single().setValue(TEST_FIRST_NAME);
        $(TextFieldElement.class).withCaption("Last Name").single().setValue(TEST_LAST_NAME);
        $(TextFieldElement.class).withCaption("Email").single().setValue(TEST_EMAIL);
        $(TextFieldElement.class).withCaption("Phone").single().setValue(TEST_PHONE);
        $(DatePickerElement.class).withCaption("Date Of Birth").single().setDate(TEST_BIRTHDAY);
        $(TextFieldElement.class).withCaption("Occupation").single().setValue(TEST_OCCUPATION);
        $(TextFieldElement.class).withCaption("Role").single().setValue(TEST_ROLE);

        // Save the new person
        $(ButtonElement.class).withCaption("Save").single().click();
    }

    private void selectLatestItemInGrid(GridElement grid) {
        // Find and select the row with our test data
        int rowCount = grid.getRowCount();
        boolean found = false;

        for (int i = 0; i < rowCount; i++) {
            String firstName = grid.getCell(i, 0).getText();
            String lastName = grid.getCell(i, 1).getText();
            if (TEST_FIRST_NAME.equals(firstName) && TEST_LAST_NAME.equals(lastName)) {
                grid.getRow(i).select();
                found = true;
                break;
            }
        }

        Assertions.assertTrue(found, "Newly created item should be found in grid");
    }

    private void deleteLatestItemInGrid() {
        // Delete the selected item
        $(ButtonElement.class).withId("delete-button").single().click();

        // Verify item was deleted by checking it no longer exists in grid
        GridElement grid = $(GridElement.class).first();
        int rowCount = grid.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            String firstName = grid.getCell(i, 0).getText();
            String lastName = grid.getCell(i, 1).getText();
            Assertions.assertFalse(
                    TEST_FIRST_NAME.equals(firstName) && TEST_LAST_NAME.equals(lastName),
                    "Deleted item should no longer exist in grid");
        }
    }

    @Override
    public String getViewName() {
        return "crud-example";
    }
}
