package com.vaadin.testbench.screenshot;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;

public class ReferenceNameGeneratorTest {

    @Test
    public void testCreateReferenceNameGenerator() {
        ReferenceNameGenerator rng = new ReferenceNameGenerator();
        assertNotNull(rng);
    }

    @Test
    public void testGenerateName_shotFirefox11inCapabilities_returnsGeneratedName() {
        ReferenceNameGenerator rng = new ReferenceNameGenerator();
        Capabilities ffcaps = createNiceMock(Capabilities.class);
        expect(ffcaps.getPlatform()).andReturn(Platform.XP);
        expect(ffcaps.getBrowserName()).andReturn("Firefox");
        expect(ffcaps.getVersion()).andReturn("11");
        replay(ffcaps);
        String name = rng.generateName("shot", ffcaps);
        assertEquals("shot_xp_Firefox_11", name);
        verify(ffcaps);
    }

    @Test
    public void testGenerateName_shotChrome14inCapabilities_returnsGeneratedName() {
        ReferenceNameGenerator rng = new ReferenceNameGenerator();
        Capabilities chrome = createNiceMock(Capabilities.class);
        expect(chrome.getPlatform()).andReturn(Platform.LINUX);
        expect(chrome.getBrowserName()).andReturn("Chrome");
        expect(chrome.getVersion()).andReturn("14");
        replay(chrome);
        String name = rng.generateName("shot", chrome);
        assertEquals("shot_linux_Chrome_14", name);
        verify(chrome);
    }

    @Test
    public void testGenerateName_fooSafari5inCapabilities_returnsGeneratedName() {
        ReferenceNameGenerator rng = new ReferenceNameGenerator();
        Capabilities safari = createNiceMock(Capabilities.class);
        expect(safari.getPlatform()).andReturn(Platform.MAC);
        expect(safari.getBrowserName()).andReturn("Safari");
        expect(safari.getVersion()).andReturn("5");
        replay(safari);
        String name = rng.generateName("foo", safari);
        assertEquals("foo_mac_Safari_5", name);
        verify(safari);
    }
}
