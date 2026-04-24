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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Refactors k6 test scripts for Vaadin applications.
 *
 * Transforms recorded k6 tests by: - Adding Vaadin helper imports - Replacing
 * hardcoded IPs with configurable variables - Extracting JSESSIONID dynamically
 * from responses - Extracting csrfToken, uiId, pushId dynamically - Converting
 * static cookies to dynamic variables - Adding realistic think time between
 * user actions
 */
public class K6TestRefactorer {

    private static final String HELPER_IMPORT = "import {extractJSessionId, getHillaCsrfToken, getVaadinPushId, getVaadinSecurityKey, getVaadinUiId} from '../utils/vaadin-k6-helpers.js'";

    private static final String CONFIG_VARS = """

            // Server configuration - can be overridden with: k6 run -e APP_IP=192.168.1.100 -e APP_PORT=8081 script.js
            const APP_IP = __ENV.APP_IP || 'localhost';
            const APP_PORT = __ENV.APP_PORT || '8080';
            const BASE_URL = `http://${APP_IP}:${APP_PORT}`;
            """;

    private static final String JSESSION_EXTRACT = """


            // Extract JSESSIONID from Set-Cookie header
            const jsessionId = extractJSessionId(response);
            // Extract Hilla/Spring CSRF token from <meta name="_csrf"> in the HTML
            const hillaCsrfToken = getHillaCsrfToken(response.body);""";

    private static final String VAADIN_EXTRACT = """


            //Request for csrf toke and push id
            //extra csrf token from Vaadin-Security-Key and Vaadin-Push-ID from response
            const csrfToken = getVaadinSecurityKey(response.body);
            const uiId = getVaadinUiId(response.body);
            const pushId = getVaadinPushId(response.body);""";

    // Patterns for detection
    /**
     * Matches the application's {@code http://host:port} prefix. Accepts any
     * DNS-style hostname (including short single-label names like
     * {@code flyfast}) and IPv4 addresses, not just {@code localhost}.
     */
    private static final Pattern SERVER_PATTERN = Pattern.compile(
            "http://([A-Za-z0-9](?:[A-Za-z0-9\\-]*[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9\\-]*[A-Za-z0-9])?)*):(\\d+)");
    private static final Pattern JSESSION_PATTERN = Pattern
            .compile("'[Cc]ookie': 'JSESSIONID=([A-F0-9]+)'");
    private static final Pattern CSRF_PATTERN = Pattern
            .compile("\"csrfToken\":\"([a-f0-9-]{36})\"");
    /**
     * Matches a hardcoded Hilla/Spring CSRF header. Spring's
     * CookieCsrfTokenRepository produces a URL-safe base64 token (>= 20 chars)
     * — distinct from the Vaadin-Security-Key UUID placed in UIDL bodies.
     */
    private static final Pattern HILLA_CSRF_HEADER_PATTERN = Pattern.compile(
            "('[Xx]-[CcXx][Ss][Rr][Ff]-[Tt][Oo][Kk][Ee][Nn]')\\s*:\\s*'([A-Za-z0-9_\\-]{20,})'");

    private static final Logger log = Logger
            .getLogger(K6TestRefactorer.class.getName());
    private final ThinkTimeConfig thinkTimeConfig;

    /**
     * Configuration for think time delays.
     *
     * @param enabled
     *            whether to insert think time delays
     * @param pageReadDelay
     *            base delay after page load in seconds (0 to disable)
     * @param interactionDelay
     *            base delay after user interaction in seconds (0 to disable)
     * @param actionBlockThresholdMs
     *            requests within this threshold are grouped as one "action
     *            block" and only receive think time after the block completes
     *            (default: 100ms)
     * @param existingDelayThresholdMs
     *            if HAR already has a gap larger than this, don't add more
     *            delay (default: 500ms)
     */
    public record ThinkTimeConfig(boolean enabled, double pageReadDelay,
            double interactionDelay, long actionBlockThresholdMs,
            long existingDelayThresholdMs) {

        /** Default configuration with realistic think times enabled. */
        public static final ThinkTimeConfig DEFAULT = new ThinkTimeConfig(true,
                2.0, 0.5, 100, 500);

        /** Configuration with think times disabled for maximum throughput. */
        public static final ThinkTimeConfig DISABLED = new ThinkTimeConfig(
                false, 0, 0, 100, 500);

        /**
         * Simplified constructor for backwards compatibility.
         */
        public ThinkTimeConfig(boolean enabled, double pageReadDelay,
                double interactionDelay) {
            this(enabled, pageReadDelay, interactionDelay, 100, 500);
        }
    }

    /**
     * Creates a refactorer with default think time configuration.
     */
    public K6TestRefactorer() {
        this(ThinkTimeConfig.DEFAULT);
    }

    /**
     * Creates a refactorer with custom think time configuration.
     *
     * @param thinkTimeConfig
     *            think time configuration
     */
    public K6TestRefactorer(ThinkTimeConfig thinkTimeConfig) {
        this.thinkTimeConfig = thinkTimeConfig;
    }

    /**
     * Refactors a k6 test file for Vaadin compatibility.
     *
     * @param inputFile
     *            the input k6 test file
     * @param outputFile
     *            the output refactored test file
     * @throws IOException
     *             if reading or writing fails
     */
    public void refactor(Path inputFile, Path outputFile) throws IOException {
        log.info("Refactoring k6 test for Vaadin...");

        String content = Files.readString(inputFile);
        String refactored = refactorContent(content);

        Files.writeString(outputFile, refactored);

        log.info("Refactored k6 test written to: " + outputFile);
    }

    /**
     * Refactors the content of a k6 test script.
     *
     * @param content
     *            the original script content
     * @return the refactored script content
     */
    public String refactorContent(String content) {
        // 1. Detect the server IP in the script
        ServerInfo server = detectServerIp(content);
        if (server == null) {
            log.warning("Could not detect server IP in the script");
            return content;
        }

        String serverUrl = "http://" + server.ip() + ":" + server.port();
        String hostHeader = server.ip() + ":" + server.port();

        log.info("  Detected server: " + serverUrl);

        // 2. Find JSESSIONID value to replace later
        Matcher jsessionMatcher = JSESSION_PATTERN.matcher(content);
        String jsessionId = jsessionMatcher.find() ? jsessionMatcher.group(1)
                : null;

        // 3. Find csrfToken value to replace later
        Matcher csrfMatcher = CSRF_PATTERN.matcher(content);
        String csrfToken = csrfMatcher.find() ? csrfMatcher.group(1) : null;

        log.info("  JSESSIONID found: " + (jsessionId != null ? "yes" : "no"));
        log.info("  csrfToken found: " + (csrfToken != null ? "yes" : "no"));

        // 4. Add helper import after 'import http from k6/http'
        content = content.replaceFirst("(import http from ['\"]k6/http['\"])",
                "$1\n" + HELPER_IMPORT);

        // 5. Add configuration variables before 'export default function'
        if (!content.contains("const BASE_URL")) {
            content = content.replaceFirst("(export default function)",
                    Matcher.quoteReplacement(CONFIG_VARS) + "\n$1");
        }

        // 6. Replace all hardcoded URLs with BASE_URL (both single-quoted and
        // backtick)
        String escapedServerUrl = Pattern.quote(serverUrl);
        // Single-quoted URLs: 'http://host:port/...' → `${BASE_URL}/...`
        content = content.replaceAll("'" + escapedServerUrl + "(/[^']*)?'",
                Matcher.quoteReplacement("`${BASE_URL}") + "$1`");
        // Backtick URLs: `http://host:port/...` → `${BASE_URL}/...`
        // (these already contain template expressions like ${uiId})
        content = content.replaceAll("`" + escapedServerUrl,
                Matcher.quoteReplacement("`${BASE_URL}"));

        // 7. Replace host headers
        String escapedHostHeader = Pattern.quote(hostHeader);
        content = content.replaceAll("'host': '" + escapedHostHeader + "'",
                Matcher.quoteReplacement("'host': `${APP_IP}:${APP_PORT}`"));

        // 8. Replace origin headers
        content = content.replaceAll("'[Oo]rigin': '" + escapedServerUrl + "'",
                Matcher.quoteReplacement("'Origin': `${BASE_URL}`"));

        // 9. Replace referer headers (both single-quoted and backtick)
        content = content.replaceAll(
                "'[Rr]eferer': '" + escapedServerUrl + "(/[^']*)?'",
                Matcher.quoteReplacement("'Referer': `${BASE_URL}") + "$1`");
        content = content.replaceAll("'[Rr]eferer': `" + escapedServerUrl,
                Matcher.quoteReplacement("'Referer': `${BASE_URL}"));

        // 10. Replace JSESSIONID cookies (handles both 'cookie' and 'Cookie'
        // headers)
        if (jsessionId != null) {
            content = content.replaceAll(
                    "'([Cc]ookie)': 'JSESSIONID=" + Pattern.quote(jsessionId)
                            + "'",
                    "'$1': " + Matcher
                            .quoteReplacement("`JSESSIONID=${jsessionId}`"));
        }

        // 11. Replace csrfToken in POST bodies
        if (csrfToken != null) {
            content = content.replaceAll(
                    "\"csrfToken\":\"" + Pattern.quote(csrfToken) + "\"",
                    Matcher.quoteReplacement("\"csrfToken\":\"${csrfToken}\""));

            // Convert POST body strings from single quotes to template literals
            // when they contain ${
            content = convertPostBodiesToTemplateLiterals(content);
        }

        // 12. Insert JSESSIONID extraction after first GET to the app
        // Skip if already extracted by converter (let jsessionId) or refactorer
        // (const jsessionId)
        if (!content.contains("const jsessionId = extractJSessionId")
                && !content.contains("let jsessionId")) {
            content = insertJsessionExtraction(content);
        }

        // 13. Insert Vaadin token extraction after v-r=init request
        // Skip if already extracted by converter (let uiId) or refactorer
        // (const uiId)
        if (!content.contains("const csrfToken = getVaadinSecurityKey")
                && !content.contains("let uiId")) {
            content = insertVaadinExtraction(content);
        }

        // 14. Rewrite hardcoded Hilla/Spring CSRF headers to read from the
        // cookie jar. The recorded value is tied to the original session;
        // each k6 VU gets its own XSRF-TOKEN cookie that must be echoed back.
        content = rewriteHillaCsrfHeaders(content);

        // 15. Insert realistic think times between user actions
        content = insertThinkTimes(content);

        return content;
    }

    /**
     * Replaces any hardcoded {@code X-CSRF-TOKEN} / {@code X-XSRF-TOKEN} header
     * value with a call to {@code getHillaCsrfToken(BASE_URL)} so each VU sends
     * the CSRF token matching its own session's XSRF-TOKEN cookie.
     *
     * @param content
     *            the k6 script content
     * @return the content with dynamic CSRF header lookups
     */
    private String rewriteHillaCsrfHeaders(String content) {
        Matcher matcher = HILLA_CSRF_HEADER_PATTERN.matcher(content);
        if (!matcher.find()) {
            return content;
        }
        matcher.reset();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        while (matcher.find()) {
            String headerName = matcher.group(1);
            matcher.appendReplacement(sb,
                    Matcher.quoteReplacement(headerName + ": hillaCsrfToken"));
            count++;
        }
        matcher.appendTail(sb);
        log.info("  Hilla CSRF headers rewritten: " + count);
        return sb.toString();
    }

    // Pattern to extract HAR timing delta from comments
    private static final Pattern HAR_DELTA_PATTERN = Pattern
            .compile("// HAR_DELTA_MS: (\\d+)");

    /**
     * Inserts realistic think time delays at user action boundaries.
     * <p>
     * The algorithm: 1. Parses HAR timing deltas from embedded comments 2.
     * Groups requests with gaps &lt; actionBlockThreshold as "action blocks" 3.
     * Adds think time only at action block boundaries: - After v-r=init: page
     * read delay (user reading the loaded page) - After v-r=uidl blocks:
     * interaction delay (user thinking before next action) 4. Skips adding
     * delay if HAR already has a large gap (existing user delay in TestBench)
     *
     * @param content
     *            the k6 script content
     * @return the modified content with think time delays inserted
     */
    private String insertThinkTimes(String content) {
        if (!thinkTimeConfig.enabled()) {
            log.info("  Think times: disabled");
            return content;
        }

        List<String> lines = new ArrayList<>(List.of(content.split("\n")));

        // First pass: collect request info with timing
        List<RequestInfo> requests = parseRequestsWithTiming(lines);

        if (requests.isEmpty()) {
            log.info("  Think times: no requests found");
            return content;
        }

        // Second pass: identify user actions and block boundaries, insert
        // delays
        List<Integer> insertionPoints = new ArrayList<>();
        List<String> delaysToInsert = new ArrayList<>();

        int pageReadDelays = 0;
        int interactionDelays = 0;
        int skippedDueToExistingDelay = 0;

        // Track current action block for init detection
        boolean blockContainsInit = false;
        int initEndLineIndex = -1;

        for (int i = 0; i < requests.size(); i++) {
            RequestInfo req = requests.get(i);
            RequestInfo nextReq = (i + 1 < requests.size())
                    ? requests.get(i + 1)
                    : null;

            // Update block tracking for init
            if (req.isInit) {
                blockContainsInit = true;
                initEndLineIndex = req.endLineIndex;
            }

            // Check timing to next request
            long nextDeltaMs = (nextReq != null) ? nextReq.harDeltaMs
                    : Long.MAX_VALUE;
            boolean isBlockBoundary = (nextReq == null
                    || nextDeltaMs > thinkTimeConfig.actionBlockThresholdMs());
            boolean hasExistingDelay = nextDeltaMs >= thinkTimeConfig
                    .existingDelayThresholdMs();

            // Case 1: Block containing init ends - add page read delay
            if (isBlockBoundary && blockContainsInit
                    && thinkTimeConfig.pageReadDelay() > 0) {
                if (hasExistingDelay) {
                    skippedDueToExistingDelay++;
                } else {
                    // Insert after Vaadin extraction if it exists
                    int insertAt = req.endLineIndex;
                    if (initEndLineIndex > 0
                            && initEndLineIndex + 1 < lines.size()
                            && lines.get(initEndLineIndex + 1)
                                    .contains("getVaadinSecurityKey")) {
                        insertAt = initEndLineIndex + 5;
                    }
                    insertionPoints.add(insertAt);
                    delaysToInsert
                            .add(generateDelayCode("user reading the page",
                                    thinkTimeConfig.pageReadDelay(),
                                    thinkTimeConfig.pageReadDelay() * 1.5));
                    pageReadDelays++;
                }
                // Reset init tracking
                blockContainsInit = false;
                initEndLineIndex = -1;
                continue; // Don't also add interaction delay
            }

            // Case 2: User action detected (click, text input) - add
            // interaction delay
            if (req.isUserAction && thinkTimeConfig.interactionDelay() > 0) {
                if (hasExistingDelay) {
                    skippedDueToExistingDelay++;
                } else {
                    insertionPoints.add(req.endLineIndex);
                    delaysToInsert.add(generateDelayCode(
                            "user thinking before next action",
                            thinkTimeConfig.interactionDelay(),
                            thinkTimeConfig.interactionDelay() * 3));
                    interactionDelays++;
                }
            }

            // Reset init tracking at block boundaries
            if (isBlockBoundary) {
                blockContainsInit = false;
                initEndLineIndex = -1;
            }
        }

        // Insert delays in reverse order to maintain line indices
        for (int i = insertionPoints.size() - 1; i >= 0; i--) {
            int insertAt = insertionPoints.get(i);
            if (insertAt < lines.size()) {
                lines.add(insertAt + 1, delaysToInsert.get(i));
            }
        }

        log.info("  Think times: " + (pageReadDelays + interactionDelays)
                + " delays inserted " + "(page: " + pageReadDelays + " @ "
                + thinkTimeConfig.pageReadDelay() + "s base, " + "interaction: "
                + interactionDelays + " @ " + thinkTimeConfig.interactionDelay()
                + "s base)");
        if (skippedDueToExistingDelay > 0) {
            log.info("  Think times: " + skippedDueToExistingDelay
                    + " delay(s) skipped (HAR already has gaps >= "
                    + thinkTimeConfig.existingDelayThresholdMs() + "ms)");
        }

        return String.join("\n", lines);
    }

    // Patterns to detect user actions in UIDL content
    private static final Pattern CLICK_EVENT_PATTERN = Pattern
            .compile("\"event\":\"click\"");
    private static final Pattern CHANGE_EVENT_PATTERN = Pattern
            .compile("\"event\":\"change\"");
    private static final Pattern VALUE_CHANGE_WITH_DATA = Pattern
            .compile("\"value\":\"[^\"]+\"");

    /**
     * Parses request information including HAR timing from the generated
     * script.
     *
     * @param lines
     *            the script lines to parse
     * @return list of parsed request info objects
     */
    private List<RequestInfo> parseRequestsWithTiming(List<String> lines) {
        List<RequestInfo> requests = new ArrayList<>();
        long currentHarDelta = 0;
        boolean inRequest = false;
        boolean isUidl = false;
        boolean isInit = false;
        boolean isUserAction = false;
        int parenCount = 0;
        boolean seenOpenParen = false;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            // Parse HAR timing delta
            Matcher deltaMatcher = HAR_DELTA_PATTERN.matcher(line);
            if (deltaMatcher.find()) {
                currentHarDelta = Long.parseLong(deltaMatcher.group(1));
            }

            // Detect start of request
            if (line.contains("v-r=init") && !inRequest) {
                inRequest = true;
                isInit = true;
                isUidl = false;
                isUserAction = false;
                parenCount = 0;
                seenOpenParen = false;
            } else if (line.contains("v-r=uidl") && !inRequest) {
                inRequest = true;
                isUidl = true;
                isInit = false;
                isUserAction = false;
                parenCount = 0;
                seenOpenParen = false;
            } else if ((line.contains("http.get(")
                    || line.contains("http.post(")) && !inRequest) {
                inRequest = true;
                isInit = false;
                isUidl = false;
                isUserAction = false;
                parenCount = 0;
                seenOpenParen = false;
            }

            if (inRequest) {
                int opens = countOccurrences(line, '(');
                parenCount += opens;
                parenCount -= countOccurrences(line, ')');
                if (opens > 0) {
                    seenOpenParen = true;
                }

                // Check for user action patterns in UIDL requests
                if (isUidl) {
                    if (CLICK_EVENT_PATTERN.matcher(line).find()) {
                        isUserAction = true;
                    }
                    // Change event with actual value = user typed something
                    if (CHANGE_EVENT_PATTERN.matcher(line).find()
                            && VALUE_CHANGE_WITH_DATA.matcher(line).find()) {
                        isUserAction = true;
                    }
                }

                // End of request — the http.XXX(...) call's parens are
                // balanced back to zero. Handles both single-line
                // `http.post(url, body)` and multi-line forms where the
                // closing `)` sits on its own line.
                if (seenOpenParen && parenCount == 0) {
                    requests.add(new RequestInfo(i, currentHarDelta, isInit,
                            isUidl, isUserAction));
                    inRequest = false;
                    isInit = false;
                    isUidl = false;
                    isUserAction = false;
                    currentHarDelta = 0;
                }
            }
        }

        return requests;
    }

    /**
     * Information about a parsed request.
     *
     * @param endLineIndex
     *            line index where the request ends
     * @param harDeltaMs
     *            time delta from previous request in ms
     * @param isInit
     *            true if this is a v-r=init request
     * @param isUidl
     *            true if this is a v-r=uidl request
     * @param isUserAction
     *            true if this UIDL contains a user action (click, text input)
     */
    private record RequestInfo(int endLineIndex, long harDeltaMs,
            boolean isInit, boolean isUidl, boolean isUserAction) {
    }

    /**
     * Generates JavaScript code for a sleep delay with randomness.
     *
     * @param comment
     *            the comment describing the delay type
     * @param baseDelay
     *            the base delay in seconds
     * @param randomRange
     *            the random range added to the base delay
     * @return the generated JavaScript sleep code
     */
    private String generateDelayCode(String comment, double baseDelay,
            double randomRange) {
        // Use Locale.US to ensure period decimal separator in generated
        // JavaScript
        return String.format(java.util.Locale.US, """

                // Think time: %s
                sleep(%.1f + Math.random() * %.1f);""", comment, baseDelay,
                randomRange);
    }

    /**
     * Detects server host and port in the script.
     *
     * @param content
     *            the k6 script content
     * @return the detected server info, or {@code null} if not found
     */
    private ServerInfo detectServerIp(String content) {
        Matcher matcher = SERVER_PATTERN.matcher(content);
        if (matcher.find()) {
            return new ServerInfo(matcher.group(1), matcher.group(2));
        }
        return null;
    }

    /**
     * Converts POST body strings from single quotes to template literals when
     * they contain ${.
     *
     * @param content
     *            the k6 script content
     * @return the modified content with template literals
     */
    private String convertPostBodiesToTemplateLiterals(String content) {
        // Match: http.post(..., '{...${csrfToken}...}', ...)
        Pattern postPattern = Pattern.compile(
                "(http\\.post\\(\\s*`[^`]+`,\\s*)'(\\{[^']*\\$\\{csrfToken\\}[^']*\\})'",
                Pattern.MULTILINE);
        Matcher matcher = postPattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String replacement = matcher.group(1) + "`" + matcher.group(2)
                    + "`";
            matcher.appendReplacement(sb,
                    Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Inserts JSESSIONID extraction after first GET to the app.
     *
     * @param content
     *            the k6 script content
     * @return the modified content with JSESSIONID extraction
     */
    private String insertJsessionExtraction(String content) {
        List<String> lines = new ArrayList<>(List.of(content.split("\n")));
        boolean foundFirstRequest = false;
        boolean inFirstAppRequest = false;
        int braceCount = 0;
        int insertLineIndex = -1;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            // Look for "// Request 1:" comment that marks the first request
            if (line.contains("// Request 1:") && !foundFirstRequest) {
                foundFirstRequest = true;
            }

            // Once we found the comment, look for http.get on subsequent lines
            if (foundFirstRequest && !inFirstAppRequest
                    && line.contains("http.get(")) {
                inFirstAppRequest = true;
                braceCount = 0;
            }

            if (inFirstAppRequest) {
                braceCount += countOccurrences(line, '{');
                braceCount -= countOccurrences(line, '}');

                if (braceCount == 0 && line.contains(")")) {
                    insertLineIndex = i;
                    break;
                }
            }
        }

        if (insertLineIndex > 0) {
            lines.add(insertLineIndex + 1, JSESSION_EXTRACT);
            return String.join("\n", lines);
        }

        return content;
    }

    /**
     * Inserts Vaadin token extraction after v-r=init request.
     *
     * @param content
     *            the k6 script content
     * @return the modified content with Vaadin token extraction
     */
    private String insertVaadinExtraction(String content) {
        List<String> lines = new ArrayList<>(List.of(content.split("\n")));
        boolean inInitRequest = false;
        int braceCount = 0;
        int insertLineIndex = -1;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.contains("v-r=init") && !inInitRequest) {
                inInitRequest = true;
                braceCount = 0;
            }

            if (inInitRequest) {
                braceCount += countOccurrences(line, '{');
                braceCount -= countOccurrences(line, '}');

                if (braceCount == 0 && line.contains(")")) {
                    insertLineIndex = i;
                    break;
                }
            }
        }

        if (insertLineIndex > 0) {
            lines.add(insertLineIndex + 1, VAADIN_EXTRACT);
            return String.join("\n", lines);
        }

        return content;
    }

    private int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    private record ServerInfo(String ip, String port) {
    }
}
