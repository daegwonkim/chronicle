import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://chronicle-test-app:8081';

const errorRate = new Rate('errors');
const logLatency = new Trend('log_latency');
const bulkLatency = new Trend('bulk_latency');

export const options = {
  stages: [
    { duration: '30s', target: 200 },
    { duration: '30s', target: 400 },
    { duration: '30s', target: 600 },
    { duration: '30s', target: 800 },
    { duration: '30s', target: 1000 },
    { duration: '1m', target: 2000 },  // 최대 부하 유지
    { duration: '30s', target: 0 }
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95% 요청이 500ms 이내
    errors: ['rate<0.1'],              // 에러율 10% 미만
  },
};

export default function () {
  // POST /test/logs - 모든 레벨 로그 생성
  const logRes = http.post(`${BASE_URL}/test/logs`);
  check(logRes, {
    'logs: status 200': (r) => r.status === 200,
  }) || errorRate.add(1);
  logLatency.add(logRes.timings.duration);

  // POST /test/logs/bulk - 벌크 로그 생성
//  const bulkCount = Math.floor(Math.random() * 91) + 10; // 10~100
//  const bulkRes = http.post(`${BASE_URL}/test/logs/bulk?count=${bulkCount}`);
//  check(bulkRes, {
//    'bulk: status 200': (r) => r.status === 200,
//  }) || errorRate.add(1);
//  bulkLatency.add(bulkRes.timings.duration);
}
