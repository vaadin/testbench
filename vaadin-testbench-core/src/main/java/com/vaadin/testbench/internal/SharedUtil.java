package com.vaadin.testbench.internal;

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

import java.io.Serializable;
import java.util.stream.IntStream;

import static java.lang.Character.isUpperCase;
import static java.util.Locale.ENGLISH;

/**
 * Copied from Flow to avoid a dependency.
 */
public class SharedUtil implements Serializable {

    /**
     * Converts a camelCase string into dash ("-") separated.
     * <p>
     * Examples:
     * <p>
     * <ul>
     *      <li>{@literal foo} becomes {@literal foo}
     *      <li>{@literal fooBar} becomes {@literal foo-bar}
     *      <li>{@literal MyBeanContainer} becomes {@literal -my-bean-container}
     *      <li>{@literal AwesomeURLFactory} becomes {@literal -awesome-uRL-factory}
     *      <li>{@literal someUriAction} becomes {@literal some-uri-action}
     * </ul>
     *
     * @param camelCaseString The input string in camelCase format
     * @return A human friendly version of the input
     */
    public static String camelCaseToDashSeparated(String camelCaseString) {
        if (camelCaseString == null) {
            return null;
        }
        String[] parts = splitCamelCase(camelCaseString);
        if (parts[0].length() >= 1
                && isUpperCase(parts[0].charAt(0))) {
            // starts with upper case
            parts[0] = "-" + firstToLower(parts[0]);
        }
        IntStream
                .range(1, parts.length)
                .forEach(i -> parts[i] = firstToLower(parts[i]));
        return joinWithHyphen(parts);
    }

    /**
     * Splits a camelCaseString into an array of words with the casing
     * preserved.
     *
     * @param camelCaseString The input string in camelCase format
     * @return An array with one entry per word in the input string
     * @since 7.4
     */
    private static String[] splitCamelCase(String camelCaseString) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelCaseString.length(); i++) {
            char c = camelCaseString.charAt(i);
            if (isUpperCase(c)
                    && isWordComplete(camelCaseString, i)) {
                sb.append(' ');
            }
            sb.append(c);
        }
        return sb.toString().split(" ");
    }

    private static boolean isWordComplete(String camelCaseString, int i) {
        if (i == 0) {
            // Word can't end at the beginning
            return false;
        } else // Word ends if next char isn't upper case
            if (!isUpperCase(camelCaseString.charAt(i - 1))) {
                // Word ends if previous char wasn't upper case
                return true;
            } else return i + 1 < camelCaseString.length()
                    && !isUpperCase(camelCaseString.charAt(i + 1));
    }

    /**
     * Joins the words in the input array together into a single string by
     * inserting the separator string between each word.
     *
     * @param parts     The array of words
     * @return The constructed string of words and separators
     * @since 7.4
     */
    private static String joinWithHyphen(String[] parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part);
            sb.append("-");
        }
        return sb.substring(0, sb.length() - "-".length());
    }

    /**
     * Changes the first character in the given string to lower case in a way
     * suitable for use in code (methods, properties etc).
     *
     * @param string The string to change
     * @return The string with initial character turned into lower case
     */
    private static String firstToLower(String string) {
        if (string == null) {
            return null;
        }

        if (string.length() <= 1) {
            return string.toLowerCase(ENGLISH);
        }

        return string.substring(0, 1).toLowerCase(ENGLISH)
                + string.substring(1);
    }
}
