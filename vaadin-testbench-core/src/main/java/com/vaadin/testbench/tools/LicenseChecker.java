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
package com.vaadin.testbench.tools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.vaadin.testbench.tools.CvalChecker.CvalInfo;
import com.vaadin.testbench.tools.CvalChecker.InvalidCvalException;
import com.vaadin.testbench.tools.CvalChecker.UnreachableCvalServerException;

/*
 * This class nags about license information whenever TestBench is started
 * unless the vaadin.testbench.developer.license system property is set or the
 * ~/.vaadin.testbench.developer.license file exists.
 */
public class LicenseChecker {
    private static final String PRODUCT_NAME = "vaadin-testbench";
    private static final String PRODUCT_TITLE = "Vaadin TestBench";
    private static String productVersion = "unversioned";
    private static final CvalChecker licenseChecker = new CvalChecker();

    public static void nag() {
        try {
            // get project version from properties file
            // version in properties file is set by maven
            Properties props = System.getProperties();
            try {
                props.load(LicenseChecker.class.getClassLoader()
                        .getResourceAsStream("project.properties"));
            } catch (IOException e) {
                System.err.println("Coulnd load properties file");
                e.printStackTrace();
            }
            productVersion = props.getProperty("version");
            // Validate license
            CvalInfo validationInfo = licenseChecker.validateProduct(
                    PRODUCT_NAME, productVersion, PRODUCT_TITLE);
            // If license is valid print the message about it
            System.out.println(validationInfo.getMessage());
        } catch (InvalidCvalException e) {
            System.err.println(e.getMessage());
            printCVALInformationAndHowToGetRidOfThisInformation();
            System.exit(0);
        }

        catch (UnreachableCvalServerException e) {
            System.err.println(e.getMessage());
            printCVALInformationAndHowToGetRidOfThisInformation();
            System.exit(0);
        }
    }

    private static void printCVALInformationAndHowToGetRidOfThisInformation() {
        try {
            System.err.println(CharStreams.toString(new InputStreamReader(
                    LicenseChecker.class.getResourceAsStream("licensenag.txt"),
                    Charsets.UTF_8)));
        } catch (IOException e) {
            System.err
                    .println("VAADIN TESTBENCH IS COMMERCIAL SOFTWARE, SEE https://vaadin.com/license/cval-3.0");
        }
    }
}
