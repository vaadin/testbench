package com.vaadin.dependencies.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ResourceList {

  /**
   * for all elements of java.class.path get a Collection of resources Pattern
   * pattern = Pattern.compile(".*"); gets all resources
   *
   * @param pattern the pattern to match
   * @return the resources in the order they are found
   */
  public static Collection<String> getResources(final Pattern pattern) {
    final List<String> retval = new ArrayList<>();
    final String classPath = System.getProperty("java.class.path" , ".");
    final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
    for (final String element : classPathElements) {
      retval.addAll(getResources(element , pattern));
    }
    return retval;
  }

  private static Collection<String> getResources(final String element , final Pattern pattern) {
    final List<String> retval = new ArrayList<>();
    final File file = new File(element);
    if (file.isDirectory()) {
      retval.addAll(getResourcesFromDirectory(file , pattern));
    } else {
      retval.addAll(getResourcesFromJarFile(file , pattern));
    }
    return retval;
  }

  private static Collection<String> getResourcesFromJarFile(
      final File file ,
      final Pattern pattern) {
    final List<String> retval = new ArrayList<>();
    ZipFile zf;
    try {
      zf = new ZipFile(file);
    } catch (final ZipException e) {
      throw new Error(e);
    } catch (final IOException e) {
      throw new Error(e);
    }
    final Enumeration e = zf.entries();
    while (e.hasMoreElements()) {
      final ZipEntry ze = (ZipEntry) e.nextElement();
      final String fileName = ze.getName();
      final boolean accept = pattern.matcher(fileName).matches();
      if (accept) {
        retval.add(fileName);
      }
    }
    try {
      zf.close();
    } catch (final IOException e1) {
      throw new Error(e1);
    }
    return retval;
  }

  private static Collection<String> getResourcesFromDirectory(
      final File directory ,
      final Pattern pattern) {
    final List<String> retval = new ArrayList<>();
    final File[] fileList = directory.listFiles();
    for (final File file : fileList) {
      if (file.isDirectory()) {
        retval.addAll(getResourcesFromDirectory(file , pattern));
      } else {
        try {
          final String fileName = file.getCanonicalPath();
          final boolean accept = pattern.matcher(fileName).matches();
          if (accept) {
            retval.add(fileName);
          }
        } catch (final IOException e) {
          throw new Error(e);
        }
      }
    }
    return retval;
  }

  /**
   * Creates a {@code URLClassLoader} from JAR files found in the
   * globalclasspath directory, assuming that globalclasspath is in
   * {@code System.getProperty("java.class.path")}.
   */
  public static URLClassLoader createURLClassLoader(String pattern) {
    Collection<String> resources = ResourceList.getResources(Pattern.compile(pattern));
    Collection<URL> urls = new ArrayList<>();
    for (String resource : resources) {
      File file = new File(resource);
      // Ensure that the JAR exists
      // and is in the globalclasspath directory.
      if (file.isFile() && "globalclasspath".equals(file.getParentFile().getName())) {
        try {
          urls.add(file.toURI().toURL());
        } catch (MalformedURLException e) {
          // This should never happen.
          e.printStackTrace();
        }
      }
    }
    return new URLClassLoader(urls.toArray(new URL[0]));
  }

  /**
   * For Pattern *.jar
   * @return
   */
  public static URLClassLoader createURLClassLoader(){
    return createURLClassLoader(".*\\.jar");
  }

//  public static void main(String[] args) {
//    URLClassLoader classLoader = createURLClassLoader();
//    System.out.println(classLoader.getResource("mine.properties"));
//  }

//  /**
//   * list the resources that match args[0]
//   *
//   * @param args
//   *            args[0] is the pattern to match, or list all resources if
//   *            there are no args
//   */
//  public static void main(final String[] args){
//    Pattern pattern;
//    if(args.length < 1){
//      pattern = Pattern.compile(".*");
//    } else{
//      pattern = Pattern.compile(args[0]);
//    }
//    final Collection<String> list = ResourceList.getResources(pattern);
//    for(final String name : list){
//      System.out.println(name);
//    }
//  }
}
