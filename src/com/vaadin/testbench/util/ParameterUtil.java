package com.vaadin.testbench.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.vaadin.testbench.Parameters;

/**
 * ParameterUtil is a singleton class to handle Properties file read and
 * returning of changing parameters if available
 */
public class ParameterUtil {

    private static ParameterUtil _parameter = null;
    private Properties properties = null;

    private ParameterUtil() {
        // Exists so class can't be instantiated.
    }

    public static ParameterUtil getInstance() {
        return _parameter;
    }

    public static void init() throws FileNotFoundException {
        if (_parameter == null) {
            _parameter = new ParameterUtil();
            _parameter.initParameter();
        }

    }

    /**
     * Initialize properties and load properties file if available
     * 
     * @throws FileNotFoundException
     * 
     * @throws RuntimeException
     *             If the properties file was defined but not found
     */
    private void initParameter() throws FileNotFoundException {
        properties = new Properties();

        if (Parameters.hasParameterFile()) {
            try {
                FileInputStream in = new FileInputStream(
                        Parameters.getParameterFile());
                properties.load(in);
                in.close();
            } catch (IOException ioe) {
                throw new FileNotFoundException("Properties file "
                        + Parameters.getParameterFile() + " was not found");
            }
        }
    }

    /**
     * Return string for key. Else return key
     * 
     * @param key
     *            Key to search for
     * @return Property for key. If key not found return key.
     */
    private String get(String key) {
        return get(key, key);
    }

    /**
     * Return string for key. Else return default value
     * 
     * @param key
     *            Key to search for
     * @return Property for key. If key not found return default value.
     */
    private String get(String key, String defaultValue) {
        // Properties is based on a HashTable that can't handle null values
        if (key == null) {
            return defaultValue;
        }

        return properties.getProperty(key, defaultValue);
    }

    public static String translate(String value) {
        return getInstance().get(value);
    }

}
