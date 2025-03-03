import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080/api/v1/coupons/issue';
const DURATION = '5m'; // 테스트 실행 시간
const RAMP_UP_TIME = '1m'; // 부하 증가 시간
const RAMP_DOWN_TIME = '30s'; // 부하 감소 시간

// 요청 바디 생성 함수 (랜덤 사용자 ID와 쿠폰 코드)
function generatePayload() {
    return JSON.stringify({
        userId: Math.floor(Math.random() * 100000), // 임의의 사용자 ID
        couponCode: 'COUPON2025' // 테스트용 쿠폰 코드
    });
}

export const options = {
    scenarios: {
        // 정상 트래픽 시나리오 (1초에 5건 요청)
        normal_load: {
            executor: 'constant-arrival-rate',
            rate: 5,
            timeUnit: '1s',
            duration: DURATION,
            preAllocatedVUs: 10,
        },
        // 피크 트래픽 시나리오 (1초에 1000건 이상 요청)
        peak_load: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 50,
            stages: [
                { target: 1000, duration: RAMP_UP_TIME }, // 1분 동안 초당 1000건으로 증가
                { target: 1000, duration: '2m' }, // 2분 동안 유지
                { target: 10, duration: RAMP_DOWN_TIME } // 30초 동안 감소
            ],
        },
        // 부하 및 스트레스 테스트 시나리오
        stress_test: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 50,
            stages: [
                { target: 10, duration: '30s' },
                { target: 100, duration: '1m' },
                { target: 500, duration: '1m' },
                { target: 1000, duration: '1m' },
                { target: 10, duration: '30s' }
            ],
        }
    }
};

// API 요청 함수
export default function () {
    const headers = { 'Content-Type': 'application/json' };
    const res = http.post(BASE_URL, generatePayload(), { headers });

    check(res, {
        'is status 200': (r) => r.status === 200,
    });

    sleep(1);
}
