/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.playwright;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.vaadin.testbench.Keys;
import com.vaadin.testbench.StaleElementReferenceException;
import com.vaadin.testbench.backend.selenium.SeleniumBackend;
import com.vaadin.testbench.spi.Backend;
import com.vaadin.testbench.spi.ElementBackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The same page through {@link SeleniumBackend}, the contract, and
 * {@link PlaywrightBackend}: every SPI method must answer the same. Needs a
 * Chrome or Chromium and its chromedriver: {@code webdriver.chrome.driver} and
 * {@code com.vaadin.testbench.playwright.executablePath}; skipped without them.
 */
@EnabledIfSystemProperty(named = "webdriver.chrome.driver", matches = ".+")
@EnabledIfSystemProperty(named = "com.vaadin.testbench.playwright.executablePath", matches = ".+")
class BackendParityTest {

    private static HttpServer server;
    private static String url;
    private static WebDriver driver;
    private static Playwright playwright;
    private static Page page;
    private static Backend selenium;
    private static Backend pw;

    @BeforeAll
    static void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            try (InputStream in = BackendParityTest.class.getResourceAsStream(
                    "/parity/" + path.substring(path.lastIndexOf('/') + 1))) {
                if (in == null) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                byte[] body = in.readAllBytes();
                exchange.getResponseHeaders().add("Content-Type",
                        path.endsWith(".js") ? "text/javascript"
                                : "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
            } finally {
                exchange.close();
            }
        });
        server.start();
        url = "http://127.0.0.1:" + server.getAddress().getPort()
                + "/page.html";

        driver = new ChromeDriver(new ChromeOptions()
                .addArguments("--headless=new", "--window-size=1280,1024"));
        selenium = new SeleniumBackend(driver);

        playwright = Playwright.create(new Playwright.CreateOptions()
                .setEnv(Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1")));
        Browser browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(true)
                        .setExecutablePath(Path.of(System.getProperty(
                                "com.vaadin.testbench.playwright.executablePath"))));
        page = browser.newContext(
                new Browser.NewContextOptions().setViewportSize(1280, 1024))
                .newPage();
        pw = new PlaywrightBackend(page, Duration.ofSeconds(2));
    }

    @AfterAll
    static void stop() {
        if (driver != null) {
            driver.quit();
        }
        if (playwright != null) {
            playwright.close();
        }
        if (server != null) {
            server.stop(0);
        }
    }

    @BeforeEach
    void open() {
        driver.get(url);
        page.navigate(url);
    }

    private static ElementBackend byId(Backend backend, String id) {
        return (ElementBackend) backend.executeScript(
                "return document.getElementById(arguments[0])", id);
    }

    private final List<String> mismatches = new ArrayList<>();

    /** Records where the two backends answer differently. */
    private void same(String what, Function<Backend, Object> call) {
        String se = outcome(selenium, call);
        String p = outcome(pw, call);
        if (!se.equals(p)) {
            mismatches.add(what + ": Selenium " + se + ", Playwright " + p);
        }
    }

    private static String outcome(Backend backend,
            Function<Backend, Object> call) {
        try {
            return describe(call.apply(backend));
        } catch (RuntimeException e) {
            // TestBench's own exceptions must match; past them each backend
            // throws its library's own (a script error, an invalid selector)
            return e.getClass().getName().startsWith("com.vaadin.testbench")
                    || e instanceof IllegalArgumentException
                            ? "throws " + e.getClass().getSimpleName()
                            : "throws the library's exception";
        }
    }

    // Values with their types, nodes by tag and id
    private static String describe(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof ElementBackend element) {
            String name = element.getDomAttribute("id");
            if (name == null) {
                name = element.getDomAttribute("data-name");
            }
            return "<" + element.getTagName() + " " + name + ">";
        }
        if (value instanceof List<?> list) {
            List<String> items = new ArrayList<>();
            list.forEach(item -> items.add(describe(item)));
            return items.toString();
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, String> items = new TreeMap<>();
            map.forEach((k, v) -> items.put(String.valueOf(k),
                    v instanceof ElementBackend || v instanceof List
                            || v instanceof Map
                                    ? describe(v)
                                    : v == null ? "null"
                                            : v.getClass().getSimpleName()
                                                    .replace(
                                                            "ElementHandleImpl",
                                                            "node")
                                                    .replace("RemoteWebElement",
                                                            "node")
                                                    + ":"
                                                    + (v.getClass()
                                                            .getSimpleName()
                                                            .contains("Element")
                                                                    ? ""
                                                                    : v)));
            return items.toString();
        }
        if (value instanceof byte[] bytes) {
            return "bytes";
        }
        return value.getClass().getSimpleName() + ":" + value;
    }

    private void assertNoMismatches() {
        assertTrue(mismatches.isEmpty(), () -> mismatches.size()
                + " mismatches:\n" + String.join("\n", mismatches));
    }

    private static final List<String> READ_IDS = List.of("host", "t-ws", "t-br",
            "t-hidden", "t-vis", "t-upper", "t-nbsp", "t-blocks", "t-shadow",
            "t-none", "t-offscreen", "t-opacity", "t-zero", "t-alpha",
            "t-input", "t-check", "t-radio", "t-link", "t-select", "t-one",
            "t-two", "t-fieldset", "t-in-fieldset", "t-svg", "t-foreign",
            "keys", "area", "button");

    @Test
    void reads() {
        for (String id : READ_IDS) {
            same(id + " tag", b -> byId(b, id).getTagName());
            same(id + " text", b -> byId(b, id).getText());
            for (String attribute : List.of("id", "class", "href", "disabled",
                    "checked", "value", "selected", "tabindex", "name",
                    "nonexistent")) {
                same(id + " getAttribute " + attribute,
                        b -> byId(b, id).getAttribute(attribute));
                same(id + " getDomAttribute " + attribute,
                        b -> byId(b, id).getDomAttribute(attribute));
            }
            for (String property : List.of("value", "checked", "tabIndex",
                    "className", "nonexistent", "childElementCount")) {
                same(id + " getDomProperty " + property,
                        b -> byId(b, id).getDomProperty(property));
            }
            for (String css : List.of("color", "display", "width",
                    "background-color", "font-size")) {
                same(id + " css " + css, b -> byId(b, id).getCssValue(css));
            }
            same(id + " displayed", b -> byId(b, id).isDisplayed());
            same(id + " enabled", b -> byId(b, id).isEnabled());
            same(id + " selected", b -> byId(b, id).isSelected());
            same(id + " rect", b -> byId(b, id).getRect());
        }
        assertNoMismatches();
    }

    @Test
    void scriptResults() {
        for (String expression : List.of("1", "-7", "2**40", "1.5", "1.0",
                "Infinity", "NaN", "null", "undefined", "'s'", "true",
                "[1, 'a', null, undefined, 2.5]",
                "{a: 1, b: [2, 'x'], c: null}", "document.body",
                "document.querySelectorAll('.x')",
                "[document.body, [document.getElementById('keys')]]",
                "document.getElementsByTagName('p')", "{node: document.body}",
                "[]", "{}", "function() {}")) {
            same("return " + expression,
                    b -> b.executeScript("return " + expression));
        }
        same("no return", b -> b.executeScript("var x = 1;"));
        assertNoMismatches();
    }

    @Test
    void scriptArguments() {
        Map<String, Object> map = new HashMap<>();
        map.put("n", null);
        map.put("i", 5);
        List<Object> list = new ArrayList<>(Arrays.asList(1, null, "x"));
        Object[][] cases = { { null }, { 5L }, { 1L << 40 }, { 2.5f },
                { (short) 3 }, { (byte) 4 }, { 'c' },
                { new BigDecimal("1.25") }, { list }, { map },
                { new Object[] { 1, "a" } }, { true }, { "s" } };
        for (Object[] args : cases) {
            same("argument " + describe(args[0]), b -> b.executeScript(
                    "var a = arguments[0]; return [typeof a, a === null, JSON.stringify(a)]",
                    args));
        }
        same("element argument",
                b -> b.executeScript(
                        "return arguments[0].id + ':' + arguments[1].length",
                        byId(b, "keys"),
                        List.of(byId(b, "area"), byId(b, "host"))));
        same("map with element",
                b -> b.executeScript("return arguments[0].e.id",
                        Map.of("e", byId(b, "area"))));
        same("argument count",
                b -> b.executeScript("return arguments.length", 1, null, 3));
        assertNoMismatches();
    }

    @Test
    void asyncScripts() {
        same("async value", b -> b.executeAsyncScript(
                "var done = arguments[arguments.length - 1];"
                        + " setTimeout(() => done(arguments[0] * 2), 10);",
                21));
        same("async node", b -> b.executeAsyncScript(
                "arguments[arguments.length - 1](document.getElementById('keys'))"));
        same("async arguments",
                b -> b.executeAsyncScript(
                        "arguments[arguments.length - 1](arguments.length)", 1,
                        2));
        same("script error", b -> b.executeScript("throw new Error('x')"));
        assertNoMismatches();
    }

    @Test
    void finds() {
        for (String css : List.of("span.x", "*", "x-inner", "div > span",
                ":scope > span", "nothing")) {
            same("find " + css, b -> byId(b, "host").findElements(css));
        }
        same("find invalid", b -> byId(b, "host").findElements("[["));
        assertNoMismatches();
    }

    @Test
    void keys() {
        List<CharSequence[]> sequences = List.of(new CharSequence[] { "abc" },
                new CharSequence[] { Keys.SHIFT, "ab" },
                new CharSequence[] { "a", Keys.SHIFT, "b", Keys.NULL, "c" },
                new CharSequence[] { "ab", Keys.BACK_SPACE, "c" },
                new CharSequence[] { "x", Keys.ARROW_LEFT, "y", Keys.END, "z" },
                new CharSequence[] { "a1", Keys.SHIFT, "2" },
                new CharSequence[] { Keys.NUMPAD1, Keys.ADD, Keys.DECIMAL },
                new CharSequence[] { "hello", Keys.chord(Keys.CONTROL, "a"),
                        "bye" },
                new CharSequence[] { "tab", Keys.TAB });
        for (CharSequence[] keys : sequences) {
            String what = Arrays.toString(keys).replaceAll("[\\uE000-\\uE05D]",
                    "<key>");
            same("sendKeys " + what, b -> {
                b.executeScript("var k = document.getElementById('keys');"
                        + " k.value = 'pre'; window.keyLog = []; k.blur();"
                        + " document.body.focus();");
                byId(b, "keys").sendKeys(keys);
                return b.executeScript(
                        "return [document.getElementById('keys').value,"
                                + " JSON.stringify(window.keyLog.map(e => e.key + '/' + e.mods)),"
                                + " document.activeElement.id]");
            });
        }
        // Selenium also fires a keydown with an empty key per character,
        // Playwright none (R_pw_keys); what arrives is the same
        same("sendKeys non-ASCII", b -> {
            b.executeScript("document.getElementById('keys').value = ''");
            byId(b, "keys").sendKeys("é漢😀");
            return b.executeScript(
                    "return document.getElementById('keys').value");
        });
        same("sendKeys textarea", b -> {
            byId(b, "area").sendKeys("!", Keys.ENTER, "x");
            return b.executeScript(
                    "return document.getElementById('area').value");
        });
        assertNoMismatches();
    }

    @Test
    void clear() {
        same("clear input", b -> {
            b.executeScript(
                    "var k = document.getElementById('keys'); k.value = 'abc';"
                            + " window.events = [];"
                            + " ['input', 'change', 'focus', 'blur'].forEach(t => k.addEventListener(t, () => window.events.push(t)));");
            byId(b, "keys").clear();
            return b.executeScript(
                    "return [document.getElementById('keys').value, window.events.join(',')]");
        });
        same("clear textarea", b -> {
            byId(b, "area").clear();
            return b.executeScript(
                    "return document.getElementById('area').value");
        });
        same("clear disabled", b -> {
            byId(b, "t-input").clear();
            return null;
        });
        same("clear a paragraph", b -> {
            byId(b, "t-ws").clear();
            return null;
        });
        assertNoMismatches();
    }

    @Test
    void mouse() {
        Map<String, Function<ElementBackend, Object>> actions = new LinkedHashMap<>();
        actions.put("click", e -> {
            e.click();
            return null;
        });
        actions.put("click at offset with shift", e -> {
            e.click(10, 5, Keys.SHIFT);
            return null;
        });
        actions.put("doubleClick", e -> {
            e.doubleClick();
            return null;
        });
        actions.put("contextClick", e -> {
            e.contextClick();
            return null;
        });
        actions.put("hover", e -> {
            e.hover();
            return null;
        });
        actions.forEach((name, action) -> same(name, b -> {
            b.executeScript("window.clicks = []; window.scrollTo(0, 0);");
            action.apply(byId(b, "button"));
            return b.executeScript(
                    "return window.clicks.filter(c => c !== 'mouseover').join(';')"
                            + " + (window.clicks.includes('mouseover') ? ' +over' : '')");
        }));
        assertNoMismatches();
    }

    @Test
    void stale() {
        for (Backend b : List.of(selenium, pw)) {
            ElementBackend gone = byId(b, "gone");
            b.executeScript("document.getElementById('gone').remove()");
            assertThrows(StaleElementReferenceException.class, gone::getText,
                    b.getClass().getSimpleName());
            assertThrows(StaleElementReferenceException.class,
                    () -> b.executeScript("return arguments[0].id", gone),
                    b.getClass().getSimpleName());
            assertThrows(StaleElementReferenceException.class, gone::click,
                    b.getClass().getSimpleName());
            ElementBackend keys = byId(b, "keys");
            if (b == selenium) {
                driver.navigate().refresh();
            } else {
                page.reload();
            }
            assertThrows(StaleElementReferenceException.class, keys::getTagName,
                    b.getClass().getSimpleName() + " after a reload");
        }
    }

    @Test
    void equality() {
        for (Backend b : List.of(selenium, pw)) {
            ElementBackend first = byId(b, "keys");
            ElementBackend again = (ElementBackend) b
                    .executeScript("return document.querySelector('#keys')");
            assertEquals(first, again, b.getClass().getSimpleName());
            assertEquals(first.hashCode(), again.hashCode());
            assertNotEquals(first, byId(b, "area"));
        }
    }

    @Test
    void screenshot() {
        same("screenshot is a PNG", b -> {
            byte[] png = byId(b, "button").getScreenshot();
            return png.length > 8 && png[1] == 'P' && png[2] == 'N'
                    && png[3] == 'G';
        });
        assertNoMismatches();
    }

    @Test
    void browserName() {
        assertEquals(selenium.getBrowserName(), pw.getBrowserName());
    }

    static {
        // Fail fast if the fixture is missing
        if (BackendParityTest.class.getResource("/parity/page.html") == null) {
            throw new UncheckedIOException(
                    new IOException("no parity/page.html"));
        }
    }
}
