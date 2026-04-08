/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.laboratory.views.scenario;

import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.vaadin.testbench.loadtest.PlaywrightHelper;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * User scenario for the CRUD Example view.
 * <p>
 * This Playwright test simulates a typical CRUD workflow:
 * <ol>
 * <li>Select a random item in the grid</li>
 * <li>Create a new item</li>
 * <li>Select the newly created item</li>
 * <li>Delete the item</li>
 * </ol>
 */
public class CrudExamplePlaywrightIT {

    private static final String TEST_FIRST_NAME = "TestFirstName";
    private static final String TEST_LAST_NAME = "TestLastName";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PHONE = "123456789";
    private static final String TEST_DATE_OF_BIRTH = "1/1/1934";
    private static final String TEST_OCCUPATION = "Vaadin Expert";
    private static final String TEST_ROLE = "Model";

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(true));
        context = PlaywrightHelper.createBrowserContext(browser);
        page = context.newPage();
        page.navigate(PlaywrightHelper.getBaseUrl() + "/crud-example");
    }

    @AfterEach
    public void tearDown() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Test
    public void crudWorkflow() {
        Locator grid = page.locator("vaadin-grid");
        grid.waitFor();

        // 1. Select random item in grid
        selectRandomItemInGrid(grid);

        // 2. Create new item in grid
        createNewItemInGrid();

        // 3. Select the latest new item in grid
        selectLatestItemInGrid(grid);

        // 4. Delete the latest item in grid
        deleteLatestItemInGrid(grid);
    }

    private void selectRandomItemInGrid(Locator grid) {
        // Get row count via grid API
        int rowCount = (int) grid
                .evaluate("g => g._dataProviderController.rootCache.size");
        assertTrue(rowCount > 0, "Grid should have at least one row");

        int randomRow = new Random().nextInt(Math.min(rowCount, 99));

        // Scroll to and activate the row (this triggers the selection event)
        grid.evaluate(
                "(g, idx) => {"
                        + "  g.scrollToIndex(idx);"
                        + "  g.activeItem = g._dataProviderController"
                        + "    .rootCache.items[idx];"
                        + "}",
                randomRow);

        // Verify form is populated
        assertThat(page.getByLabel("First Name")).not().hasValue("");
    }

    private void createNewItemInGrid() {
        // Click cancel to clear any existing selection
        page.locator("#cancel-button").click();

        // Fill form with new person data
        page.getByLabel("First Name").fill(TEST_FIRST_NAME);
        page.getByLabel("Last Name").fill(TEST_LAST_NAME);
        page.getByLabel("Email").fill(TEST_EMAIL);
        page.getByLabel("Phone").fill(TEST_PHONE);
        page.getByLabel("Date Of Birth").fill(TEST_DATE_OF_BIRTH);
        page.getByLabel("Occupation").fill(TEST_OCCUPATION);
        page.getByLabel("Role").fill(TEST_ROLE);

        // Save the new person
        page.locator("#save-button").click();
    }

    private void selectLatestItemInGrid(Locator grid) {
        // Find the row with our test data by iterating grid items
        int rowIndex = (int) grid.evaluate(
                "(g, name) => {"
                        + "  const items = g._dataProviderController"
                        + "    .rootCache.items;"
                        + "  for (let i = 0; i < items.length; i++) {"
                        + "    if (items[i] && items[i].firstName === name) {"
                        + "      return i;"
                        + "    }"
                        + "  }"
                        + "  return -1;"
                        + "}",
                TEST_FIRST_NAME);

        assertTrue(rowIndex >= 0,
                "Newly created item should be found in grid");

        // Scroll to and select the row
        grid.evaluate(
                "(g, idx) => {"
                        + "  g.scrollToIndex(idx);"
                        + "  g.activeItem = g._dataProviderController"
                        + "    .rootCache.items[idx];"
                        + "}",
                rowIndex);
    }

    private void deleteLatestItemInGrid(Locator grid) {
        // Delete the selected item
        page.locator("#delete-button").click();

        // Verify item was deleted
        int rowIndex = (int) grid.evaluate(
                "(g, name) => {"
                        + "  const items = g._dataProviderController"
                        + "    .rootCache.items;"
                        + "  for (let i = 0; i < items.length; i++) {"
                        + "    if (items[i] && items[i].firstName === name) {"
                        + "      return i;"
                        + "    }"
                        + "  }"
                        + "  return -1;"
                        + "}",
                TEST_FIRST_NAME);

        assertTrue(rowIndex < 0,
                "Deleted item should no longer exist in grid");
    }
}
