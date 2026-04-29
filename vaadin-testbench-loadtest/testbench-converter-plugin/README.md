# TestBench k6 Converter Maven Plugin

A Maven plugin that converts Vaadin TestBench tests to k6 load tests. It automates the process of recording HTTP traffic through a proxy, converting HAR files to k6 scripts, and refactoring them for Vaadin-specific session handling.

## Prerequisites

- **Java 21+** - For running Maven and the plugin
- **k6** - For running load tests (only needed for `k6:run` goal)

## Installation

The plugin is built as part of the parent project:

```bash
mvn install -pl testbench-converter-plugin
```

## Goals

| Goal | Description |
|------|-------------|
| `k6:convert` | Convert a HAR file to a k6 load test |
| `k6:record` | Record a TestBench test and convert to k6 |
| `k6:run` | Run a k6 load test |
| `k6:help` | Display help information |

## Usage

### k6:convert - Convert HAR to k6

Converts an existing HAR file to a Vaadin-compatible k6 test.

```bash
mvn k6:convert -Dk6.harFile=recording.har
```

**Parameters:**

| Parameter | Default | Description |
|-----------|---------|-------------|
| `k6.harFile` | (required) | Path to the HAR file to convert |
| `k6.outputDir` | `${project.build.directory}/k6/tests` | Output directory for k6 tests |
| `k6.outputName` | (derived from HAR file) | Output file base name |
| `k6.skipFilter` | `false` | Skip filtering external domains |
| `k6.skipRefactor` | `false` | Skip Vaadin-specific refactoring |

### k6:record - Record TestBench Test

Records a TestBench test through a proxy and converts it to k6.

```bash
mvn k6:record -Dk6.testClass=HelloWorldIT -Dk6.appPort=8080
```

**Parameters:**

| Parameter | Default | Description |
|-----------|---------|-------------|
| `k6.testClass` | (required) | TestBench test class to record |
| `k6.proxyPort` | `6000` | Port for the recording proxy |
| `k6.appPort` | `8080` | Port where the application is running |
| `k6.testWorkDir` | `${project.basedir}` | Working directory for Maven test execution |
| `k6.harDir` | `${project.build.directory}` | Directory for HAR recordings |
| `k6.outputDir` | `${project.build.directory}/k6/tests` | Output directory for k6 tests |
| `k6.testTimeout` | `300` | Timeout for test execution (seconds) |
| `k6.thinkTime.enabled` | `true` | Enable realistic think time delays |
| `k6.thinkTime.pageReadDelay` | `2.0` | Base delay (seconds) after page load |
| `k6.thinkTime.interactionDelay` | `0.5` | Base delay (seconds) after user interaction |

### k6:run - Run k6 Load Test

Executes a k6 test file with configurable parameters.

```bash
mvn k6:run -Dk6.testFile=target/k6/tests/hello-world.js -Dk6.vus=50 -Dk6.duration=1m
```

**Parameters:**

| Parameter | Default | Description |
|-----------|---------|-------------|
| `k6.testFile` | (required) | Path to the k6 test file |
| `k6.vus` | `10` | Number of virtual users |
| `k6.duration` | `30s` | Test duration (e.g., "30s", "1m", "5m") |
| `k6.appIp` | `localhost` | Application IP address |
| `k6.appPort` | `8080` | Application port |
| `k6.failOnThreshold` | `true` | Fail build if k6 thresholds are breached |

## POM Configuration

Add the plugin to your project:

```xml
<plugin>
    <groupId>com.vaadin</groupId>
    <artifactId>testbench-converter-plugin</artifactId>
    <version>10.2-SNAPSHOT</version>
    <executions>
        <execution>
            <id>record-scenario</id>
            <phase>integration-test</phase>
            <goals>
                <goal>record</goal>
            </goals>
            <configuration>
                <testClass>HelloWorldIT</testClass>
                <appPort>8081</appPort>
            </configuration>
        </execution>
        <execution>
            <id>run-load-test</id>
            <phase>integration-test</phase>
            <goals>
                <goal>run</goal>
            </goals>
            <configuration>
                <testFile>${project.build.directory}/k6/tests/hello-world.js</testFile>
                <virtualUsers>50</virtualUsers>
                <duration>1m</duration>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## How It Works

The plugin uses pure Java utilities (no Node.js required):

1. **Proxy Recording** (`ProxyRecorder.java`) - BrowserMob Proxy-based MITM proxy that captures traffic as HAR
2. **HAR Filtering** (`HarFilter.java`) - Removes external domain requests (Google, analytics, etc.)
3. **HAR to k6** (`HarToK6Converter.java`) - Converts HAR to k6 script
4. **Vaadin Refactoring** (`K6TestRefactorer.java`) - Adds dynamic session handling:
   - Extracts JSESSIONID from responses
   - Extracts CSRF token, UI ID, and Push ID
   - Replaces hardcoded IPs with configurable variables

## Generated Test Features

The refactored k6 tests include:

- **Dynamic session handling** - Extracts session IDs from responses
- **Configurable target** - Use `-e APP_IP=host -e APP_PORT=port` to test different servers
- **Vaadin helper imports** - Uses shared utility functions for Vaadin protocol handling (`vaadin-k6-helpers.js`)

## Integration Tests

Self-contained Maven Invoker projects under `src/it/` exercise each Mojo end-to-end against a real Vaadin Flow fixture. `mvn verify` runs them. Skip with `-DskipTests`.

**Prerequisites:** Java 21+, Maven, and Chrome installed (used by the recording ITs).

Run the full suite:

```bash
mvn verify
```

Run a single IT (or a comma-separated list):

```bash
mvn invoker:run -Dinvoker.test=convert-har
mvn invoker:run -Dinvoker.test=demo-server,record-testbench
```

The ITs install the plugin to `target/local-repo` and clone each project under `target/its/`. Build logs are at `target/its/<name>/build.log`.