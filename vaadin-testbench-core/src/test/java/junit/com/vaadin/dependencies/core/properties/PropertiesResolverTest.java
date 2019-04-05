package junit.com.vaadin.dependencies.core.properties;

import com.vaadin.testbench.PropertiesResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PropertiesResolverTest {

    private PropertiesResolver propertiesResolver;

    @BeforeEach
    void setup() {
        propertiesResolver = new PropertiesResolver();
    }

    @Test
    @DisplayName("load from resource")
    void test001() {
        Properties properties = propertiesResolver.get("test001");

        assertNotNull(properties);

        assertEquals("20", properties.get("a.a"));
        assertEquals("Hello", properties.getProperty("a.b"));
    }

    @Test
    @DisplayName("load from resource and working dir")
    void test002() {
        Properties properties = propertiesResolver.get("test002");

        assertNotNull(properties);

        assertEquals("20", properties.get("a.a"));
        assertEquals("Hello", properties.getProperty("a.b"));
    }

    @Test
    @DisplayName("load from home dir")
    void test003() throws IOException {
        String homeDir = System.getProperty("user.home");
        File file = new File(homeDir, "test003.properties");
        createPropertiesFile(file);
        Properties properties = propertiesResolver.get("test003");

        assertNotNull(properties);

        assertEquals("20", properties.get("a.a"));
        assertEquals("Hello", properties.getProperty("a.b"));
    }

    @Test
    @DisplayName("load from dir specified in enviroment")
    void test004() throws IOException {
        Path tempDir = Files.createTempDirectory(getClass().getSimpleName());
        System.setProperty(PropertiesResolver.CONFIG_LOCATION_PROPERTY, tempDir.toString());

        File file = tempDir.resolve("test004.properties").toFile();

        createPropertiesFile(file);
        Properties properties = propertiesResolver.get("test004");

        assertNotNull(properties);

        assertEquals("20", properties.get("a.a"));
        assertEquals("Hello", properties.getProperty("a.b"));
    }

    private void createPropertiesFile(File file) throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/test001.properties");
             OutputStream os = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}
