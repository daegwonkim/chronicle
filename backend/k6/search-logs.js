import http from 'k6/http';
import { check } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const API_URL = __ENV.API_URL || 'http://chronicle-api:8080';

const errorRate = new Rate('errors');
const logLatency = new Trend('log_latency');

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

const LOG_LEVELS = ['TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR'];

export default function () {
  const params = {
    appIds: __ENV.APP_IDS || '1',
    logLevel: LOG_LEVELS[Math.floor(Math.random() * LOG_LEVELS.length)],
    query: __ENV.QUERY || '',
    page: 0,
    size: 20,
  };

  const query = Object.entries(params)
    .filter(([, v]) => v !== '')
    .map(([k, v]) => `${k}=${encodeURIComponent(v)}`)
    .join('&');

  const res = http.get(`${API_URL}/v1/api/logs?${query}`);

  check(res, {
    'status 200': (r) => r.status === 200,
  }) || errorRate.add(1);

  logLatency.add(res.timings.duration);
}
