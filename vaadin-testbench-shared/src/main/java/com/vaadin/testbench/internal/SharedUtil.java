/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.internal;

import java.io.Serializable;
import java.util.Locale;

/**
 * Copied from Flow to avoid a dependency.
 */
public class SharedUtil implements Serializable {

    /**
     * Trims trailing slashes (if any) from a string.
     *
     * @param value
     *            The string value to be trimmed. Cannot be null.
     * @return String value without trailing slashes.
     */
    public static String trimTrailingSlashes(String value) {
        return value.replaceAll("/*$", "");
    }

    /**
     * Splits a camelCaseString into an array of words with the casing
     * preserved.
     *
     * @since 7.4
     * @param camelCaseString
     *            The input string in camelCase format
     * @return An array with one entry per word in the input string
     */
    public static String[] splitCamelCase(String camelCaseString) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelCaseString.length(); i++) {
            char c = camelCaseString.charAt(i);
            if (Character.isUpperCase(c)
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
        } else if (!Character.isUpperCase(camelCaseString.charAt(i - 1))) {
            // Word ends if previous char wasn't upper case
            return true;
        } else if (i + 1 < camelCaseString.length()
                && !Character.isUpperCase(camelCaseString.charAt(i + 1))) {
            // Word ends if next char isn't upper case
            return true;
        } else {
            return false;
        }
    }

    /**
     * Converts a camelCaseString to a human friendly format (Camel case
     * string).
     * <p>
     * In general splits words when the casing changes but also handles special
     * cases such as consecutive upper case characters. Examples:
     * <p>
     * {@literal MyBeanContainer} becomes {@literal My Bean Container}
     * {@literal AwesomeURLFactory} becomes {@literal Awesome URL Factory}
     * {@literal SomeUriAction} becomes {@literal Some Uri Action}
     *
     * @since 7.4
     * @param camelCaseString
     *            The input string in camelCase format
     * @return A human friendly version of the input
     */
    public static String camelCaseToHumanFriendly(String camelCaseString) {
        String[] parts = splitCamelCase(camelCaseString);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = capitalize(parts[i]);
        }
        return join(parts, " ");
    }

    /**
     * Joins the words in the input array together into a single string by
     * inserting the separator string between each word.
     *
     * @since 7.4
     * @param parts
     *            The array of words
     * @param separator
     *            The separator string to use between words
     * @return The constructed string of words and separators
     */
    public static String join(String[] parts, String separator) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part);
            sb.append(separator);
        }
        return sb.substring(0, sb.length() - separator.length());
    }

    /**
     * Capitalizes the first character in the given string in a way suitable for
     * use in code (methods, properties etc)
     *
     * @since 7.4
     * @param string
     *            The string to capitalize
     * @return The capitalized string
     */
    public static String capitalize(String string) {
        if (string == null) {
            return null;
        }

        if (string.length() <= 1) {
            return string.toUpperCase(Locale.ENGLISH);
        }

        return string.substring(0, 1).toUpperCase(Locale.ENGLISH)
                + string.substring(1);
    }

    /**
     * Changes the first character in the given string to lower case in a way
     * suitable for use in code (methods, properties etc).
     *
     * @param string
     *            The string to change
     * @return The string with initial character turned into lower case
     */
    public static String firstToLower(String string) {
        if (string == null) {
            return null;
        }

        if (string.length() <= 1) {
            return string.toLowerCase(Locale.ENGLISH);
        }

        return string.substring(0, 1).toLowerCase(Locale.ENGLISH)
                + string.substring(1);
    }

    /**
     * Converts a property id to a human friendly format. Handles nested
     * properties by only considering the last part, e.g. "address.streetName"
     * is equal to "streetName" for this method.
     *
     * @since 7.4
     * @param propertyId
     *            The propertyId to format
     * @return A human friendly version of the property id
     */
    public static String propertyIdToHumanFriendly(Object propertyId) {
        String string = propertyId.toString();
        if (string.isEmpty()) {
            return "";
        }

        // For nested properties, only use the last part
        int dotLocation = string.lastIndexOf('.');
        if (dotLocation > 0 && dotLocation < string.length() - 1) {
            string = string.substring(dotLocation + 1);
        }

        return camelCaseToHumanFriendly(string);
    }

    /**
     * Adds the given get parameter to the URI and returns the new URI.
     *
     * @param uri
     *            the URI to which the parameter should be added.
     * @param parameter
     *            the name of the parameter
     * @param value
     *            the value of the parameter
     * @return The modified URI with the parameter added
     */
    public static String addGetParameter(String uri, String parameter,
            String value) {
        return addGetParameters(uri, parameter + "=" + value);
    }

    /**
     * Adds the given get parameter to the URI and returns the new URI.
     *
     * @param uri
     *            the URI to which the parameter should be added.
     * @param parameter
     *            the name of the parameter
     * @param value
     *            the value of the parameter
     * @return The modified URI with the parameter added
     */
    public static String addGetParameter(String uri, String parameter,
            int value) {
        return addGetParameter(uri, parameter, Integer.toString(value));
    }

    /**
     * Adds the get parameters to the uri and returns the new uri that contains
     * the parameters.
     *
     * @param uri
     *            The uri to which the parameters should be added.
     * @param extraParams
     *            One or more parameters in the format "a=b" or "c=d&amp;e=f".
     *            An empty string is allowed but will not modify the url.
     * @return The modified URI with the get parameters in extraParams added.
     */
    public static String addGetParameters(String uri, String extraParams) {
        if (extraParams == null || extraParams.length() == 0) {
            return uri;
        }
        // RFC 3986: The query component is indicated by the first question
        // mark ("?") character and terminated by a number sign ("#") character
        // or by the end of the URI.
        String fragment = null;
        int hashPosition = uri.indexOf('#');
        if (hashPosition != -1) {
            // Fragment including "#"
            fragment = uri.substring(hashPosition);
            // The full uri before the fragment
            uri = uri.substring(0, hashPosition);
        }

        if (uri.contains("?")) {
            uri += "&";
        } else {
            uri += "?";
        }
        uri += extraParams;

        if (fragment != null) {
            uri += fragment;
        }

        return uri;
    }

    /**
     * Converts a dash ("-") separated string into camelCase.
     * <p>
     * Examples:
     * <p>
     * {@literal foo} becomes {@literal foo} {@literal foo-bar} becomes
     * {@literal fooBar} {@literal foo--bar} becomes {@literal fooBar}
     *
     * @since 7.5
     * @param dashSeparated
     *            The dash separated string to convert
     * @return a camelCase version of the input string
     */
    public static String dashSeparatedToCamelCase(String dashSeparated) {
        if (dashSeparated == null) {
            return null;
        }
        String[] parts = dashSeparated.split("-");
        for (int i = 1; i < parts.length; i++) {
            parts[i] = capitalize(parts[i]);
        }

        return join(parts, "");
    }

    /**
     * Converts a camelCase string into dash ("-") separated.
     * <p>
     * Examples:
     * <p>
     * {@literal foo} becomes {@literal foo} {@literal fooBar} becomes
     * {@literal foo-bar} {@literal MyBeanContainer} becomes
     * {@literal -my-bean-container} {@literal AwesomeURLFactory} becomes
     * {@literal -awesome-uRL-factory} {@literal someUriAction} becomes
     * {@literal some-uri-action}
     *
     * @param camelCaseString
     *            The input string in camelCase format
     * @return A human friendly version of the input
     */
    public static String camelCaseToDashSeparated(String camelCaseString) {
        if (camelCaseString == null) {
            return null;
        }
        String[] parts = splitCamelCase(camelCaseString);
        if (parts[0].length() >= 1
                && Character.isUpperCase(parts[0].charAt(0))) {
            // starts with upper case
            parts[0] = "-" + firstToLower(parts[0]);
        }
        for (int i = 1; i < parts.length; i++) {
            parts[i] = firstToLower(parts[i]);
        }
        return join(parts, "-");
    }

}
