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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
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

import com.vaadin.testbench.Actions;
import com.vaadin.testbench.BrowserDriver;
import com.vaadin.testbench.By;
import com.vaadin.testbench.Dimension;
import com.vaadin.testbench.ElementQuery;
import com.vaadin.testbench.Keys;
import com.vaadin.testbench.Point;
import com.vaadin.testbench.StaleElementReferenceException;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.backend.selenium.SeleniumBackend;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;
import com.vaadin.testbench.spi.BrowserBackend;
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
    private static BrowserBackend selenium;
    private static BrowserBackend pw;

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

    private static ElementBackend byId(BrowserBackend backend, String id) {
        return (ElementBackend) backend.executeScript(
                "return document.getElementById(arguments[0])", id);
    }

    private final List<String> mismatches = new ArrayList<>();

    /** Records where the two backends answer differently. */
    private void same(String what, Function<BrowserBackend, Object> call) {
        String se = outcome(selenium, call);
        String p = outcome(pw, call);
        if (!se.equals(p)) {
            mismatches.add(what + ": Selenium " + se + ", Playwright " + p);
        }
    }

    private static String outcome(BrowserBackend backend,
            Function<BrowserBackend, Object> call) {
        try {
            return describe(call.apply(backend));
        } catch (RuntimeException e) {
            if (System.getProperty("parity.trace") != null) {
                e.printStackTrace();
            }
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
        // Playwright none; what arrives is the same
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

    private static BrowserDriver driverOn(BrowserBackend backend) {
        return TestBench.createDriver(backend);
    }

    /** By.xpath, from an element and from the page, as the corpus uses it. */
    @Test
    void xpath() {
        Map<String, Function<BrowserDriver, Object>> cases = new LinkedHashMap<>();
        cases.put("parent",
                d -> ids(element(d, "t-one").findElements(By.xpath(".."))));
        cases.put("children",
                d -> ids(element(d, "t-select").findElements(By.xpath("./*"))));
        cases.put("a child by attribute", d -> ids(element(d, "t-select")
                .findElements(By.xpath("./*[@value='2']"))));
        cases.put("the page from an element", d -> ids(element(d, "t-select")
                .findElements(By.xpath("//*[@id='t-link']"))));
        cases.put("by text",
                d -> ids(d.findElements(By.xpath("//*[text()='two']"))));
        cases.put("none", d -> ids(d.findElements(By.xpath("//nothing"))));
        cases.put("findElement on none",
                d -> d.findElement(By.xpath("//nothing")));
        cases.put("not an element",
                d -> d.findElements(By.xpath("//p[@id='t-br']/text()")));
        cases.forEach((name, find) -> same("xpath " + name,
                b -> find.apply(driverOn(b))));
        assertNoMismatches();
    }

    private static List<String> ids(List<TestBenchElement> elements) {
        return elements.stream().map(TestBenchElement::getId).toList();
    }

    private static TestBenchElement element(BrowserDriver driver, String id) {
        return (TestBenchElement) driver.executeScript(
                "return document.getElementById(arguments[0])", id);
    }

    private static boolean isPng(byte[] png) {
        return png.length > 8 && png[1] == 'P' && png[2] == 'N'
                && png[3] == 'G';
    }

    @Test
    void page() {
        same("title", BrowserBackend::getTitle);
        same("url", BrowserBackend::getCurrentUrl);
        same("source has the title",
                b -> b.getPageSource().contains("<title>parity</title>"));
        same("navigate, back, forward, refresh", b -> {
            b.navigate(url + "?second");
            String second = b.getCurrentUrl();
            b.back();
            String back = b.getCurrentUrl();
            b.forward();
            String forward = b.getCurrentUrl();
            b.executeScript("window.marker = 1");
            b.refresh();
            return List.of(second, back, forward, b.getCurrentUrl(),
                    b.executeScript("return window.marker === undefined"));
        });
        same("window size", b -> {
            Dimension before = b.getWindowSize();
            b.setWindowSize(new Dimension(1000, 700));
            Dimension after = b.getWindowSize();
            // The viewport differs: Selenium sizes the window around it
            Object innerWidth = b.executeScript("return window.innerWidth");
            b.setWindowSize(before);
            return List.of(before.toString(), after.toString(), innerWidth);
        });
        same("page screenshot", b -> isPng(b.getScreenshot()));
        assertNoMismatches();
    }

    @Test
    void browserFacts() throws IOException {
        // Both drive the same Chromium; the remote address and the log
        // differ by design (a Playwright browser has no driver server, and
        // reading its log is not implemented yet)
        same("capabilities", b -> List.of(b.getCapabilities().getBrowserName(),
                String.valueOf(b.getCapabilities().getPlatformName()),
                ReferenceNameGenerator.getMajorVersion(b.getCapabilities())));
        same("session gone", BrowserBackend::isSessionGone);
        same("window position", b -> {
            b.setWindowPosition(new Point(0, 0));
            return b.executeScript("return window.innerWidth");
        });
        // Not in the temp dir: the Chromium snap cannot read /tmp
        Path file = Path.of("target", "parity-upload.txt").toAbsolutePath();
        try {
            Files.writeString(file, "parity");
            same("file input", b -> {
                ElementBackend input = (ElementBackend) b.executeScript(
                        "const i = document.createElement('input');"
                                + "i.type = 'file'; i.multiple = true;"
                                + "document.body.appendChild(i); return i;");
                input.setInputFiles(List.of(file.toFile(), file.toFile()));
                return b.executeScript(
                        "return Array.from(arguments[0].files).map(f => f.name + ':' + f.size)",
                        input);
            });
            same("a path typed into a hidden file input", b -> {
                ElementBackend input = (ElementBackend) b.executeScript(
                        "const i = document.createElement('input');"
                                + "i.type = 'file'; i.hidden = true;"
                                + "document.body.appendChild(i); return i;");
                input.sendKeys(file.toString());
                return b.executeScript(
                        "return Array.from(arguments[0].files).map(f => f.name + ':' + f.size)",
                        input);
            });
        } finally {
            Files.delete(file);
        }
        assertNoMismatches();
    }

    @Test
    void windows() {
        same("a window opened by the page", b -> {
            String first = b.getWindowHandle();
            b.executeScript("window.open(arguments[0] + '?popup', '_blank')",
                    url);
            Set<String> handles = b.getWindowHandles();
            for (int i = 0; i < 50 && handles.size() < 2; i++) {
                sleep(100);
                handles = b.getWindowHandles();
            }
            String other = handles.stream().filter(h -> !h.equals(first))
                    .findFirst().orElseThrow();
            b.switchToWindow(other);
            String popup = b.getCurrentUrl();
            Object popupScript = b.executeScript("return location.search");
            b.close();
            b.switchToWindow(first);
            return List.of(handles.size(),
                    handles.iterator().next().equals(first), popup, popupScript,
                    b.getCurrentUrl(), b.getWindowHandles().size());
        });
        same("switch to no window", b -> {
            b.switchToWindow("no-such-window");
            return null;
        });
        assertNoMismatches();
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private static final String FOCUS_KEYS = "var k = document.getElementById('keys');"
            + " k.value = 'pre'; window.keyLog = []; k.focus();";

    private static final String KEYS_RESULT = "return [document.getElementById('keys').value,"
            + " JSON.stringify(window.keyLog.map(e => e.key + '/' + e.mods)),"
            + " document.activeElement.id]";

    /**
     * Key sequences into the focused element, through {@code Actions}. None
     * holds {@code Keys.NULL}, which {@code Keys.chord} ends with: in
     * {@code Actions} chromedriver sends an "Unidentified" keydown for it,
     * which Playwright cannot.
     */
    @Test
    void keysThroughActions() {
        List<CharSequence[]> sequences = List.of(new CharSequence[] { "abc" },
                new CharSequence[] { Keys.SHIFT, "ab" },
                new CharSequence[] { "ab", Keys.BACK_SPACE, "c" },
                new CharSequence[] { "x", Keys.ARROW_LEFT, "y", Keys.END, "z" },
                new CharSequence[] { "a1", Keys.SHIFT, "2" },
                new CharSequence[] { Keys.NUMPAD1, Keys.ADD, Keys.DECIMAL },
                new CharSequence[] { "tab", Keys.TAB });
        for (CharSequence[] keys : sequences) {
            String what = Arrays.toString(keys).replaceAll("[\\uE000-\\uE05D]",
                    "<key>");
            same("keyboard " + what, b -> {
                b.executeScript(FOCUS_KEYS);
                new Actions(driverOn(b)).sendKeys(keys).perform();
                return b.executeScript(KEYS_RESULT);
            });
        }
        same("keyboard holds a key across calls", b -> {
            b.executeScript(FOCUS_KEYS);
            Actions actions = new Actions(driverOn(b));
            actions.keyDown(Keys.SHIFT).perform();
            actions.sendKeys("a").perform();
            actions.sendKeys("b").perform();
            actions.keyUp(Keys.SHIFT).perform();
            actions.sendKeys("c").perform();
            return b.executeScript(KEYS_RESULT);
        });
        same("keyboard non-ASCII", b -> {
            b.executeScript(FOCUS_KEYS);
            new Actions(driverOn(b)).sendKeys("é漢😀").perform();
            return b.executeScript(
                    "return document.getElementById('keys').value");
        });
        assertNoMismatches();
    }

    @Test
    void actionsKeys() {
        Map<String, Consumer<BrowserDriver>> cases = new LinkedHashMap<>();
        cases.put("sendKeys",
                d -> new Actions(d).sendKeys("ab", Keys.ENTER).perform());
        cases.put("sendKeys with SHIFT presses and releases it",
                d -> new Actions(d).sendKeys(Keys.SHIFT, "ab").perform());
        cases.put("keyDown, sendKeys, keyUp",
                d -> new Actions(d).keyDown(Keys.SHIFT).sendKeys("ab")
                        .keyUp(Keys.SHIFT).sendKeys("c").perform());
        cases.put("a key held across perform()s", d -> {
            new Actions(d).keyDown(Keys.SHIFT).perform();
            new Actions(d).sendKeys("a").perform();
            new Actions(d).keyUp(Keys.SHIFT).perform();
            new Actions(d).sendKeys("b").perform();
        });
        cases.put("select all and replace",
                d -> new Actions(d).keyDown(Keys.CONTROL).sendKeys("a")
                        .keyUp(Keys.CONTROL).sendKeys("x").perform());
        cases.put("sendKeys to an element", d -> new Actions(d)
                .sendKeys(element(d, "area"), "!").perform());
        // chromedriver also fires a keydown "Unidentified" for NULL,
        // Playwright none; it releases nothing on either
        same("Actions NULL", b -> {
            b.executeScript(FOCUS_KEYS);
            BrowserDriver d = driverOn(b);
            new Actions(d).keyDown(Keys.SHIFT).perform();
            new Actions(d).sendKeys(Keys.NULL, "a").perform();
            new Actions(d).keyUp(Keys.SHIFT).perform();
            return b.executeScript(
                    "return document.getElementById('keys').value");
        });
        cases.forEach((name, action) -> same("Actions " + name, b -> {
            b.executeScript(FOCUS_KEYS);
            action.accept(driverOn(b));
            return List.of(b.executeScript(KEYS_RESULT), b.executeScript(
                    "return document.getElementById('area').value"));
        }));
        assertNoMismatches();
    }

    @Test
    void drags() {
        Map<String, Consumer<BrowserDriver>> cases = new LinkedHashMap<>();
        cases.put("Actions dragAndDrop", d -> new Actions(d)
                .dragAndDrop(element(d, "drag"), element(d, "drop")).perform());
        cases.put("Actions dragAndDropBy", d -> new Actions(d)
                .dragAndDropBy(element(d, "drag"), 40, 10).perform());
        cases.put("Actions clickAndHold, moveByOffset, release",
                d -> new Actions(d).clickAndHold(element(d, "drag"))
                        .moveByOffset(20, 0).moveByOffset(20, 10).release()
                        .perform());
        cases.put("Actions doubleClick",
                d -> new Actions(d).doubleClick(element(d, "drag")).perform());
        cases.put("Actions contextClick",
                d -> new Actions(d).contextClick(element(d, "drag")).perform());
        cases.put("Actions Shift-click", d -> new Actions(d).keyDown(Keys.SHIFT)
                .click(element(d, "drag")).keyUp(Keys.SHIFT).perform());
        cases.put("Actions moveToElement with offset, click",
                d -> new Actions(d).moveToElement(element(d, "drag"), 10, 5)
                        .click().perform());
        cases.put("Actions moveToLocation, click",
                d -> new Actions(d).moveToLocation(430, 35).click().perform());
        cases.forEach((name, action) -> same(name, b -> {
            b.executeScript("window.scrollTo(0, 0); window.mouseLog = [];"
                    + " window.moves = 0; getSelection().removeAllRanges();");
            action.accept(driverOn(b));
            return b.executeScript(
                    "return [window.mouseLog.join('; '), window.moves > 0]");
        }));
        assertNoMismatches();
    }

    /** A mouse drag of a draggable element is an HTML drag on both. */
    @Test
    void html5DragAndDrop() {
        same("drag a draggable", b -> {
            b.executeScript("window.scrollTo(0, 0); window.mouseLog = [];");
            BrowserDriver d = driverOn(b);
            new Actions(d).dragAndDrop(element(d, "html5"), element(d, "drop"))
                    .perform();
            return b.executeScript("return window.mouseLog.join('; ')");
        });
        assertNoMismatches();
    }

    @Test
    void stale() {
        for (BrowserBackend b : List.of(selenium, pw)) {
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

    /** Selenium's staleness, through TestBench's own element and query. */
    @Test
    void aReplacedNodeIsStale() {
        for (BrowserBackend b : List.of(selenium, pw)) {
            TestBenchCommandExecutor executor = new TestBenchCommandExecutor(
                    null, null);
            executor.setBackend(b);
            TestBenchElement gone = new ElementQuery<>(TestBenchElement.class,
                    "div").onPage(executor).id("gone");
            b.executeScript("var old = document.getElementById('gone');"
                    + " var fresh = document.createElement('div');"
                    + " fresh.id = 'gone'; fresh.textContent = 'again';"
                    + " old.replaceWith(fresh);");
            assertThrows(StaleElementReferenceException.class, gone::getText,
                    b.getClass().getSimpleName());
            assertThrows(StaleElementReferenceException.class,
                    () -> gone.getPropertyString("textContent"),
                    b.getClass().getSimpleName());
        }
    }

    /**
     * A script after one that navigated runs in the new page, as on Selenium.
     */
    @Test
    void aScriptRightAfterANavigationRunsInTheNewPage() {
        for (int i = 0; i < 5; i++) {
            pw.executeScript("location.reload()");
            assertEquals(1L, pw.executeScript(
                    "return document.querySelectorAll('#keys').length"));
        }
    }

    @Test
    void equality() {
        for (BrowserBackend b : List.of(selenium, pw)) {
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
