package com.vaadin.testbench.configuration;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SaucelabsTarget extends RemoteTarget {

    private static final String TAGS = "tags";

    private DesiredCapabilities desiredCapabilities;

    public SaucelabsTarget(DesiredCapabilities desiredCapabilities) {
        this.desiredCapabilities = desiredCapabilities;
        configure();
    }

    @Override
    public String getHubUrl() {
        return "https://" + getUsername() + ":" + getAccessKey() + "@ondemand.saucelabs.com:443/wd/hub";
    }

    public String getUsername() {
        return System.getProperty("sauce.user");
    }

    public String getAccessKey() {
        return System.getProperty("sauce.sauceAccessKey");
    }

    public String getSauceOptions() {
        return System.getProperty("sauce.options");
    }

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        return desiredCapabilities;
    }

    public String getProject() {
        return null;
    }

    private void configure() {
        if (StringUtils.isNotBlank(getProject())) {
            getDesiredCapabilities().setCapability(TAGS, getProject());
        }

        final String build = System.getenv("SAUCELABS_BUILD");
        if (StringUtils.isNotBlank(build)) {
            getDesiredCapabilities().setCapability("build", build);
        }

        if (StringUtils.isNotBlank(getSauceOptions())) {
            String tunnelId = getTunnelIdentifier(getSauceOptions());
            if (tunnelId != null) {
                getDesiredCapabilities().setCapability("tunnelIdentifier", tunnelId);
            }
        }
    }

    /**
     * @param options
     *            the command line options used to launch Sauce Connect
     * @return String representing the tunnel identifier
     */
    private static String getTunnelIdentifier(String options) {
        if (StringUtils.isNotBlank(options)) {
            final String[] tokens = options.split(" ");
            for (int a = 0; a < tokens.length; a++) {
                if (a < tokens.length - 1 && (tokens[a].equals("-i")
                        || tokens[a].equals("--tunnel-identifier"))) {
                    return tokens[a + 1]; // The next token is the identifier.
                }
            }
        }

        return null;
    }
}
