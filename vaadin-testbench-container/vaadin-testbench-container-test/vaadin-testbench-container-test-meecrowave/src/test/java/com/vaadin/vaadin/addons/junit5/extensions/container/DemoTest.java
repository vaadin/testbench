package com.vaadin.vaadin.addons.junit5.extensions.container;

import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfoExtension;
import com.vaadin.testbench.addons.junit5.extensions.container.ServletContainerExtension;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(ServletContainerExtension.class)
//@MeecrowaveConfig - would start the meecrowave again on beforeAll level
public class DemoTest {

    private static OkHttpClient client = new OkHttpClient();
    @RegisterExtension
    ContainerInfoExtension config = new ContainerInfoExtension();

    @Test
    public void test001(TestReporter reporter) {
        final String url = "http://" + config.host() + ":" + config.port() + "?value=HalloNase";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string().trim();
            assertEquals("HALLONASE", result);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
