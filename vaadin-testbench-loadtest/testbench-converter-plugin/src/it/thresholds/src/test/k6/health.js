// Scenario 2 for the thresholds IT. Hits the demo-server root with a query
// string so the request is distinguishable in the summary. Two scenarios are
// required because K6RunMojo only invokes K6ScenarioCombiner (which embeds
// the configured threshold block) when more than one test file participates
// in combineScenarios mode.
import http from 'k6/http'
import { check, sleep } from 'k6'

export default function () {
  const host = __ENV.APP_IP || 'localhost'
  const port = __ENV.APP_PORT || '8080'
  const res = http.get(`http://${host}:${port}/?ping=1`)
  check(res, { 'health status is 200': (r) => r.status === 200 })
  sleep(0.1)
}
