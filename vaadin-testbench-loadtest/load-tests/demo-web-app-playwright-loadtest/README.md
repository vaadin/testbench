# Demo Web Application Playwright Load Tests

This module provides k6 load testing capabilities for the Playwright-based demo application in the `demo-web-app-playwright` module. It creates k6 scripts from Playwright integration tests using HAR recording (no proxy needed) and executes load tests against the server. It supports two main workflows through Maven profiles:

1. **Local Development** (default in this demo, but not recommended for real projects) - Creates test scripts, starts the web server, and runs tests on the same machine
2. **Remote Load Testing** - Runs tests against a server running elsewhere. This is the recommended approach for real-world use: run load tests on representative hardware and generate traffic from a separate machine

## Quick Start

> **Note:** This module is only included in the build when `-DrunPlaywrightLoadTests` is passed.
> It is excluded by default to avoid failures in CI snapshot builds.

### Local Development Workflow

Record scenarios and run quick load tests locally:

```bash
# Build and run the full workflow (start server, record, run test)
mvn verify -DrunPlaywrightLoadTests
```

### Remote Load Testing

Run pre-recorded tests against a remote server:

```bash
# Test against a staging server
mvn verify -DrunPlaywrightLoadTests -Premote -Dk6.appHost=staging.example.com -Dk6.appPort=8080

# High-load test against production
mvn verify -DrunPlaywrightLoadTests -Premote -Dk6.appHost=10.0.1.50 -Dk6.vus=100 -Dk6.duration=5m
```

## Maven Profiles

### `local` (Default)

The default profile for development and CI. It:

1. Starts the demo-web-app-playwright locally
2. Records Playwright scenarios via HAR recording (no proxy needed)
3. Runs a quick load test to verify the recording
4. Stops the application

```bash
mvn verify -DrunPlaywrightLoadTests                              # Full workflow
mvn verify -DrunPlaywrightLoadTests -Dk6.skipRun=true            # Only record, don't run load test
mvn verify -DrunPlaywrightLoadTests -Dk6.skipRecord=true         # Only run test, don't re-record
```

### `remote`

For production load testing against a server running on another machine. This profile:

- Does NOT start any server
- Validates the remote server is accessible
- Runs pre-recorded k6 tests against the configured target
- Is designed to run from a dedicated load generation machine

```bash
# Basic usage
mvn verify -DrunPlaywrightLoadTests -Premote -Dk6.appHost=staging.example.com

# Full configuration
mvn verify -DrunPlaywrightLoadTests -Premote \
    -Dk6.appHost=192.168.1.100 \
    -Dk6.appPort=8080 \
    -Dk6.vus=50 \
    -Dk6.duration=2m
```

### `record-only`

Records scenarios without running load tests. Useful for preparing tests that will be executed later on dedicated infrastructure.

```bash
mvn verify -DrunPlaywrightLoadTests -Precord-only
```

## Included Scenarios

This demo records and runs two Playwright integration tests as k6 load tests:

| Scenario | Description | Generated Test |
|----------|-------------|----------------|
| `HelloWorldPlaywrightIT` | Simple form interaction: enter name, click button | `hello-world-playwright.js` |
| `CrudExamplePlaywrightIT` | Full CRUD workflow: browse grid, create, edit, delete | `crud-example-playwright.js` |

By default, scenarios are combined into a single `combined-scenarios.js` with weighted VU distribution (70% HelloWorld, 30% CrudExample).

## Differences from TestBench Load Tests

This module is the Playwright-based alternative to `demo-web-app-loadtest`. The key differences are:

| Aspect | TestBench (`demo-web-app-loadtest`) | Playwright (this module) |
|--------|--------------------------------------|--------------------------|
| Recording method | HTTP proxy (port 6000) | Playwright HAR recording (no proxy) |
| Test framework | TestBench `@BrowserTest` | Playwright Java API |
| Browser control | WebDriver-based | Playwright API |
| Plugin goal | `record` | `record-playwright` |

## Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `k6.appHost` | `localhost` | Target server hostname/IP (remote profile) |
| `k6.appPort` | `8080` | Target server port (remote profile) |
| `app.port` | `8081` | Local server port (local profile) |
| `management.port` | `8082` | Local management/actuator port (local profile) |
| `k6.vus` | `100` | Number of virtual users |
| `k6.duration` | `30s` | Test duration (e.g., "30s", "1m", "5m") |
| `k6.testDir` | `target/k6/tests` | Directory containing k6 test files |
| `k6.combineScenarios` | `true` | Combine all scenarios into a single weighted test |
| `k6.scenarioWeights` | `helloWorld:70,crudExample:30` | VU distribution across scenarios |
| `k6.collectVaadinMetrics` | `true` | Collect Vaadin-specific metrics via actuator |
| `k6.skipRecord` | `false` | Skip recording phase |
| `k6.skipRun` | `false` | Skip load test phase |

## Production Load Testing Best Practices

For accurate performance metrics, follow these guidelines:

### 1. Separate Machines

Run the load generator on a different machine than the application server:

```
┌─────────────────────────┐         ┌──────────────────────────┐
│  Load Generator         │  ────>  │  App Server              │
│  (this module)          │  HTTP   │  (demo-web-app-playwright)│
│  mvn -Premote           │         │  java -jar ...           │
└─────────────────────────┘         └──────────────────────────┘
```

### 2. Network Considerations

- Use a fast, stable network connection
- Consider network latency in your test results
- For cloud testing, run load generator in same region as app server

### 3. Pre-Record Tests

Record tests locally, then distribute the k6 scripts:

```bash
# On development machine: record the test
mvn verify -DrunPlaywrightLoadTests -Precord-only

# Copy k6/tests/*.js to load generation machine

# On load generation machine: run the test
mvn verify -DrunPlaywrightLoadTests -Premote -Dk6.appHost=app-server.example.com
```

### 4. Scaling Up

Increase load gradually to find breaking points:

```bash
# Start small
mvn verify -DrunPlaywrightLoadTests -Premote -Dk6.appHost=prod -Dk6.vus=10 -Dk6.duration=1m

# Increase load
mvn verify -DrunPlaywrightLoadTests -Premote -Dk6.appHost=prod -Dk6.vus=50 -Dk6.duration=5m

# Stress test
mvn verify -DrunPlaywrightLoadTests -Premote -Dk6.appHost=prod -Dk6.vus=200 -Dk6.duration=10m
```

## Troubleshooting

### Remote server not accessible

```
ERROR: Cannot reach http://staging.example.com:8080
```

- Verify the server is running and accessible
- Check firewall rules allow traffic on the specified port
- Ensure correct hostname/IP and port

### k6 not found

```
k6 is required but not found
```

k6 must be installed on the machine generating the load. For testing your setup locally, you can also install it on your workstation.

Install k6:
- macOS: `brew install k6`
- Linux: See [k6 installation guide](https://grafana.com/docs/k6/latest/get-started/installation/)

### Recording fails

- Verify the application is running and accessible
- Ensure Playwright browsers are installed (`mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"`)
- Check that the app port (8081) is not in use
