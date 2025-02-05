package kr.hhplus.be.server.controller.coupon;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import kr.hhplus.be.server.controller.coupon.application.CouponApplicationService;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.scheduler.coupon.CouponScheduler;
import kr.hhplus.be.server.service.coupon.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CouponControllerConcurrentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponApplicationService couponApplicationService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CouponScheduler couponScheduler;

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application")
            .withInitScript("init.sql");

    static {
        MYSQL_CONTAINER.start();
    }

    private static final String COUPON_REQUEST_KEY_PREFIX = "coupon-requests:";
    private static final String COUPON_ISSUED_KEY_PREFIX = "coupon-issued:";
    private static final String COUPON_CODE = "DISCOUNT30";

    private final List<Long> userIds = List.of(4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);  // 8명 사용자

    @BeforeEach
    void setup() {
        // 테스트 실행 전 Redis 데이터 초기화
        redisTemplate.delete(COUPON_REQUEST_KEY_PREFIX + COUPON_CODE);
        redisTemplate.delete(COUPON_ISSUED_KEY_PREFIX + COUPON_CODE);
    }

    @Test
    @DisplayName("스케줄러 기반 동시성 테스트: 선착순 5개 쿠폰 발급, 나머지 실패")
    void testCouponIssueConcurrency() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(userIds.size());
        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        List<Integer> statuses = new CopyOnWriteArrayList<>();  // 동시성 안전한 리스트

        for (Long userId : userIds) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                try {
                    MvcResult result = mockMvc.perform(post("/api/v1/coupons/issue")
                                    .contentType("application/json")
                                    .content(String.format("{\"userId\": %d, \"couponCode\": \"%s\"}", userId, COUPON_CODE)))
                            .andReturn();

                    int statusCode = result.getResponse().getStatus();
                    statuses.add(statusCode);
                    return statusCode;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executorService);

            futures.add(future);
        }

        // 모든 비동기 작업 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

//        // 스케줄러 실행 대기 (최대 6초) → 실제 실행 간격보다 조금 여유있게
//        Thread.sleep(6000);
        couponScheduler.processCouponRequests();

        // 결과 분석
        long successCount = statuses.stream().filter(status -> status == 200).count();
        long failureCount = statuses.stream().filter(status -> status == 422).count();

        System.out.println("성공한 요청 개수: " + successCount);
        System.out.println("실패한 요청 개수: "+ failureCount);

//        assertEquals(5, successCount, "성공한 요청은 5개여야 합니다.");
//        assertEquals(3, failureCount, "실패한 요청은 3개여야 합니다.");

        // Redis에 저장된 데이터 검증
        Set<ZSetOperations.TypedTuple<String>> sortedRequests = redisTemplate.opsForZSet().rangeWithScores(COUPON_REQUEST_KEY_PREFIX + COUPON_CODE, 0, -1);
        Set<String> issuedUsers = redisTemplate.opsForSet().members(COUPON_ISSUED_KEY_PREFIX + COUPON_CODE);

        assertNotNull(sortedRequests);
        assertNotNull(issuedUsers);
        assertEquals(5, issuedUsers.size(), "발급된 사용자 수는 5명이어야 합니다.");

        System.out.println(" Redis 선착순 요청 리스트: "+ sortedRequests);
        System.out.println(" Redis 발급된 사용자 목록: "+ issuedUsers);

        // DB 상태 검증
        long issuedCouponCount = couponRepository.countAvailableCoupons(COUPON_CODE);
        assertEquals(0, issuedCouponCount, "DB에 남아있는 쿠폰 개수는 0이어야 합니다.");

        executorService.shutdown();
    }
}
