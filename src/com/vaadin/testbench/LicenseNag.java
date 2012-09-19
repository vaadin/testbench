package com.vaadin.testbench;

import java.io.File;

/**
 * This class nags about license information whenever TestBench is started
 * unless the com.vaadin.useraccount system property is set or the
 * ~/.vaadin.useraccount file exists.
 * 
 * @author Jonatan Kronqvist / Vaadin Ltd
 * 
 */
public class LicenseNag {
    public static void nag() {
        String userAccount = System.getProperty("com.vaadin.useraccount");
        if (userAccount == null) {
            String homedir = System.getProperty("user.home");
            File file = new File(homedir + "/.vaadin.useraccount");
            if (!file.exists()) {
                printCVALInformationAndHowToGetRidOfThisInformation();
            }
        }
    }

    private static void printCVALInformationAndHowToGetRidOfThisInformation() {
        System.err
                .println("VAADIN TESTBENCH IS COMMERCIAL SOFTWARE. I CRY EVRYTIM YOU USE WITHOUT PAY :(");
    }
}
