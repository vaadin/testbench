// Minimal k6 script used by the run-constant-load integration test.
// Hits the demo-server's root endpoint at the host/port supplied by
// K6RunMojo via the APP_IP / APP_PORT environment variables. Implements
// handleSummary so K6RunMojo's SUMMARY_FILE env var produces a JSON file
// that the plugin then renders into an HTML report.
import http from 'k6/http'
import { check, sleep } from 'k6'

export default function () {
  const host = __ENV.APP_IP || 'localhost'
  const port = __ENV.APP_PORT || '8080'
  const res = http.get(`http://${host}:${port}/`)
  check(res, { 'status is 200': (r) => r.status === 200 })
  sleep(0.1)
}

export function handleSummary(data) {
  const result = {}
  if (__ENV.SUMMARY_FILE) {
    result[__ENV.SUMMARY_FILE] = JSON.stringify(data, null, 2)
  }
  return result
}
