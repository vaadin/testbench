# Demo Web Application Load Tests

This module provides k6 load testing capabilities for the demo application in the `demo-web-app` module. It creates k6 scripts based on Vaadin TestBench integration tests and executes load tests against the server. It supports two main workflows through Maven profiles:

1. **Local Development** (default in this demo, but not recommended for real projects) - Creates test scripts, starts the web server, and runs tests on the same machine
2. **Remote Load Testing** - Runs tests against a server running elsewhere. This is the recommended approach for real-world use: run load tests on representative hardware and generate traffic from a separate machine

## Quick Start

### Local Development Workflow

Record scenarios and run quick load tests locally:

```bash
# Build and run the full workflow (start server, record, run test)
mvn verify
```

### Remote Load Testing

Run pre-recorded tests against a remote server:

```bash
# Test against a staging server
mvn verify -Premote -Dk6.appHost=staging.example.com -Dk6.appPort=8080

# High-load test against production
mvn verify -Premote -Dk6.appHost=10.0.1.50 -Dk6.vus=100 -Dk6.duration=5m
```

## Maven Profiles

### `local` (Default)

The default profile for development and CI. It:

1. Starts the demo-web-app locally
2. Records TestBench scenarios through a proxy
3. Runs a quick load test to verify the recording
4. Stops the application

```bash
mvn verify                              # Full workflow
mvn verify -Dk6.skipRun=true            # Only record, don't run load test
mvn verify -Dk6.skipRecord=true         # Only run test, don't re-record
```

### `remote`

For production load testing against a server running on another machine. This profile:

- Does NOT start any server
- Validates the remote server is accessible
- Runs pre-recorded k6 tests against the configured target
- Is designed to run from a dedicated load generation machine

```bash
# Basic usage
mvn verify -Premote -Dk6.appHost=staging.example.com

# Full configuration
mvn verify -Premote \
    -Dk6.appHost=192.168.1.100 \
    -Dk6.appPort=8080 \
    -Dk6.vus=50 \
    -Dk6.duration=2m
```

### `record-only`

Records scenarios without running load tests. Useful for preparing tests that will be executed later on dedicated infrastructure.

```bash
mvn verify -Precord-only
```

## Included Scenarios

This demo records and runs two TestBench integration tests as k6 load tests:

| Scenario | Description | Generated Test |
|----------|-------------|----------------|
| `HelloWorldIT` | Simple form interaction: enter name, click button | `hello-world.js` |
| `CrudExampleIT` | Full CRUD workflow: browse grid, create, edit, delete | `crud-example.js` |

## Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `k6.appHost` | `localhost` | Target server hostname/IP (remote profile) |
| `k6.appPort` | `8080` | Target server port (remote profile) |
| `app.port` | `8081` | Local server port (local profile) |
| `proxy.port` | `6000` | Recording proxy port |
| `k6.vus` | `10` | Number of virtual users |
| `k6.duration` | `30s` | Test duration (e.g., "30s", "1m", "5m") |
| `k6.testDir` | `target/k6/tests` | Directory containing k6 test files |
| `k6.skipRecord` | `false` | Skip recording phase |
| `k6.skipRun` | `false` | Skip load test phase |

## Production Load Testing Best Practices

For accurate performance metrics, follow these guidelines:

### 1. Separate Machines

Run the load generator on a different machine than the application server:

```
┌─────────────────┐         ┌─────────────────┐
│  Load Generator │  ───►   │  App Server     │
│  (this module)  │  HTTP   │  (demo-web-app) │
│  mvn -Premote   │         │  java -jar ...  │
└─────────────────┘         └─────────────────┘
```

### 2. Network Considerations

- Use a fast, stable network connection
- Consider network latency in your test results
- For cloud testing, run load generator in same region as app server

### 3. Pre-Record Tests

Record tests locally, then distribute the k6 scripts:

```bash
# On development machine: record the test
mvn verify -Precord-only

# Copy k6/tests/*.js to load generation machine

# On load generation machine: run the test
mvn verify -Premote -Dk6.appHost=app-server.example.com
```

### 4. Scaling Up

Increase load gradually to find breaking points:

```bash
# Start small
mvn verify -Premote -Dk6.appHost=prod -Dk6.vus=10 -Dk6.duration=1m

# Increase load
mvn verify -Premote -Dk6.appHost=prod -Dk6.vus=50 -Dk6.duration=5m

# Stress test
mvn verify -Premote -Dk6.appHost=prod -Dk6.vus=200 -Dk6.duration=10m
```

## Example: CI/CD Integration

### GitHub Actions (Remote Testing)

```yaml
jobs:
  load-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Install k6
        run: |
          sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
          echo "deb https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
          sudo apt-get update
          sudo apt-get install k6

      - name: Run Load Test
        run: |
          mvn verify -pl demo-web-app-loadtest -Premote \
            -Dk6.appHost=${{ secrets.STAGING_HOST }} \
            -Dk6.appPort=8080 \
            -Dk6.vus=50 \
            -Dk6.duration=2m
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

- Check the proxy port (6000) is not in use
- Verify the application is running and accessible
- Ensure Chrome browser is installed (required for TestBench)
