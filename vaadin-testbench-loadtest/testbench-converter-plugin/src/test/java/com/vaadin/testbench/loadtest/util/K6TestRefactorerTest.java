/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vaadin.testbench.loadtest.util.K6TestRefactorer.ThinkTimeConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class K6TestRefactorerTest {

    @TempDir
    Path tempDir;

    private static final String MINIMAL_SCRIPT = """
            import http from 'k6/http'
            import { sleep } from 'k6'
            export default function() {
              http.get('http://localhost:8080/')
            }
            """;

    @Test
    void refactorContent_addsVaadinHelperImport() {
        K6TestRefactorer refactorer = new K6TestRefactorer(
                ThinkTimeConfig.DISABLED);

        String refactored = refactorer.refactorContent(MINIMAL_SCRIPT);

        assertTrue(refactored.contains("from '../utils/vaadin-k6-helpers.js'"),
                "Expected helper import statement in refactored output:\n"
                        + refactored);
        assertTrue(refactored.contains("getVaadinSecurityKey"));
    }

    @Test
    void refactorContent_addsBaseUrlConstants() {
        K6TestRefactorer refactorer = new K6TestRefactorer(
                ThinkTimeConfig.DISABLED);

        String refactored = refactorer.refactorContent(MINIMAL_SCRIPT);

        // Full declarations — the refactorer hardcodes these, so assert the
        // exact values rather than matching only the identifier prefix.
        assertTrue(
                refactored.contains(
                        "const APP_IP = __ENV.APP_IP || 'localhost';"),
                "Expected APP_IP declaration:\n" + refactored);
        assertTrue(
                refactored
                        .contains("const APP_PORT = __ENV.APP_PORT || '8080';"),
                "Expected APP_PORT declaration:\n" + refactored);
        assertTrue(
                refactored.contains(
                        "const BASE_URL = `http://${APP_IP}:${APP_PORT}`;"),
                "Expected BASE_URL declaration:\n" + refactored);
    }

    @Test
    void refactorContent_replacesHardcodedUrlWithBaseUrl() {
        K6TestRefactorer refactorer = new K6TestRefactorer(
                ThinkTimeConfig.DISABLED);

        String refactored = refactorer.refactorContent(MINIMAL_SCRIPT);

        assertTrue(refactored.contains("`${BASE_URL}"),
                "Expected BASE_URL template literal:\n" + refactored);
        assertFalse(refactored.contains("'http://localhost:8080/'"),
                "Hardcoded URL should have been replaced:\n" + refactored);
    }

    @Test
    void refactorContent_noServerDetected_returnsInputUnchanged() {
        K6TestRefactorer refactorer = new K6TestRefactorer(
                ThinkTimeConfig.DISABLED);
        String scriptWithoutServer = """
                import http from 'k6/http'
                export default function() { /* no URLs */ }
                """;

        String refactored = refactorer.refactorContent(scriptWithoutServer);

        assertEquals(scriptWithoutServer, refactored);
    }

    @Test
    void refactorContent_thinkTimesDisabled_noThinkTimeComments() {
        K6TestRefactorer refactorer = new K6TestRefactorer(
                ThinkTimeConfig.DISABLED);

        String refactored = refactorer.refactorContent(MINIMAL_SCRIPT);

        assertFalse(refactored.contains("// Think time:"),
                "Think times should not appear when disabled:\n" + refactored);
    }

    @Test
    void refactorContent_thinkTimesEnabled_injectsDelaysAtBlockBoundary() {
        K6TestRefactorer refactorer = new K6TestRefactorer(
                ThinkTimeConfig.DEFAULT);

        // Script with v-r=init and two uidl blocks separated by a large gap so
        // the block boundary is triggered by actionBlockThresholdMs (100ms).
        String scriptWithUidl = """
                import http from 'k6/http'
                import { sleep } from 'k6'
                export default function() {
                  // HAR_DELTA_MS: 0
                  // v-r=init request
                  http.post('http://localhost:8080/?v-r=init', 'body')
                  // HAR_DELTA_MS: 50
                  // v-r=uidl request with click event
                  http.post('http://localhost:8080/?v-r=uidl', '{"event":"click"}')
                  // HAR_DELTA_MS: 50
                  // Another uidl request
                  http.post('http://localhost:8080/?v-r=uidl', '{}')
                }
                """;

        String refactored = refactorer.refactorContent(scriptWithUidl);

        assertTrue(refactored.contains("// Think time:"),
                "Expected think time comment when enabled with uidl blocks:\n"
                        + refactored);
        // Ensure an actual sleep() call is emitted so generated scripts really
        // pause between user actions.
        assertTrue(refactored.matches(
                "(?s).*sleep\\(\\d+\\.\\d+\\s*\\+\\s*Math\\.random\\(\\)\\s*\\*\\s*\\d+\\.\\d+\\);.*"),
                "Expected randomized sleep(...) call in refactored output:\n"
                        + refactored);
    }

    @Test
    void refactor_readsInputAndWritesOutputFile() throws IOException {
        Path input = tempDir.resolve("in.js");
        Path output = tempDir.resolve("out.js");
        Files.writeString(input, MINIMAL_SCRIPT);

        new K6TestRefactorer(ThinkTimeConfig.DISABLED).refactor(input, output);

        assertTrue(Files.exists(output));
        String content = Files.readString(output);
        assertTrue(content.contains("vaadin-k6-helpers.js"));
    }

    @Test
    void thinkTimeConfigRecord_simpleConstructorUsesDefaultThresholds() {
        ThinkTimeConfig config = new ThinkTimeConfig(true, 2.0, 0.5);
        assertEquals(100L, config.actionBlockThresholdMs());
        assertEquals(500L, config.existingDelayThresholdMs());
    }

    @Test
    void hillaCsrfHeaderIsRewrittenToCookieJarLookup() {
        String script = """
                import http from 'k6/http'

                export default function () {
                  // Request 1: GET http://localhost:8080/
                  let response = http.get('http://localhost:8080/', { headers: {} })

                  // Request 2: POST http://localhost:8080/connect/MovieEndpoint/list
                  response = http.post(
                    'http://localhost:8080/connect/MovieEndpoint/list',
                    '{}',
                    {
                      headers: {
                      'X-CSRF-TOKEN': 'GSefoi3yDjDgBenJkNFVEkX0M4pdcplHxAPS8ELavtHAbKTHe0X7lB7Ka1bNZor7pvxhJHPBHug4Rq1q9jXqlHO-h7X2XsD_'
                    }
                    }
                  )
                }
                """;

        K6TestRefactorer refactorer = new K6TestRefactorer(
                K6TestRefactorer.ThinkTimeConfig.DISABLED);
        String refactored = refactorer.refactorContent(script);

        assertTrue(refactored.contains("'X-CSRF-TOKEN': hillaCsrfToken"),
                "Hardcoded X-CSRF-TOKEN should be replaced with dynamic lookup. Got:\n"
                        + refactored);
        assertFalse(refactored.contains("GSefoi3yDjDgBen"),
                "Recorded CSRF token literal should be gone. Got:\n"
                        + refactored);
        assertTrue(refactored.contains("getHillaCsrfToken"),
                "Helper import should include getHillaCsrfToken. Got:\n"
                        + refactored);
    }

    @Test
    void nonLocalhostHostnameIsDetectedAsServer() {
        // Reproduces a real failure: servers named by short DNS hostname (e.g.
        // "flyfast") were ignored by the refactorer, so BASE_URL substitution
        // and the Hilla CSRF rewrite were both silently skipped.
        String script = """
                import http from 'k6/http'

                export default function () {
                  // Request 1: GET http://flyfast:8081/
                  let response = http.get('http://flyfast:8081/', { headers: {} })

                  // Request 2: POST http://flyfast:8081/connect/MovieEndpoint/list
                  response = http.post(
                    'http://flyfast:8081/connect/MovieEndpoint/list',
                    '{}',
                    {
                      headers: {
                      'X-CSRF-TOKEN': 'NwhHbrWHhCCjCu328_eiLRpD9O2I3dqhEvXhUHEl7b6u5tuhAjhxW4W05USOO9uXl9qWGiwl2Y'
                    }
                    }
                  )
                }
                """;

        K6TestRefactorer refactorer = new K6TestRefactorer(
                K6TestRefactorer.ThinkTimeConfig.DISABLED);
        String refactored = refactorer.refactorContent(script);

        assertTrue(refactored.contains("const BASE_URL"),
                "BASE_URL should be declared when the host is a DNS hostname. Got:\n"
                        + refactored);
        assertTrue(refactored.contains("'X-CSRF-TOKEN': hillaCsrfToken"),
                "Hilla CSRF header should be rewritten even when host is not localhost. Got:\n"
                        + refactored);
        assertTrue(refactored.contains("`${BASE_URL}/`"),
                "Single-quoted URL should be replaced with BASE_URL template. Got:\n"
                        + refactored);
        assertTrue(
                refactored.contains("`${BASE_URL}/connect/MovieEndpoint/list`"),
                "Path URL should be replaced with BASE_URL template. Got:\n"
                        + refactored);
    }

    @Test
    void refactorWithoutCsrfHeaderIsUnchanged() {
        String script = """
                import http from 'k6/http'

                export default function () {
                  // Request 1: GET http://localhost:8080/
                  let response = http.get('http://localhost:8080/', { headers: {} })
                }
                """;

        K6TestRefactorer refactorer = new K6TestRefactorer(
                K6TestRefactorer.ThinkTimeConfig.DISABLED);
        String refactored = refactorer.refactorContent(script);

        // Helper import is always added, but no CSRF lookup call appears when
        // the script has no Hilla endpoint requests.
        assertFalse(refactored.contains(": hillaCsrfToken"),
                "Scripts without Hilla endpoints should not reference hillaCsrfToken. Got:\n"
                        + refactored);
    }
}
