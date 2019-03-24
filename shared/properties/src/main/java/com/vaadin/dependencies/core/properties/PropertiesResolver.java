package com.vaadin.dependencies.core.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.frp.functions.CheckedFunction;

/**
 * Creates a {@link java.util.Properties} object merged from different sources.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class PropertiesResolver implements HasLogger {
  /** Constant <code>CONFIG_LOCATION_PROPERTY="rapidpm.configlocation"</code> */
  public static final String CONFIG_LOCATION_PROPERTY = "rapidpm.configlocation";
  public static final String PROPERIES_EXTENSION = ".properties";


  public static CheckedFunction<String, Properties> propertyReader() {
    return (filename) -> {
      final PropertiesResolver resolver = new PropertiesResolver();
      return resolver.get(filename);
    };
  }

  private String createFileName(String name) {
    return name + PROPERIES_EXTENSION;
  }

  /**
   * Creates a {@link java.util.Properties} object from different sources. The sources are:
   *
   * <ol>
   * <li>the root of the classpath</li>
   * <li>the current working directory</li>
   * <li>the home directory of the current user</li>
   * <li>a directory specified by the system property <code>rapidm.configlocation</code></li>
   * </ol>
   * <br>
   * Properties defined in the higher sources override the ones defined in lower sources.
   *
   * @param name the name of the properties file - without the <code>.properties</code> file
   *        extensions
   * @return the merged {@link java.util.Properties} object
   */
  public Properties get(String name) {
    Properties propertiesFromResource = loadFromResource(name);
    Properties propertiesFromWorkingDir = loadFromWorkingDir(name);
    Properties propertiesFromHomeDir = loadFromHomeDir(name);
    Properties propertiesFromEnvironmentSource = loadFromEnviromentSource(name);
    return merge(propertiesFromResource ,
                 propertiesFromWorkingDir ,
                 propertiesFromHomeDir ,
                 propertiesFromEnvironmentSource);
  }

  private Properties loadFromEnviromentSource(String name) {
    String configSource = System.getProperty(CONFIG_LOCATION_PROPERTY);
    logger().fine("loadFromEnviromentSource - " + configSource);
    Properties properties;
    if (configSource != null) {
      String fileName = createFileName(name);
      File file = new File(configSource , fileName);
      properties = loadFromFile(file);
    } else {
      properties = new Properties();
    }
    return properties;
  }

  private Properties loadFromFile(File file) {
    Properties properties = new Properties();
    try (InputStream in = new FileInputStream(file)) {
      logger().info("Load properties from file: " + file);
      properties.load(in);
    } catch (FileNotFoundException e) {
      logger().fine("No properties file " + file.getAbsolutePath() + " found.");
    } catch (IOException e) {
      logger().severe("Failure loading properties from file: " + file.getAbsolutePath() , e);
    }

    return properties;
  }

  private Properties loadFromHomeDir(String name) {
    String fileName = createFileName(name);
    String homeDir = System.getProperty("user.home");
    File file = new File(homeDir , fileName);
    logger().fine("loadFromHomeDir: the file " + file.getAbsolutePath() + " is existing -> " +file.exists());
    return loadFromFile(file);
  }

  private Properties loadFromResource(String name) {
    Properties properties = new Properties();

    String resourceName = "/" + createFileName(name);
    logger().fine("Resource name: " + resourceName);
    try (InputStream in = getClass().getResourceAsStream(resourceName)) {
      if (in != null) {
        logger().info("Load properties from resource: " + resourceName);
        properties.load(in);
      }
    } catch (IOException e) {
      logger().severe("Failure loading properties from resource: " + resourceName , e);
    }
    return properties;
  }

  private Properties loadFromWorkingDir(String name) {
    String fileName = createFileName(name);
    File file = new File(fileName);
    logger().fine("loadFromWorkingDir: the file " + file.getAbsolutePath() + " is existing -> " +file.exists());
    return loadFromFile(file);
  }

  private Properties merge(Properties... properties) {
    Properties result = new Properties();
    for (Properties toAdd : properties) {
      for (Object key : toAdd.keySet()) {
        result.setProperty(key.toString() , toAdd.getProperty(key.toString()));
      }
    }
    return result;
  }
}
