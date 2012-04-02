package com.vaadin.testbench.screenshot;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openqa.selenium.Capabilities;

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
        expect(ffcaps.getBrowserName()).andReturn("Firefox");
        expect(ffcaps.getVersion()).andReturn("11");
        replay(ffcaps);
        String name = rng.generateName("shot", ffcaps);
        assertEquals("shot_Firefox_11", name);
        verify(ffcaps);
    }

    @Test
    public void testGenerateName_shotChrome14inCapabilities_returnsGeneratedName() {
        ReferenceNameGenerator rng = new ReferenceNameGenerator();
        Capabilities chrome = createNiceMock(Capabilities.class);
        expect(chrome.getBrowserName()).andReturn("Chrome");
        expect(chrome.getVersion()).andReturn("14");
        replay(chrome);
        String name = rng.generateName("shot", chrome);
        assertEquals("shot_Chrome_14", name);
        verify(chrome);
    }

    @Test
    public void testGenerateName_fooSafari5inCapabilities_returnsGeneratedName() {
        ReferenceNameGenerator rng = new ReferenceNameGenerator();
        Capabilities chrome = createNiceMock(Capabilities.class);
        expect(chrome.getBrowserName()).andReturn("Safari");
        expect(chrome.getVersion()).andReturn("5");
        replay(chrome);
        String name = rng.generateName("foo", chrome);
        assertEquals("foo_Safari_5", name);
        verify(chrome);
    }
}
