/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.testbench.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.exceptions.CouldNotParseVaadinVersionException;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class VersionUtil {
    private static int VAADIN_MAJOR_VERSION = 0;
    private static int VAADIN_MINOR_VERSION = 0;
    private static int VAADIN_REVISION = 0;
    private static String VAADIN_BUILD = "";

    private static boolean isVaadinVersionInitializated() {
        return VAADIN_MAJOR_VERSION != 0;
    }

    private static void initializeVaadinVersion(WebDriver webDriver) {
        Pattern pattern = Pattern
                .compile("\"vaadinVersion\": ?\"([0-9]+)\\.([0-9]+)\\.([0-9]+)(\\..*)?\"");
        Matcher matcher = pattern.matcher(webDriver.getPageSource());

        if (!matcher.find()) {
            throw new CouldNotParseVaadinVersionException();
        }

        VAADIN_MAJOR_VERSION = Integer.parseInt(matcher.group(1));
        VAADIN_MINOR_VERSION = Integer.parseInt(matcher.group(2));
        VAADIN_REVISION = Integer.parseInt(matcher.group(3));
        if (matcher.groupCount() == 4) {
            VAADIN_BUILD = matcher.group(4);
        }
    }

    /**
     * Gets current Vaadin major version.
     * 
     * @param webDriver
     *            {@link WebDriver} used to run the current test, from which
     *            current Vaadin version will be read.
     * @return Current Vaadin major version.
     */
    public static int getVaadinMajorVersion(WebDriver webDriver) {
        if (!isVaadinVersionInitializated()) {
            initializeVaadinVersion(webDriver);
        }
        return VAADIN_MAJOR_VERSION;
    }

    /**
     * Gets current Vaadin minor version.
     * 
     * @param webDriver
     *            {@link WebDriver} used to run the current test, from which
     *            current Vaadin version will be read.
     * @return Current Vaadin minor version.
     */
    public static int getVaadinMinorVersion(WebDriver webDriver) {
        if (!isVaadinVersionInitializated()) {
            initializeVaadinVersion(webDriver);
        }
        return VAADIN_MINOR_VERSION;
    }

    /**
     * Gets current Vaadin revision.
     * 
     * @param webDriver
     *            {@link WebDriver} used to run the current test, from which
     *            current Vaadin version will be read.
     * @return Current Vaadin revision.
     */
    public static int getVaadinRevision(WebDriver webDriver) {
        if (!isVaadinVersionInitializated()) {
            initializeVaadinVersion(webDriver);
        }
        return VAADIN_REVISION;
    }

    /**
     * Gets current Vaadin build.
     * 
     * @param webDriver
     *            {@link WebDriver} used to run the current test, from which
     *            current Vaadin version will be read.
     * @return Current Vaadin build.
     */
    public static String getVaadinBuild(WebDriver webDriver) {
        if (!isVaadinVersionInitializated()) {
            initializeVaadinVersion(webDriver);
        }
        return VAADIN_BUILD;
    }

    /**
     * Verifies if current Vaadin version is at least the version given by the
     * parameters major, minor, revision.
     * 
     * @param major
     *            minimum major version required.
     * @param minor
     *            minimum minor version required.
     * @param revision
     *            minimum revision required.
     * @param webDriver
     *            {@link WebDriver} used to run the current test, from which
     *            current Vaadin version will be read.
     * @return true if current Vaadin version is at least the version given by
     *         the parameters major, minor, revision; or if version is
     *         0.0.0.unversioned-development-build.<br>
     *         False otherwise.
     */
    public static boolean isAtLeast(int major, int minor, int revision,
            WebDriver webDriver) {
        if (!isVaadinVersionInitializated()) {
            initializeVaadinVersion(webDriver);
        }

        if (VAADIN_MAJOR_VERSION == 0 && VAADIN_MINOR_VERSION == 0
                && VAADIN_REVISION == 0
                && "unversioned-development-build".equals(VAADIN_BUILD)) {
            return true;
        }

        if (VAADIN_MAJOR_VERSION < major) {
            return false;
        }

        if (major == VAADIN_MAJOR_VERSION && VAADIN_MINOR_VERSION < minor) {
            return false;
        }

        if (major == VAADIN_MAJOR_VERSION && minor == VAADIN_MINOR_VERSION
                && VAADIN_REVISION < revision) {
            return false;
        }

        return true;
    }
}
