/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.screenshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;

public class ReferenceNameGeneratorTest {

    private ReferenceNameGenerator rng;

    @Before
    public void setUp() {
        rng = new ReferenceNameGenerator();
    }

    @Test
    public void testCreateReferenceNameGenerator() {
        assertNotNull(rng);
    }

    @Test
    public void testGenerateName_shotFirefox11inCapabilities_returnsGeneratedName() {
        Capabilities ffcaps = Mockito.mock(Capabilities.class);
        Mockito.when(ffcaps.getPlatformName()).thenReturn(Platform.XP);
        Mockito.when(ffcaps.getBrowserName()).thenReturn("Firefox");
        Mockito.when(ffcaps.getBrowserVersion()).thenReturn("13.0.1");
        String name = rng.generateName("shot", ffcaps);
        assertEquals("shot_windows_Firefox_13", name);
    }

    @Test
    public void testGenerateName_shotNoPlatformInCapabilities_returnsGeneratedName() {
        Capabilities someBrowser = Mockito.mock(Capabilities.class);
        Mockito.when(someBrowser.getBrowserName()).thenReturn("SomeBrowser");
        Mockito.when(someBrowser.getBrowserVersion()).thenReturn("12.3");
        String name = rng.generateName("shot", someBrowser);
        assertEquals("shot_unknown_SomeBrowser_12", name);
    }

    @Test
    public void testGenerateName_fooSafari5inCapabilities_returnsGeneratedName() {
        Capabilities safari = Mockito.mock(Capabilities.class);
        Mockito.when(safari.getPlatformName()).thenReturn(Platform.MAC);
        Mockito.when(safari.getBrowserName()).thenReturn("Safari");
        Mockito.when(safari.getBrowserVersion()).thenReturn("5");
        String name = rng.generateName("foo", safari);
        assertEquals("foo_mac_Safari_5", name);
    }

    @Test
    public void testGenerateName_shotEdgeinCapabilities_returnsGeneratedName() {
        Capabilities chrome = Mockito.mock(Capabilities.class);
        Mockito.when(chrome.getPlatformName()).thenReturn(Platform.XP);
        Mockito.when(chrome.getBrowserName()).thenReturn("MicrosoftEdge");
        Mockito.when(chrome.getBrowserVersion()).thenReturn("");
        Mockito.when(chrome.getCapability("browserVersion")).thenReturn("25");
        String name = rng.generateName("shot", chrome);
        assertEquals("shot_windows_MicrosoftEdge_25", name);
    }

    @Test
    public void linuxUsedInScreenshotName() {
        Capabilities chrome = Mockito.mock(Capabilities.class);
        Mockito.when(chrome.getPlatformName()).thenReturn(Platform.LINUX);
        Mockito.when(chrome.getBrowserName()).thenReturn("Chrome");
        Mockito.when(chrome.getBrowserVersion()).thenReturn("");
        Mockito.when(chrome.getCapability("browserVersion")).thenReturn("25");
        String name = rng.generateName("shot", chrome);
        assertEquals("shot_linux_Chrome_25", name);

    }
}
