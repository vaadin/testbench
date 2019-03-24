package com.vaadin.dependencies.core.basepattern.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>Abstract NestedBuilder class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public abstract class NestedBuilder<T, V> {
  protected T parent;

  /**
   * To get the parent builder
   *
   * @return T the instance of the parent builder
   */
  public T done() {
    Class<?> parentClass = parent.getClass();
    try {
      V build = this.build();
      String methodname = "with" + build.getClass().getSimpleName();
      Method method = parentClass.getDeclaredMethod(methodname, build.getClass());
      final boolean accessible = method.isAccessible();
      method.setAccessible(true);
      method.invoke(parent, build);
      method.setAccessible(accessible);
    } catch (NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException e) {
      e.printStackTrace();
    }
    return parent;
  }

  /**
   * <p>build.</p>
   *
   * @return a V object.
   */
  public abstract V build();

  /**
   * <p>withParentBuilder.</p>
   *
   * @param parent a T object.
   * @param <P> a P object.
   * @return a P object.
   */
  public <P extends NestedBuilder<T, V>> P withParentBuilder(T parent) {
    this.parent = parent;
    return (P) this;
  }
}
