/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import org.junit.jupiter.api.Test;

import com.vaadin.testbench.loadtest.util.ResponseCheckConfig.Scope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseCheckConfigTest {

    @Test
    void emptyConfigProducesNoCheckLines() {
        assertEquals("", ResponseCheckConfig.EMPTY.toK6CheckLines(Scope.INIT));
        assertEquals("", ResponseCheckConfig.EMPTY.toK6CheckLines(Scope.UIDL));
    }

    @Test
    void initScopeCheckAppearsOnlyForInit() {
        ResponseCheckConfig config = ResponseCheckConfig.EMPTY.withCheck(
                Scope.INIT, "has title", "(r) => r.body.includes('<title>')");
        String lines = config.toK6CheckLines(Scope.INIT);
        assertTrue(lines
                .contains("'has title': (r) => r.body.includes('<title>'),"));
        assertTrue(config.toK6CheckLines(Scope.UIDL).isBlank(),
                "INIT check should not come into UIDL scope");
    }

    @Test
    void uidlScopeCheckAppearsForUidl() {
        ResponseCheckConfig config = ResponseCheckConfig.EMPTY.withCheck(
                Scope.UIDL, "no warning", "(r) => !r.body.includes('warning')");
        String lines = config.toK6CheckLines(Scope.UIDL);
        assertTrue(lines
                .contains("'no warning': (r) => !r.body.includes('warning'),"));
        assertTrue(config.toK6CheckLines(Scope.INIT).isBlank(),
                "UIDL check should not come into INIT scope");
    }

    @Test
    void allScopeCheckAppearsForBoth() {
        ResponseCheckConfig config = ResponseCheckConfig.EMPTY.withCheck(
                Scope.ALL, "fast response", "(r) => r.timings.duration < 3000");
        String initLines = config.toK6CheckLines(Scope.INIT);
        String uidlLines = config.toK6CheckLines(Scope.UIDL);
        assertTrue(initLines.contains("'fast response':"));
        assertTrue(uidlLines.contains("'fast response':"));
    }

    @Test
    void multipleChecks() {
        ResponseCheckConfig config = ResponseCheckConfig.EMPTY
                .withCheck(Scope.INIT, "check1", "(r) => true")
                .withCheck(Scope.UIDL, "check2", "(r) => true")
                .withCheck(Scope.ALL, "check3", "(r) => true");
        String initLines = config.toK6CheckLines(Scope.INIT);
        String uidlLines = config.toK6CheckLines(Scope.UIDL);
        assertTrue(initLines.contains("'check1':"));
        assertTrue(initLines.contains("'check3':"));
        assertTrue(uidlLines.contains("'check2':"));
        assertTrue(uidlLines.contains("'check3':"));
        // check2 should NOT appear in init
        assertTrue(!initLines.contains("'check2':"));
        // check1 should NOT appear in uidl
        assertTrue(!uidlLines.contains("'check1':"));
    }

    @Test
    void parseThreePartFormat() {
        ResponseCheckConfig config = ResponseCheckConfig.EMPTY
                .withChecks("INIT|has title|(r) => r.body.includes('<title>')");
        String lines = config.toK6CheckLines(Scope.INIT);
        assertTrue(lines
                .contains("'has title': (r) => r.body.includes('<title>'),"));
    }

    @Test
    void parseTwoPartFormatDefaultsToAll() {
        ResponseCheckConfig config = ResponseCheckConfig.EMPTY
                .withChecks("fast response|(r) => r.timings.duration < 3000");
        assertTrue(
                config.toK6CheckLines(Scope.INIT).contains("'fast response':"));
        assertTrue(
                config.toK6CheckLines(Scope.UIDL).contains("'fast response':"));
    }

    @Test
    void parseMultipleEntries() {
        ResponseCheckConfig config = ResponseCheckConfig.EMPTY.withChecks(
                "INIT|has title|(r) => r.body.includes('<title>');UIDL|no warning|(r) => !r.body.includes('warning')");
        assertTrue(config.toK6CheckLines(Scope.INIT).contains("'has title':"));
        assertEquals("",
                config.toK6CheckLines(Scope.INIT).contains("'no warning':")
                        ? "fail"
                        : "");
        assertTrue(config.toK6CheckLines(Scope.UIDL).contains("'no warning':"));
    }

    @Test
    void parseCaseInsensitiveScope() {
        ResponseCheckConfig config = ResponseCheckConfig.EMPTY
                .withChecks("init|check1|(r) => true");
        assertTrue(config.toK6CheckLines(Scope.INIT).contains("'check1':"));
    }

    @Test
    void parseIgnoresEmptyEntries() {
        ResponseCheckConfig config = ResponseCheckConfig.EMPTY
                .withChecks("check1|(r) => true;;  ;check2|(r) => true");
        assertTrue(config.toK6CheckLines(Scope.INIT).contains("'check1':"));
        assertTrue(config.toK6CheckLines(Scope.INIT).contains("'check2':"));
    }

    @Test
    void parseInvalidFormatThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ResponseCheckConfig.EMPTY.withChecks("no-pipe-here"));
    }

    @Test
    void parseInvalidScopeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ResponseCheckConfig.EMPTY
                        .withChecks("INVALID|check|(r) => true"));
    }

    @Test
    void parseEmptyNameThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ResponseCheckConfig.EMPTY
                        .withChecks("INIT||(r) => true"));
    }

    @Test
    void withCheckIsImmutable() {
        ResponseCheckConfig original = ResponseCheckConfig.EMPTY;
        ResponseCheckConfig modified = original.withCheck(Scope.INIT, "check",
                "(r) => true");
        assertEquals("", original.toK6CheckLines(Scope.INIT));
        assertTrue(modified.toK6CheckLines(Scope.INIT).contains("'check':"));
    }

    @Test
    void nameWithSingleQuoteIsEscaped() {
        ResponseCheckConfig config = ResponseCheckConfig.EMPTY
                .withCheck(Scope.INIT, "it's valid", "(r) => true");
        String lines = config.toK6CheckLines(Scope.INIT);
        assertTrue(lines.contains("'it\\'s valid':"));
    }
}
