package com.vaadin.tests.elements.ng.tooling;

/**
 *
 */
public interface AssertsTrait {

    /**
     * Asserts that {@literal a} is &gt;= {@literal b}
     *
     * @param message
     *     The message to include in the {@link AssertionError}
     * @param a
     * @param b
     * @throws AssertionError
     *     If comparison fails
     */
    default <T> void assertGreaterOrEqual(String message,
                                          Comparable<T> a, T b)
        throws AssertionError {
        if (a.compareTo(b) < 0)
            throw new AssertionError(decorate(message, a, b));
    }

    /**
     * Asserts that {@literal a} is &gt; {@literal b}
     *
     * @param message
     *     The message to include in the {@link AssertionError}
     * @param a
     * @param b
     * @throws AssertionError
     *     If comparison fails
     */
    default <T> void assertGreater(String message, Comparable<T> a,
                                   T b)
        throws AssertionError {
        if (a.compareTo(b) <= 0)
            throw new AssertionError(decorate(message, a, b));
    }

    /**
     * Asserts that {@literal a} is &lt;= {@literal b}
     *
     * @param message
     *     The message to include in the {@link AssertionError}
     * @param a
     * @param b
     * @throws AssertionError
     *     If comparison fails
     */
    default <T> void assertLessThanOrEqual(String message,
                                           Comparable<T> a, T b)
        throws AssertionError {
        if (a.compareTo(b) > 0)
            throw new AssertionError(decorate(message, a, b));
    }

    /**
     * Asserts that {@literal a} is &lt; {@literal b}
     *
     * @param message
     *     The message to include in the {@link AssertionError}
     * @param a
     * @param b
     * @throws AssertionError
     *     If comparison fails
     */
    default <T> void assertLessThan(String message,
                                    Comparable<T> a, T b)
        throws AssertionError {
        if (a.compareTo(b) >= 0)
            throw new AssertionError(decorate(message, a, b));
    }

    default <T> String decorate(String message, Comparable<T> a, T b) {
        message = message.replace("{0}", a.toString());
        message = message.replace("{1}", b.toString());
        return message;
    }

}
