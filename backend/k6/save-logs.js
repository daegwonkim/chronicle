import http from 'k6/http';
import { check } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const API_URL = __ENV.API_URL || 'http://chronicle-api:8080';
const API_KEY = __ENV.API_KEY || '';

const LOG_LEVELS = ['TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR'];

const errorRate = new Rate('errors');
const logLatency = new Trend('log_latency');

const headers = {
  'Content-Type': 'application/json',
  'X-Api-Key': API_KEY,
};

export const options = {
  stages: [
    { duration: '2m', target: 150 },   // 워밍업
    { duration: '3m', target: 350 },   // 평균 트래픽
    { duration: '5m', target: 650 },   // 피크 트래픽
    { duration: '5m', target: 650 },   // 5분간 유지
    { duration: '2m', target: 0 },     // 쿨다운
  ],
  thresholds: {
    http_req_duration: ['p(95)<472'],
    http_req_failed: ['rate<0.01'],
  },
};

function buildPayload() {
  const now = new Date().toISOString();
  const appName = __ENV.APP_NAME || 'k6-test-app';
  const logs = [];
  for (let i = 0; i < 5; i++) {
    logs.push({
      level: LOG_LEVELS[i],
      message: `${LOG_LEVELS[i]} level log message`,
      logger: 'k6-test',
      loggedAt: now,
    });
  }
  return JSON.stringify({ appName, logs });
}

export default function () {
  const payload = buildPayload();
  const res = http.post(`${API_URL}/v1/api/logs`, payload, { headers });

  check(res, {
    'status 200': (r) => r.status === 200,
  }) || errorRate.add(1);

  logLatency.add(res.timings.duration);
}
