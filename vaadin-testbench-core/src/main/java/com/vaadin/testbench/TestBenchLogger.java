package com.vaadin.testbench;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBenchLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestBenchLogger.class);

    public static Logger logger() {
        return LOGGER;
    }
}
