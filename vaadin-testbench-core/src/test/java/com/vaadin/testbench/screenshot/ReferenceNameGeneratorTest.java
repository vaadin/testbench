/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.screenshot;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
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
        Capabilities ffcaps = createNiceMock(Capabilities.class);
        expect(ffcaps.getPlatform()).andReturn(Platform.XP);
        expect(ffcaps.getBrowserName()).andReturn("Firefox");
        expect(ffcaps.getVersion()).andReturn("13.0.1");
        replay(ffcaps);
        String name = rng.generateName("shot", ffcaps);
        assertEquals("shot_xp_Firefox_13", name);
        verify(ffcaps);
    }

    @Test
    public void testGenerateName_shotNoPlatformInCapabilities_returnsGeneratedName() {
        Capabilities someBrowser = createNiceMock(Capabilities.class);
        expect(someBrowser.getBrowserName()).andReturn("SomeBrowser");
        expect(someBrowser.getVersion()).andReturn("12.3");
        replay(someBrowser);
        String name = rng.generateName("shot", someBrowser);
        assertEquals("shot_unknown_SomeBrowser_12", name);
        verify(someBrowser);
    }

    @Test
    public void testGenerateName_fooSafari5inCapabilities_returnsGeneratedName() {
        Capabilities safari = createNiceMock(Capabilities.class);
        expect(safari.getPlatform()).andReturn(Platform.MAC);
        expect(safari.getBrowserName()).andReturn("Safari");
        expect(safari.getVersion()).andReturn("5");
        replay(safari);
        String name = rng.generateName("foo", safari);
        assertEquals("foo_mac_Safari_5", name);
        verify(safari);
    }

    @Test
    public void testGenerateName_shotEdgeinCapabilities_returnsGeneratedName() {
        Capabilities chrome = createNiceMock(Capabilities.class);
        expect(chrome.getPlatform()).andReturn(Platform.XP);
        expect(chrome.getBrowserName()).andReturn("MicrosoftEdge");
        expect(chrome.getVersion()).andReturn("");
        expect(chrome.getCapability("browserVersion")).andReturn("25");
        replay(chrome);
        String name = rng.generateName("shot", chrome);
        assertEquals("shot_xp_MicrosoftEdge_25", name);
        verify(chrome);
    }
}
