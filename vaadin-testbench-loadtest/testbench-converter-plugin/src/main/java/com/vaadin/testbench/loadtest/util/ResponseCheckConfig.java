/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for custom response validation checks in generated k6 scripts.
 * Each check is a k6 {@code check()} assertion injected into the init or UIDL
 * response handling code alongside the built-in Vaadin checks.
 * <p>
 * Checks have a scope that controls where they are injected:
 * <ul>
 * <li>{@link Scope#INIT} — only after init requests</li>
 * <li>{@link Scope#UIDL} — only after UIDL requests</li>
 * <li>{@link Scope#ALL} — after both init and UIDL requests</li>
 * </ul>
 * <p>
 * Example usage:
 *
 * <pre>
 * ResponseCheckConfig checks = ResponseCheckConfig.EMPTY
 *         .withCheck(Scope.INIT, "has page title",
 *                 "(r) =&gt; r.body.includes('&lt;title&gt;')")
 *         .withCheck(Scope.UIDL, "no warning",
 *                 "(r) =&gt; !r.body.includes('warning')")
 *         .withCheck(Scope.ALL, "fast response",
 *                 "(r) =&gt; r.timings.duration &lt; 3000");
 * </pre>
 *
 * @param checks
 *            the list of custom response checks
 */
public record ResponseCheckConfig(List<ResponseCheck> checks) {

    /**
     * Scope controlling where a check is injected in the generated k6 script.
     */
    public enum Scope {
        /** Injected after init requests only. */
        INIT,
        /** Injected after UIDL requests only. */
        UIDL,
        /** Injected after both init and UIDL requests. */
        ALL
    }

    /**
     * A single custom response check.
     *
     * @param scope
     *            where to inject the check
     * @param name
     *            the check description shown in k6 output
     * @param expression
     *            the JavaScript check function (e.g.,
     *            {@code "(r) => r.status === 200"})
     */
    public record ResponseCheck(Scope scope, String name, String expression) {
    }

    /**
     * Canonical constructor that makes a defensive copy.
     */
    public ResponseCheckConfig {
        checks = checks != null ? new ArrayList<>(checks) : new ArrayList<>();
    }

    /**
     * Empty configuration with no custom checks.
     */
    public static final ResponseCheckConfig EMPTY = new ResponseCheckConfig(
            List.of());

    /**
     * Returns a new config with an additional custom check.
     *
     * @param scope
     *            where to inject the check
     * @param name
     *            the check description shown in k6 output
     * @param expression
     *            the JavaScript check function (e.g.,
     *            {@code "(r) => r.body.includes('something')"})
     * @return a new ResponseCheckConfig with the additional check
     */
    public ResponseCheckConfig withCheck(Scope scope, String name,
            String expression) {
        List<ResponseCheck> merged = new ArrayList<>(checks);
        merged.add(new ResponseCheck(scope, name, expression));
        return new ResponseCheckConfig(merged);
    }

    /**
     * Parses a custom checks string and applies each entry to this config.
     * Format: {@code scope|name|expression;scope|name|expression;...}
     * <p>
     * The scope is optional and defaults to {@link Scope#ALL}. Valid scopes:
     * {@code INIT}, {@code UIDL}, {@code ALL} (case-insensitive).
     * <p>
     * Examples:
     * <ul>
     * <li>{@code "has title|(r) => r.body.includes('myElement')"} — ALL
     * scope</li>
     * <li>{@code "INIT|has title|(r) => r.body.includes('myElement')"}</li>
     * <li>{@code "UIDL|no warning|(r) => !r.body.includes('warning');ALL|fast|(r) => r.timings.duration < 3000"}</li>
     * </ul>
     *
     * @param checksString
     *            the custom checks string to parse
     * @return a new ResponseCheckConfig with the parsed checks applied
     * @throws IllegalArgumentException
     *             if the format is invalid
     */
    public ResponseCheckConfig withChecks(String checksString) {
        ResponseCheckConfig result = this;
        for (String entry : checksString.split(";")) {
            entry = entry.trim();
            if (entry.isEmpty()) {
                continue;
            }
            String[] parts = entry.split("\\|");
            Scope scope;
            String name;
            String expression;
            if (parts.length == 3) {
                scope = parseScope(parts[0].trim());
                name = parts[1].trim();
                expression = parts[2].trim();
            } else if (parts.length == 2) {
                scope = Scope.ALL;
                name = parts[0].trim();
                expression = parts[1].trim();
            } else {
                throw new IllegalArgumentException(
                        "Invalid custom check format: '" + entry
                                + "'. Expected 'name|expression' or 'scope|name|expression'");
            }
            if (name.isEmpty() || expression.isEmpty()) {
                throw new IllegalArgumentException(
                        "Invalid custom check format: '" + entry
                                + "'. Name and expression must not be empty");
            }
            result = result.withCheck(scope, name, expression);
        }
        return result;
    }

    /**
     * Generates k6 check lines for checks matching the given scope. Returns
     * lines suitable for injection into an existing {@code check(response, {})}
     * block. Each line ends with a comma and newline.
     * <p>
     * Returns an empty string if no checks match the scope.
     *
     * @param scope
     *            the scope to filter by ({@link Scope#INIT} or
     *            {@link Scope#UIDL})
     * @return the generated k6 check lines, or empty string
     */
    public String toK6CheckLines(Scope scope) {
        StringBuilder sb = new StringBuilder();
        for (ResponseCheck check : checks) {
            if (check.scope() == scope || check.scope() == Scope.ALL) {
                sb.append("    '").append(escapeJs(check.name())).append("': ")
                        .append(check.expression()).append(",\n");
            }
        }
        return sb.toString();
    }

    private static Scope parseScope(String scopeStr) {
        try {
            return Scope.valueOf(scopeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid check scope: '"
                    + scopeStr + "'. Must be INIT, UIDL, or ALL");
        }
    }

    private static String escapeJs(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\").replace("'", "\\'");
    }
}
