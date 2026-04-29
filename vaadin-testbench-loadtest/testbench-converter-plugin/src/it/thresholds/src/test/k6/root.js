// Scenario 1 for the thresholds IT. Hits the demo-server root endpoint at the
// host/port supplied by K6RunMojo via the APP_IP / APP_PORT environment
// variables. Used by combineScenarios mode so the plugin can inject the
// threshold block configured in the IT pom.
import http from 'k6/http'
import { check, sleep } from 'k6'

export default function () {
  const host = __ENV.APP_IP || 'localhost'
  const port = __ENV.APP_PORT || '8080'
  const res = http.get(`http://${host}:${port}/`)
  check(res, { 'root status is 200': (r) => r.status === 200 })
  sleep(0.1)
}
