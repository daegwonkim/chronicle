import http from 'k6/http';
import { check } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const API_URL = __ENV.API_URL || 'http://chronicle-api:8080';
const APP_KEY = __ENV.APP_KEY || '0c3ff5ef-be52-4fb5-8494-43c94e004e5f';

const LOG_LEVELS = ['TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR'];

const errorRate = new Rate('errors');
const logLatency = new Trend('log_latency');

const headers = {
  'Content-Type': 'application/json',
  'X-App-Key': APP_KEY,
};

export const options = {
  stages: [
    { duration: '10s', target: 100 },
    { duration: '30s', target: 300 },
    { duration: '30s', target: 500 },
    { duration: '30s', target: 700 },
    { duration: '30s', target: 1000 },
    { duration: '2m', target: 1500 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],
    errors: ['rate<0.1'],
  },
};

function buildPayload() {
  const now = new Date().toISOString();
  const logs = [];
  for (let i = 0; i < 5; i++) {
    logs.push({
      level: LOG_LEVELS[i],
      message: `${LOG_LEVELS[i]} level log message`,
      logger: 'k6-test',
      loggedAt: now,
    });
  }
  return JSON.stringify({ logs });
}

export default function () {
  const payload = buildPayload();
  const res = http.post(`${API_URL}/v1/api/logs`, payload, { headers });

  check(res, {
    'status 200': (r) => r.status === 200,
  }) || errorRate.add(1);

  logLatency.add(res.timings.duration);
}
