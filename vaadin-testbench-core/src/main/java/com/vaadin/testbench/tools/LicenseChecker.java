/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.tools;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static String CVALInfo = "";
    private static String NEW_LINE = System.getProperty("line.separator");

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
            printValidationInfo(validationInfo);
        } catch (InvalidCvalException e) {
            if (e.info != null && e.info.getMessage() != null) {
                getLogger().log(Level.FINE, e.info.getMessage());
            }
            printAndRethrowException(e.getMessage(), e);
        } catch (UnreachableCvalServerException e) {
            // No connection and no cached lisence
            String message = "Your license for TestBench 4 has not been validated. Check your network connection.";
            printMessage(message);
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(LicenseChecker.class.getName());
    }

    private static void printAndRethrowException(String eMessage, Throwable t) {
        printMessage(eMessage);
        throw new Error(CVALInfo, t);
    }

    private static void printMessage(String eMessage) {
        CVALInfo = getValidationInfo(eMessage);
        System.out.println(CVALInfo);
    }

    private static String getValidationInfo(String validationInfo) {
        String message = "";
        message += getLine(validationInfo.length()) + NEW_LINE;
        message += validationInfo + NEW_LINE;
        message += getLine(validationInfo.length()) + NEW_LINE;
        return message;
    }

    private static void printValidationInfo(CvalInfo validationInfo) {
        printLine(validationInfo.getMessage().length());
        getLogger().log(Level.FINE, validationInfo.getMessage());
        System.out.println(validationInfo.getMessage());
        printLine(validationInfo.getMessage().length());
    }

    private static String getLine(int n) {
        String line = "";
        for (int i = 0; i <= n; i++) {
            line += "-";
        }
        return line;
    }

    // print formating line of "-" symbols
    private static void printLine(int n) {
        String line = getLine(n);
        getLogger().log(Level.FINE, line);
        System.out.print(line);
        System.out.println();
    }
}
