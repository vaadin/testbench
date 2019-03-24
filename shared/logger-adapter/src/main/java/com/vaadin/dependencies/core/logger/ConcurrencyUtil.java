package com.vaadin.dependencies.core.logger;

import java.util.concurrent.ConcurrentMap;

/**
 * <p>ConcurrencyUtil class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class ConcurrencyUtil {
  private ConcurrencyUtil() {
  }

  /**
   * <p>getOrPutIfAbsent.</p>
   *
   * @param map a {@link ConcurrentMap} object.
   * @param key a K object.
   * @param func a {@link ConstructorFunction} object.
   * @param <K> a K object.
   * @param <V> a V object.
   * @return a V object.
   */
  public static <K, V> V getOrPutIfAbsent(ConcurrentMap<K, V> map , K key , ConstructorFunction<K, V> func) {
    V value = map.get(key);
    if (value == null) {
      value = func.createNew(key);
      V current = map.putIfAbsent(key , value);
      value = current == null ? value : current;
    }
    return value;
  }
}
