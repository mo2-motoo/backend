import http from "k6/http";
import { check, sleep } from "k6";
import { Rate } from "k6/metrics";

// Custom metrics
const errorRate = new Rate("errors");

export const options = {
  stages: [
    { duration: "2m", target: 10 }, // Ramp up to 10 users
    { duration: "5m", target: 10 }, // Stay at 10 users
    { duration: "2m", target: 0 }, // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ["p(95)<500"], // 95% of requests must complete below 500ms
    http_req_failed: ["rate<0.1"], // Error rate must be below 10%
    errors: ["rate<0.1"],
  },
};

const BASE_URL = "http://backend:8080";

export default function () {
  // Test different API endpoints
  const endpoints = [
    "/actuator/health",
    "/actuator/info",
    // Add your actual API endpoints here
    // '/api/users',
    // '/api/transactions',
  ];

  endpoints.forEach((endpoint) => {
    const response = http.get(`${BASE_URL}${endpoint}`);

    check(response, {
      [`${endpoint} status is 200`]: (r) => r.status === 200,
      [`${endpoint} response time < 500ms`]: (r) => r.timings.duration < 500,
    });

    if (response.status !== 200) {
      errorRate.add(1);
    }
  });

  sleep(1);
}
