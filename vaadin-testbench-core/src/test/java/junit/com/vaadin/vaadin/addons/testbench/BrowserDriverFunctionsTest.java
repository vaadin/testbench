package junit.com.vaadin.vaadin.addons.testbench;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.testbench.addons.webdriver.BrowserDriverFunctions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.vaadin.testbench.PropertiesResolver.readProperties;
import static java.lang.String.valueOf;

public class BrowserDriverFunctionsTest {

    @Test
    @DisplayName("test reading properties")
    @Disabled("test is not not generic enough")
    void test001() {
        final Properties properties = readProperties(
                BrowserDriverFunctions.CONFIG_FOLDER + "config");

        final String unittestingTarget = valueOf(properties.get("unittesting.target")).trim();

        final List<String> expected = Arrays.asList("local", "selenoid.rapidpm.org", "selenoid-server");
        Assertions.assertTrue(expected.contains(unittestingTarget),
                "No expected property value found for unittesting.target");

    }
}
