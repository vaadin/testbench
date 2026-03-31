# TestBench k6 Recording Support

> **TEMPORARY SOLUTION:** This library is a development-phase workaround. In the future,
> this functionality should be integrated directly into Vaadin TestBench, eliminating
> the need for a separate dependency.

A JUnit 5 extension that enables transparent k6 recording proxy support for Vaadin TestBench tests.

## Overview

This library allows you to use regular Vaadin TestBench tests for k6 load test recordings without requiring special base classes or any code changes. When recording mode is enabled (via system properties), the extension automatically configures the browser to route traffic through a recording proxy.

## Features

- **Non-invasive**: Uses standard JUnit 5 extension auto-detection - no code changes required
- **Backward compatible**: Tests run normally when not in recording mode
- **Configuration via system properties**: No annotations or special classes needed

## Installation

Add the dependency to your test scope:

```xml
<!--
    TEMPORARY: This dependency provides transparent k6 proxy configuration
    for TestBench tests via a JUnit 5 extension. In the future, this functionality
    should be integrated directly into Vaadin TestBench itself.
-->
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>testbench-loadtest-support</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

## Usage

### Writing Tests

Write your TestBench tests as normal. The extension works with any test class that extends `BrowserTestBase` (directly or indirectly) and has a `getViewName()` method:

```java
public class MyScenario extends BrowserTestBase {

    @BrowserTest
    public void userWorkflow() {
        // Your TestBench test code - no special changes needed
        $(TextFieldElement.class).first().setValue("test");
        $(ButtonElement.class).first().click();
    }

    // Required: Tell the extension which view to open
    public String getViewName() {
        return "my-view";
    }
}
```

### Running Tests (Normal Mode)

```bash
mvn test -Dtest=MyScenario
```

Tests run without proxy configuration - standard TestBench behavior.

### Running Tests (Recording Mode)

Enable JUnit auto-detection and set the proxy host:

```bash
mvn test -Dtest=MyScenario \
    -Djunit.jupiter.extensions.autodetection.enabled=true \
    -Dk6.proxy.host=localhost:6000 \
    -Dserver.port=8081
```

The extension will automatically:
1. Close the default WebDriver created by BrowserTestBase
2. Create a new ChromeDriver configured with proxy settings
3. Apply necessary Chrome flags for MITM proxy support
4. Navigate to the test view

## Configuration

All configuration is done via system properties:

| Property | Default | Description |
|----------|---------|-------------|
| `junit.jupiter.extensions.autodetection.enabled` | `false` | Must be `true` to enable the extension |
| `k6.proxy.host` | (none) | Proxy host:port. Recording is enabled only when set |
| `server.host` | `127.0.0.1` | Host where the application is running |
| `server.port` | `8080` | Port where the application is running |

## How It Works

The extension uses JUnit 5's ServiceLoader auto-detection mechanism. When enabled and a proxy is configured, the extension:

1. **Intercepts test startup**: Hooks into JUnit's `BeforeEach` lifecycle
2. **Replaces the driver**: Closes TestBench's auto-started driver
3. **Configures proxy**: Creates a new ChromeDriver with HTTP/HTTPS proxy settings
4. **Applies Chrome flags**:
   - `--ignore-certificate-errors` for MITM proxy
   - `--proxy-bypass-list=<-loopback>` to force localhost through proxy
5. **Navigates to view**: Opens the view URL returned by `getViewName()`

## Integration with testbench-converter-plugin

This library is designed to work seamlessly with the `testbench-converter-plugin` Maven plugin. The plugin automatically enables the extension when recording:

```bash
mvn com.vaadin:testbench-converter-plugin:record -Dk6.testClass=MyScenario
```

## Requirements

- Java 21+
- Vaadin TestBench (with JUnit 6 support)
- Chrome browser with ChromeDriver
- A running k6 recording proxy (e.g., via `testbench-converter-plugin`)

## Future Integration into TestBench

This functionality is planned to be integrated directly into Vaadin TestBench. When that happens:

1. This library will no longer be needed
2. Tests will continue to work without any changes
3. The `testbench-loadtest-support` dependency can simply be removed

The integration would likely be done by:
- Adding proxy configuration support directly to TestBench's driver creation
- Exposing the same system properties for backward compatibility
- Optionally providing a more integrated configuration mechanism
