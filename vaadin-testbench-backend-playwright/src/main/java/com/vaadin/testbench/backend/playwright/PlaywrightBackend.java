/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.playwright;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.JSHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import org.jspecify.annotations.Nullable;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.StaleElementReferenceException;
import com.vaadin.testbench.TimeoutException;
import com.vaadin.testbench.spi.Backend;
import com.vaadin.testbench.spi.ElementBackend;

/**
 * The {@link Backend} on a Playwright {@link Page}. Scripts keep Selenium's
 * {@code arguments[n]} convention and its types: a whole number comes back as a
 * {@code Long}, {@code Infinity} and {@code NaN} as {@code null}, a
 * {@code null} argument arrives as {@code null}. A script given a node that is
 * no longer in the document throws {@link StaleElementReferenceException}, as
 * on Selenium; a Playwright handle would read the detached node on.
 */
public class PlaywrightBackend implements Backend {

    // Selenium's default script timeout
    private static final Duration ASYNC_SCRIPT_TIMEOUT = Duration.ofSeconds(30);

    private final Page page;
    private final Duration actionTimeout;

    public PlaywrightBackend(Page page) {
        this(page, Parameters.getActionTimeout());
    }

    PlaywrightBackend(Page page, Duration actionTimeout) {
        this.page = page;
        this.actionTimeout = actionTimeout;
    }

    /** The page this backend drives; the escape hatch to Playwright. */
    public Page getPage() {
        return page;
    }

    /** How long an action waits for its element to take it. */
    Duration getActionTimeout() {
        return actionTimeout;
    }

    @Override
    public @Nullable Object executeScript(String script,
            @Nullable Object... args) {
        return run(PRELUDE + "return finish((function() {\n" + script
                + "\n}).apply(window, args));\n}", args);
    }

    @Override
    public @Nullable Object executeAsyncScript(String script,
            @Nullable Object... args) {
        return run(PRELUDE + "return new Promise((resolve, reject) => {\n"
                + "const timer = setTimeout(() => reject(new Error('"
                + SCRIPT_TIMEOUT + "')), " + ASYNC_SCRIPT_TIMEOUT.toMillis()
                + ");\n"
                + "const done = r => { clearTimeout(timer); try { resolve(finish(r)); } catch (e) { reject(e); } };\n"
                + "try { (function() {\n" + script
                + "\n}).apply(window, args.concat([done])); }"
                + " catch (e) { clearTimeout(timer); reject(e); }\n});\n}",
                args);
    }

    /**
     * WebDriver's name for the browser: {@code "chrome"} for Chromium,
     * {@code "firefox"}; WebKit, which WebDriver does not drive, is
     * {@code "webkit"}.
     */
    @Override
    public @Nullable String getBrowserName() {
        Browser browser = page.context().browser();
        if (browser == null) {
            return null;
        }
        String name = browser.browserType().name();
        return "chromium".equals(name) ? "chrome" : name;
    }

    private static final String SCRIPT_TIMEOUT = "__tbScriptTimeout";

    // Turns the arguments back from what Playwright can send, refuses a
    // detached node, and defines finish(), which replaces each node in the
    // result by a marker with a stable id: the nodes themselves come back only
    // through a handle, fetched in a second call when there are any.
    private static final String PRELUDE = """
            input => {
            let stale = false;
            const decode = v => {
              if (v instanceof Node) { if (!v.isConnected) stale = true; return v; }
              if (Array.isArray(v)) return v.map(decode);
              if (v !== null && typeof v === 'object') {
                const keys = Object.keys(v);
                if (keys.length === 1 && v.__tbNull === true) return null;
                const o = {};
                for (const k of keys) o[k] = decode(v[k]);
                return o;
              }
              return v;
            };
            const args = decode(input);
            if (stale) return {stale: true};
            const ids = window.__tbIds || (window.__tbIds = new WeakMap());
            const doc = window.__tbDoc || (window.__tbDoc = Math.random().toString(36).slice(2));
            const finish = result => {
              const nodes = [];
              const seen = new Set();
              const encode = v => {
                if (v === undefined || typeof v === 'symbol') return null;
                if (typeof v === 'function') return {};
                if (typeof v === 'number') return isFinite(v) ? v : null;
                if (v instanceof Node) {
                  let id = ids.get(v);
                  if (id === undefined) {
                    window.__tbNextId = (window.__tbNextId || 0) + 1;
                    id = doc + ':' + window.__tbNextId;
                    ids.set(v, id);
                  }
                  nodes.push(v);
                  return {__tbNode: nodes.length - 1, id: id};
                }
                if (v === null || typeof v !== 'object') return v;
                if (seen.has(v)) throw new TypeError('cyclic object value');
                seen.add(v);
                let out;
                if (Array.isArray(v) || v instanceof NodeList || v instanceof HTMLCollection) {
                  out = Array.from(v, encode);
                } else {
                  // As chromedriver serializes an object: inherited properties
                  // too (SVG's className), the keys sorted
                  const keys = [];
                  for (const k in v) if (typeof v[k] !== 'function') keys.push(k);
                  out = {};
                  for (const k of keys.sort()) out[k] = encode(v[k]);
                }
                seen.delete(v);
                return out;
              };
              const value = encode(result);
              if (nodes.length) window.__tbNodes = nodes;
              return {value: value, nodeCount: nodes.length};
            };
            """;

    private @Nullable Object run(String source, @Nullable Object[] args) {
        Map<?, ?> result;
        List<ElementHandle> nodes;
        try {
            result = (Map<?, ?>) page.evaluate(source, toPlaywright(args));
            if (Boolean.TRUE.equals(result.get("stale"))) {
                throw new StaleElementReferenceException(
                        "The element is no longer attached to the DOM");
            }
            int nodeCount = ((Number) result.get("nodeCount")).intValue();
            nodes = nodeCount == 0 ? List.of() : fetchNodes(nodeCount);
        } catch (PlaywrightException e) {
            throw translate(e);
        }
        return fromPlaywright(result.get("value"), nodes, false);
    }

    private List<ElementHandle> fetchNodes(int count) {
        JSHandle array = page.evaluateHandle(
                "() => { const n = window.__tbNodes; delete window.__tbNodes; return n; }");
        try {
            Map<String, JSHandle> properties = array.getProperties();
            List<ElementHandle> nodes = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                nodes.add(properties.get(String.valueOf(i)).asElement());
            }
            return nodes;
        } finally {
            array.dispose();
        }
    }

    /**
     * TestBench's own exception for Playwright's timeouts and for a node that
     * is gone, detached or in a page navigated away from; any other comes back
     * as it is.
     */
    static RuntimeException translate(PlaywrightException e) {
        String message = String.valueOf(e.getMessage());
        if (e instanceof com.microsoft.playwright.TimeoutError) {
            return new TimeoutException(message, e);
        }
        if (message.contains(SCRIPT_TIMEOUT)) {
            return new TimeoutException("script timeout after "
                    + ASYNC_SCRIPT_TIMEOUT.toSeconds() + " s", e);
        }
        if (message.contains("not attached to the DOM")
                || message.contains("Element is detached")
                || message.contains("Execution context was destroyed")
                || message.contains("Cannot find context with specified id")) {
            return new StaleElementReferenceException(message, e);
        }
        return e;
    }

    // Playwright drops a null and refuses Long, Float, Short, Byte and
    // BigDecimal, which Selenium sends
    private static final Map<String, Boolean> NULL = Map.of("__tbNull", true);

    private static List<Object> toPlaywright(@Nullable Object[] args) {
        return toPlaywright(Arrays.asList(args));
    }

    private static List<Object> toPlaywright(Collection<?> values) {
        List<Object> list = new ArrayList<>(values.size());
        for (Object value : values) {
            list.add(toPlaywright(value));
        }
        return list;
    }

    private static Object toPlaywright(@Nullable Object value) {
        if (value == null) {
            return NULL;
        }
        if (value instanceof PlaywrightElementBackend element) {
            return element.getElementHandle();
        }
        if (value instanceof JSHandle handle) {
            return handle;
        }
        if (value instanceof ElementBackend other) {
            throw new IllegalArgumentException(
                    "An element of another backend: " + other);
        }
        if (value instanceof Integer || value instanceof Double
                || value instanceof Boolean || value instanceof String) {
            return value;
        }
        if (value instanceof Long number) {
            return number == number.intValue() ? (Object) number.intValue()
                    : (Object) number.doubleValue();
        }
        if (value instanceof Short || value instanceof Byte) {
            return ((Number) value).intValue();
        }
        if (value instanceof Float || value instanceof BigDecimal
                || value instanceof BigInteger) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof Collection<?> collection) {
            return toPlaywright(collection);
        }
        if (value instanceof Object[] array) {
            return toPlaywright(Arrays.asList(array));
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            map.forEach((key, item) -> copy.put(String.valueOf(key),
                    toPlaywright(item)));
            return copy;
        }
        // As Selenium refuses it
        throw new IllegalArgumentException("Argument is of an illegal type: "
                + value.getClass().getName());
    }

    // As Selenium returns them: a whole number a Long, nodes as elements, but
    // inside a map as the driver's own handles, as Selenium leaves a WebElement
    // there
    private @Nullable Object fromPlaywright(@Nullable Object value,
            List<ElementHandle> nodes, boolean inMap) {
        if (value instanceof Map<?, ?> map) {
            if (map.get("__tbNode") instanceof Number index
                    && map.get("id") instanceof String id && map.size() == 2) {
                ElementHandle node = nodes.get(index.intValue());
                return inMap ? node
                        : new PlaywrightElementBackend(this, node, id);
            }
            Map<Object, Object> copy = new LinkedHashMap<>();
            map.forEach((key, item) -> copy.put(key,
                    fromPlaywright(item, nodes, true)));
            return copy;
        }
        if (value instanceof List<?> list) {
            List<Object> copy = new ArrayList<>(list.size());
            for (Object item : list) {
                copy.add(fromPlaywright(item, nodes, inMap));
            }
            return copy;
        }
        if (value instanceof Integer || value instanceof Short
                || value instanceof Byte) {
            return ((Number) value).longValue();
        }
        if (value instanceof Double number) {
            double d = number;
            if (d == Math.rint(d) && Math.abs(d) < 0x1p63) {
                return (long) d;
            }
            return d;
        }
        return value;
    }
}
