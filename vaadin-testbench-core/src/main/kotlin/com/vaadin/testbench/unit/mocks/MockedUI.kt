/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit.mocks

import com.vaadin.flow.component.UI

/**
 * A simple no-op UI used by default by [com.vaadin.testbench.unit.MockVaadin.setup].
 * The class is open, in order to be extensible in user's library
 */
open class MockedUI : UI()