package com.vaadin.dependencies.core.fs;

import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>PathUtils class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class PathUtils {

  private PathUtils() {
  }

  /**
   * <p>createUrwxGrwxArwx.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrwxGrwxArwx() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    perms.add(PosixFilePermission.OWNER_WRITE);
    perms.add(PosixFilePermission.OWNER_EXECUTE);
    //add group permissions
    perms.add(PosixFilePermission.GROUP_READ);
    perms.add(PosixFilePermission.GROUP_WRITE);
    perms.add(PosixFilePermission.GROUP_EXECUTE);
    //add others permissions
    perms.add(PosixFilePermission.OTHERS_READ);
    perms.add(PosixFilePermission.OTHERS_WRITE);
    perms.add(PosixFilePermission.OTHERS_EXECUTE);
    return perms;
  }

  /**
   * <p>createUrwxGoooAooo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrwxGoooAooo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    perms.add(PosixFilePermission.OWNER_WRITE);
    perms.add(PosixFilePermission.OWNER_EXECUTE);
    //add group permissions
    //add others permissions
    return perms;
  }

  /**
   * <p>createUoooGrwxAooo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUoooGrwxAooo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    //add group permissions
    perms.add(PosixFilePermission.GROUP_READ);
    perms.add(PosixFilePermission.GROUP_WRITE);
    perms.add(PosixFilePermission.GROUP_EXECUTE);
    //add others permissions
    return perms;
  }

  /**
   * <p>createUoooGoooArwx.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUoooGoooArwx() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    //add group permissions
    //add others permissions
    perms.add(PosixFilePermission.OTHERS_READ);
    perms.add(PosixFilePermission.OTHERS_WRITE);
    perms.add(PosixFilePermission.OTHERS_EXECUTE);
    return perms;
  }

  /**
   * <p>createUrwoGoooAooo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrwoGoooAooo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    perms.add(PosixFilePermission.OWNER_WRITE);
    //add group permissions
    //add others permissions
    return perms;
  }


  /**
   * <p>createUrwoGrwoAooo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrwoGrwoAooo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    perms.add(PosixFilePermission.OWNER_WRITE);
    //add group permissions
    perms.add(PosixFilePermission.GROUP_READ);
    perms.add(PosixFilePermission.GROUP_WRITE);
    //add others permissions
    return perms;
  }

  /**
   * <p>createUrwoGrwoArwo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrwoGrwoArwo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    perms.add(PosixFilePermission.OWNER_WRITE);
    //add group permissions
    perms.add(PosixFilePermission.GROUP_READ);
    perms.add(PosixFilePermission.GROUP_WRITE);
    //add others permissions
    perms.add(PosixFilePermission.OTHERS_READ);
    perms.add(PosixFilePermission.OTHERS_WRITE);
    return perms;
  }

  /**
   * <p>createUrooGoooAooo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrooGoooAooo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    //add group permissions
    //add others permissions
    return perms;
  }


  /**
   * <p>createUrooGrooAooo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrooGrooAooo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    //add group permissions
    perms.add(PosixFilePermission.GROUP_READ);
    //add others permissions
    return perms;
  }

  /**
   * <p>createUrooGrooAroo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrooGrooAroo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    //add group permissions
    perms.add(PosixFilePermission.GROUP_READ);
    //add others permissions
    perms.add(PosixFilePermission.OTHERS_READ);
    return perms;
  }


}
