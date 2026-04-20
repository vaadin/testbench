# Vaadin LoadTestKit k6 - record real browser interactions and replay them at scale

This is a PoC of a tool that utilizes Vaadin TestBench E2E tests as "user stories" for k6 based load testing. Technically the tooling starts a local instance of your server, records the web traffic of E2E test cases (you can select n+1 from your app), converts them into k6 scripts (taking details of Vaadin client-server communications, such as csrf preventation mechanism into account) and provides an easy way to then generate k6 load test against your test server (doesn't have to, and for meaningful numbers, shouldn't be the same server.)


## Project Structure

```
k6-testbench-recorder/
├── testbench-converter-plugin/     # Maven plugin for k6 recording and conversion
├── testbench-loadtest-support/     # A JUnit 5 extension
├── loadtest-helper/                # Drop-in helper for Vaadin load testing
└───── load-tests/                  # Test module
   ├── demo-web-app/                # Sample Vaadin application with TestBench tests
   └── demo-web-app-loadtest/       # Integration test module demonstrating the workflow
   ├── demo-web-app-playwright/                # Sample Vaadin application with Playwright tests
   └── demo-web-app-playwright-loadtest/       # Integration test module demonstrating the workflow for playwright
```

## Prerequisites

| Tool | Version | Installation |
|------|---------|--------------|
| Java | 21+ | [Download](https://adoptium.net/) |
| Maven | 3.9+ | [Download](https://maven.apache.org/) |
| k6 | latest | `brew install k6` (macOS) or [Download](https://grafana.com/docs/k6/) |
| Chrome | latest | [Download](https://www.google.com/chrome/) |

## Quick Start

The modules are not pushed to Maven central yet, so make a clean install first:

```bash
mvn install
```

Builds both the tooling and a simple demo web app with two Vaadin TestBench E2E tests.

> **Note:** The `demo-web-app` and `demo-web-app-loadtest` modules are **not** included
> in the default build. To include them, pass `-DrunLoadTests`:
>
> ```bash
> mvn install -DrunLoadTests
> ```

### Run the Demo (Local)

*Note, you should not do this for anything else but to test the setup without external server*

```bash

# Run the complete workflow (start app, record, run load test)
mvn verify -pl demo-web-app-loadtest -DrunLoadTests
```

### Option 3: Remote Load Testing

Run pre-recorded tests against a server running on another machine:

First deploy the test app to a remote server. The next snippet assumes the remote server is staging.example.com (replace to yours):

```bash
# Test against a staging server
mvn verify -pl demo-web-app-loadtest -DrunLoadTests -Premote \
    -Dk6.appHost=staging.example.com \
    -Dk6.appPort=8080 \
    -Dk6.vus=100 \
    -Dk6.duration=5m
```

This now re-builds the k6 tests against local server deployment if source tests (TestBench) have changed, and then executes load test with k6 against the defined appHost. 

### Using the plugin "manually"

The `testbench-converter-plugin` plugin provides three goals, these are used in the loadtest module, but can be in theory used standalone as well.

```bash
# Convert an existing HAR file to k6
mvn k6:convert -Dk6.harFile=recording.har

# Record a TestBench test and convert to k6
mvn k6:record -Dk6.testClass=HelloWorldIT

# Run a k6 load test
mvn k6:run -Dk6.testFile=k6/tests/hello-world.js -Dk6.vus=50 -Dk6.duration=1m
```

### JBang App (Alternative to Maven Plugin)

For quick experimentation or when you don't want to use Maven for load testing orchestration, there's a standalone JBang app that provides the same recording functionality. 

Check [jbang/README.md](jbang/README.md) for documentation & demo walkthrough.

The JBang app also serves as a **reference implementation** for creating your own custom tooling.

## Creating TestBench Tests

Standard TestBench integration tests define user workflows. The same that you use
already to ensure the functionality doesn't break. If you update the app and/or
your test pattern, your load tests are automatically updated 🥳

In practice you probably want to select certain case from your apps E2E test battery, or craft a special case(s) re-using some page-object classes of your E2E
tests.

The k6:record goal runs these through a proxy to capture HTTP traffic for load testing. This is automated in the demo-web-app-loadtest module.

*In the current PoC, we need a slight hacks to the superclass, but final version of the project should require no special things in your app (we'll find a workaround or add support to TestBench superclass).*

### Example Test

```java
public class HelloWorldIT extends AbstractIT {

    @BrowserTest
    public void helloWorldWorkflow() {
        // Enter name in text field
        TextFieldElement nameField = $(TextFieldElement.class).first();
        nameField.setValue("Test User");

        // Click button
        $(ButtonElement.class).first().click();

        // Verify result
        $(NotificationElement.class).waitForFirst();
    }

    @Override
    public String getViewName() {
        return "";  // Root path
    }
}
```

## Running k6 Tests

Running k6 tests is automated in the demo-web-app-loadtest module and you can
tune parameter via Maven build file. The Maven example also displays a bit for
server health metrics after the execution, collected using Spring Boot Actuator.
Below you can see an example output:




The k6 scripts are also available in target directory if you want to execute them manually.

### Basic Execution

```bash
k6 run k6/tests/hello-world.js
```

### Load Testing

```bash
# 50 virtual users for 30 seconds
k6 run --vus 50 --duration 30s k6/tests/hello-world.js

# Against a different server
k6 run -e APP_IP=192.168.1.100 -e APP_PORT=8080 k6/tests/hello-world.js
```

### Understanding k6 Output

```
          /\      |‾‾| /‾‾/   /‾‾/
     /\  /  \     |  |/  /   /  /
    /  \/    \    |     (   /   ‾‾\
   /          \   |  |\  \ |  (‾)  |
  / __________ \  |__| \__\ \_____/

     scenarios: (100.00%) 1 scenario, 50 max VUs, 1m30s max duration

     ✓ page load status equals 200
     ✓ vaadin init status equals 200

     http_req_duration..............: avg=45.23ms  min=12.34ms  max=234.56ms
     http_req_failed................: 0.00%  ✓ 0   ✗ 1234
     http_reqs......................: 1234   41.13/s
```

Key metrics:
- **http_req_duration**: Response time (avg, min, max)
- **http_req_failed**: Percentage of failed requests
- **http_reqs**: Total requests and throughput

## Module Documentation

- [testbench-converter-plugin](testbench-converter-plugin/README.md) - Maven plugin documentation
- [demo-web-app](demo-web-app/README.md) - Sample application and scenarios
- [demo-web-app-loadtest](demo-web-app-loadtest/README.md) - Integration test workflow

## How It Works

The plugin uses pure Java utilities (no Node.js required) to:

1. **Record** - BrowserMob Proxy captures browser traffic as HAR
2. **Filter** - Removes external requests (Google, analytics, etc.)
3. **Convert** - Generates k6 script from HAR
4. **Refactor** - Adds Vaadin-specific session handling:
   - Dynamic JSESSIONID extraction
   - CSRF token handling
   - UI ID and Push ID management
   - Configurable target server
   - Realistic think time between user actions

## Realistic User Simulation

By default, the generated k6 scripts include realistic "think time" delays to simulate actual user behavior:

- **Page read delay**: 2-5 seconds after page loads (user reading the page)
- **Interaction delay**: 0.5-2 seconds between user actions (thinking time)

### How It Works

The plugin intelligently analyzes HAR content and timing to identify user actions:

1. **User action detection**: Analyzes UIDL request content to detect user interactions:
   - Click events (button clicks, selections)
   - Text input events (typing in fields)
2. **Page load detection**: Identifies v-r=init requests followed by resource loading as "page load" sequences
3. **Smart delay placement**:
   - After page load completes: page read delay (user reading the page)
   - After each user action: interaction delay (user thinking before next action)
4. **HAR timing awareness**: If the recorded HAR already has large gaps (> 500ms) - for example from `Thread.sleep()` or TestBench wait methods - no additional delay is added for that action

### Configuration

Configure think times via Maven properties:

```xml
<configuration>
    <!-- Enable/disable think times (default: true) -->
    <thinkTimeEnabled>true</thinkTimeEnabled>

    <!-- Base delay after page load in seconds (default: 2.0) -->
    <!-- Actual: baseDelay + random(0, baseDelay * 1.5) -->
    <pageReadDelay>2.0</pageReadDelay>

    <!-- Base delay after user interaction in seconds (default: 0.5) -->
    <!-- Actual: baseDelay + random(0, baseDelay * 3) -->
    <interactionDelay>0.5</interactionDelay>
</configuration>
```

Or via command line:

```bash
# Disable think times for maximum throughput testing
mvn k6:record -Dk6.thinkTime.enabled=false

# Custom delays
mvn k6:record -Dk6.thinkTime.pageReadDelay=3.0 -Dk6.thinkTime.interactionDelay=1.0
```

### When to Disable Think Times

Disable think times (`-Dk6.thinkTime.enabled=false`) when:
- **Maximum throughput testing**: You want to stress test the server at maximum request rate
- **TestBench tests with realistic sleeps**: If your TestBench tests already include realistic pauses using `Thread.sleep()` or TestBench wait methods, the HAR recording captures these delays. The plugin respects existing delays, but you may want to disable additional think times entirely

## Useful Links

- [Vaadin TestBench Documentation](https://vaadin.com/docs/latest/flow/testing)
- [k6 Documentation](https://grafana.com/docs/k6/latest/)
