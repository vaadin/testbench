/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.vaadin.pro.licensechecker.Capabilities;
import com.vaadin.pro.licensechecker.LicenseChecker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractK6MojoLicenseTest {

    /**
     * Concrete subclass for testing the abstract base class.
     */
    static class TestMojo extends AbstractK6Mojo {
        @Override
        public void execute() {
            // no-op
        }
    }

    @Test
    void checkLicense_callsLicenseChecker() throws MojoExecutionException {
        TestMojo mojo = new TestMojo();

        try (MockedStatic<LicenseChecker> licenseChecker = Mockito
                .mockStatic(LicenseChecker.class)) {
            mojo.checkLicense();

            licenseChecker.verify(() -> {
                LicenseChecker.checkLicenseFromStaticBlock(
                        Mockito.eq("vaadin-testbench"), Mockito.anyString(),
                        Mockito.isNull(), Mockito.any(Capabilities.class));
            });
        }
    }

    @Test
    void checkLicense_propagatesLicenseCheckerException() {
        TestMojo mojo = new TestMojo();

        try (MockedStatic<LicenseChecker> licenseChecker = Mockito
                .mockStatic(LicenseChecker.class)) {
            licenseChecker
                    .when(() -> LicenseChecker.checkLicenseFromStaticBlock(
                            Mockito.anyString(), Mockito.anyString(),
                            Mockito.isNull(), Mockito.any()))
                    .thenThrow(new RuntimeException("License check failed"));

            assertThrows(RuntimeException.class, mojo::checkLicense);
        }
    }

    @Test
    void checkLicense_succeedsWhenLicenseValid() {
        TestMojo mojo = new TestMojo();

        try (MockedStatic<LicenseChecker> licenseChecker = Mockito
                .mockStatic(LicenseChecker.class)) {
            // Static mock does nothing by default — simulates valid license
            assertDoesNotThrow(() -> mojo.checkLicense());
        }
    }
}
