// Trivial scenario shared by every load-profiles run execution. Seven
// copies exist (constant/ramp/stress/soak/custom/executor/custom-scenario)
// so each pom execution writes its summary to a distinct file and the
// verify script can inspect per-profile output. Content is intentionally
// identical across files; the load profile is supplied entirely by the
// plugin configuration.
import http from 'k6/http'
import { check, sleep } from 'k6'

export default function () {
  const host = __ENV.APP_IP || 'localhost'
  const port = __ENV.APP_PORT || '8080'
  check(http.get(`http://${host}:${port}/`),
      { 'status 200': (r) => r.status === 200 })
  sleep(0.1)
}

export function handleSummary(data) {
  return __ENV.SUMMARY_FILE
      ? { [__ENV.SUMMARY_FILE]: JSON.stringify(data, null, 2) }
      : {}
}
