import http from 'k6/http';
import { check } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const API_URL = __ENV.API_URL || 'http://chronicle-api:8080';

const errorRate = new Rate('errors');
const logLatency = new Trend('log_latency');

export const options = {
  stages: [
    { duration: '1m', target: 150 },
    { duration: '2m', target: 350 },
    { duration: '3m', target: 650 },
    { duration: '2m', target: 1300 },  // 스트레스 테스트
    { duration: '1m', target: 1300 },
    { duration: '2m', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<472'],
    http_req_failed: ['rate<0.01'],
  },
};

const LOG_LEVELS = ['TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR'];

export default function () {
  const appIds = (__ENV.APP_IDS || '1').split(',');
  const queryParts = [
    ...appIds.map((id) => `appIds=${encodeURIComponent(id.trim())}`),
    ...LOG_LEVELS.map((level) => `logLevels=${level}`),
    `size=20`,
  ];

  const queryVal = __ENV.QUERY || '';
  if (queryVal !== '') {
    queryParts.push(`query=${encodeURIComponent(queryVal)}`);
  }

  const query = queryParts.join('&');

  // 첫 페이지 요청
  const res = http.get(`${API_URL}/v1/api/logs?${query}`);

  const ok = check(res, {
    'status 200': (r) => r.status === 200,
  });
  if (!ok) {
    errorRate.add(1);
    return;
  }
  logLatency.add(res.timings.duration);

  // 다음 페이지 요청 (커서 기반)
  const body = JSON.parse(res.body);
  if (body.hasNext && body.logs.length > 0) {
    const lastLog = body.logs[body.logs.length - 1];
    const nextRes = http.get(`${API_URL}/v1/api/logs?${query}&cursorId=${lastLog.id}`);

    check(nextRes, {
      'next page status 200': (r) => r.status === 200,
    }) || errorRate.add(1);

    logLatency.add(nextRes.timings.duration);
  }
}
