package junit.com.vaadin.vaadin.addons.testbench;

import com.vaadin.testbench.addons.webdriver.BrowserDriverFunctions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static com.vaadin.frp.matcher.Case.match;
import static com.vaadin.frp.matcher.Case.matchCase;
import static com.vaadin.frp.model.Result.failure;
import static com.vaadin.frp.model.Result.success;
import static com.vaadin.testbench.PropertiesResolver.propertyReader;
import static java.lang.Boolean.TRUE;
import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.fail;

public class BrowserDriverFunctionsTest {

    @Test
    @DisplayName("test reading properties")
    @Disabled("test is not not generic enough")
    void test001() {
        Properties properties = propertyReader()
                .apply(BrowserDriverFunctions.CONFIG_FOLDER + "config").get();


        final String unittestingTarget = valueOf(properties.get("unittesting.target")).trim();
        match(
                matchCase(() -> failure("no matching unittesting.target..")),
                matchCase(() -> "locale".equals(unittestingTarget), () -> success(TRUE)),
                matchCase(() -> "selenoid.rapidpm.org".equals(unittestingTarget), () -> success(TRUE)),
                matchCase(() -> "selenoid-server".equals(unittestingTarget), () -> success(TRUE))
        )
                .ifAbsent(fail("no expected property value found for unittesting.target.. " + unittestingTarget));
    }
}
