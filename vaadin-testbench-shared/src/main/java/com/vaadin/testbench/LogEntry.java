/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

/**
 * One line of the browser's console log. Its getters and {@link #toString()}
 * are Selenium's {@code LogEntry}'s, so a test reading it ports by changing its
 * import.
 *
 * @param timestamp
 *            milliseconds since the epoch
 */
public record LogEntry(Level level, long timestamp, String message) {

    public Level getLevel() {
        return level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    /** {@code [2026-11-12T10:15:30Z] [SEVERE] message}, as Selenium's. */
    @Override
    public String toString() {
        return String.format("[%s] [%s] %s", DateTimeFormatter.ISO_INSTANT
                .format(Instant.ofEpochMilli(timestamp)), level, message);
    }
}
