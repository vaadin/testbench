/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
package com.vaadin.testbench.screenshot;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    public void testGenerateName_shotChrome14inCapabilities_returnsGeneratedName() {
        Capabilities chrome = createNiceMock(Capabilities.class);
        expect(chrome.getPlatform()).andReturn(Platform.LINUX);
        expect(chrome.getBrowserName()).andReturn("Chrome");
        expect(chrome.getVersion()).andReturn("14.5");
        replay(chrome);
        String name = rng.generateName("shot", chrome);
        assertEquals("shot_linux_Chrome_14", name);
        verify(chrome);
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
    public void testGenerateName_barPhantomJSinCapabilities_returnsGeneratedName() {
        Capabilities phantom = createNiceMock(Capabilities.class);
        expect(phantom.getPlatform()).andReturn(Platform.MAC);
        expect(phantom.getBrowserName()).andReturn("phantomjs");
        expect(phantom.getVersion()).andReturn("phantomjs-1.8.1+ghostdriver-1.0.2");
        replay(phantom);
        String name = rng.generateName("bar", phantom);
        assertEquals("bar_mac_phantomjs_1", name);
        verify(phantom);
    }
}
