/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.karibu;

import org.junit.After;
import org.junit.Before;

/**
 * Base JUnit 4 class for UI unit tests.
 *
 * Subclasses should typically restrict classpath scanning to a specific package
 * for faster bootstrap by overriding {@link #scanPackage()} method.
 */
public abstract class KaribuTest extends BaseKaribuTest {

    @Before
    public void initVaadinEnvironment() {
        super.initVaadinEnvironment();
    }

    @After
    public void cleanVaadinEnvironment() {
        super.cleanVaadinEnvironment();
    }
}
