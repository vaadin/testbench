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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class K6TestRefactorerTest {

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
