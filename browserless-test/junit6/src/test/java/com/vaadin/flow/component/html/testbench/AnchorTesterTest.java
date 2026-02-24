/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.flow.component.html.testbench;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ViewPackages
class AnchorTesterTest extends UIUnitTest {
    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(AnchorView.class);
    }

    @Test
    void anchorClick_navigatesCorrectly() {
        Anchor anchor = new Anchor("anchor", "Home");
        UI.getCurrent().add(anchor);

        Assertions.assertEquals("anchor", test(anchor).getHref());
        Assertions.assertInstanceOf(AnchorView.class, test(anchor).navigate(),
                "Click anchor did not navigate to AnchorView");
    }

    @Test
    void anchorClick_navigatesCorrectlyWithParameters() {
        Anchor anchor = new Anchor("anchor?name=value", "Home");
        UI.getCurrent().add(anchor);

        Assertions.assertEquals("anchor?name=value", test(anchor).getHref());
        Assertions.assertEquals("anchor", test(anchor).getPath());
        Assertions.assertEquals("name=value",
                test(anchor).getQueryParameters().getQueryString());
        Assertions.assertInstanceOf(AnchorView.class, test(anchor).navigate(),
                "Click anchor did not navigate to AnchorView");
    }

    @Test
    void anchorClick_disabled_throws() {
        Anchor anchor = new Anchor("anchor", "Home");
        anchor.setEnabled(false);
        UI.getCurrent().add(anchor);

        assertThrows(IllegalStateException.class, () -> test(anchor).click());
    }

    @Test
    void anchorClick_notARegisteredRoute_throws() {
        Anchor anchor = new Anchor("error", "Home");
        UI.getCurrent().add(anchor);

        final IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> test(anchor).click());

        Assertions.assertEquals("Anchor is not for an application route",
                exception.getMessage());
    }

    @Test
    void anchorClick_streamRegistration_throws() {
        StreamResource resource = new StreamResource("filename",
                () -> new ByteArrayInputStream(
                        "Hello world".getBytes(StandardCharsets.UTF_8)));
        Anchor anchor = new Anchor(resource, "Home");
        UI.getCurrent().add(anchor);

        final IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> test(anchor).click());

        Assertions.assertEquals("Anchor target seems to be a resource",
                exception.getMessage());
    }

    @Test
    void anchorDownload_writesResourceToOutputStream() {
        StreamResource resource = new StreamResource("filename",
                () -> new ByteArrayInputStream(
                        "Hello world".getBytes(StandardCharsets.UTF_8)));

        Anchor anchor = new Anchor(resource, "Download");
        UI.getCurrent().add(anchor);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        test(anchor).download(outputStream);

        Assertions.assertEquals("Hello world",
                outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    void anchorDownload_disabled_throws() {
        DownloadHandler resource = event -> event.getOutputStream()
                .write("Hello world".getBytes(StandardCharsets.UTF_8));

        Anchor anchor = new Anchor(resource, "Download");
        anchor.setEnabled(false);
        UI.getCurrent().add(anchor);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        assertThrows(IllegalStateException.class,
                () -> test(anchor).download(outputStream));
    }

    @Test
    void anchorClick_downloadHandler_throws() {
        DownloadHandler resource = event -> event.getOutputStream()
                .write("Hello world".getBytes(StandardCharsets.UTF_8));
        Anchor anchor = new Anchor(resource, "Home");
        UI.getCurrent().add(anchor);

        final IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> test(anchor).click());

        Assertions.assertEquals("Anchor target seems to be a resource",
                exception.getMessage());
    }

    @Test
    void anchorDownload_downloadHandler_writesResourceToOutputStream() {
        DownloadHandler resource = event -> event.getOutputStream()
                .write("Hello world".getBytes(StandardCharsets.UTF_8));
        Anchor anchor = new Anchor(resource, "Download");
        UI.getCurrent().add(anchor);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        test(anchor).download(outputStream);

        Assertions.assertEquals("Hello world",
                outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    void anchorDownload_noStreamRegistration_throws() {
        Anchor anchor = new Anchor("anchor", "Download");
        UI.getCurrent().add(anchor);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> test(anchor).download(outputStream));

        Assertions.assertEquals("Anchor target does not seem to be a resource",
                exception.getMessage());
    }
}
