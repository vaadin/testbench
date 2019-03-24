package com.vaadin.dependencies.core.logger;

/**
 * See ConcurrencyUtil
 *
 * @param <K> key type
 * @param <V> value type
 * @author svenruppert
 * @version $Id: $Id
 */
public interface ConstructorFunction<K, V> {

    /**
     * <p>createNew.</p>
     *
     * @param arg a K object.
     * @return a V object.
     */
    V createNew(K arg);
}
