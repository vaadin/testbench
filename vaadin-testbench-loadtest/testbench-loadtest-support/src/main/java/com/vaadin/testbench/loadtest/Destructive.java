/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test method as destructive (e.g., deletes data) so it is skipped
 * during k6 load test recording.
 * <p>
 * Destructive tests modify shared state in ways that cannot be safely repeated
 * by multiple virtual users (e.g., deleting a specific entity). When the k6
 * recording proxy is active, methods annotated with {@code @Destructive} are
 * automatically skipped.
 * <p>
 * Example usage:
 * 
 * <pre>
 * {@literal @}Test
 * {@literal @}Destructive
 * public void deletePatient() {
 *     // This test will be skipped during k6 recording
 * }
 * </pre>
 *
 * @see K6RecordingExtension
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Destructive {
}
