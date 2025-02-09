package kr.hhplus.be.server.controller.coupon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.scheduler.coupon.CouponScheduler;
import kr.hhplus.be.server.scheduler.coupon.application.CouponSchedulerApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CouponControllerConcurrentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponSchedulerApplicationService couponSchedulerApplicationService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CouponScheduler couponScheduler;

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application")
            .withInitScript("init.sql");

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.4.2"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }

    private static final String COUPON_REQUEST_KEY_PREFIX = "coupon-requests:";
    private static final String COUPON_ISSUED_KEY_PREFIX = "coupon-issued:";
    private static final String COUPON_FAILED_KEY_PREFIX = "coupon-failed:";
    private static final String COUPON_CODE = "DISCOUNT30";

    private final List<Long> userIds = List.of(4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);  // 8명 사용자

    @BeforeEach
    void setup() {
        // 테스트 실행 전 Redis 데이터 초기화
        redisTemplate.delete(COUPON_REQUEST_KEY_PREFIX + COUPON_CODE);
        redisTemplate.delete(COUPON_ISSUED_KEY_PREFIX + COUPON_CODE);
        redisTemplate.delete(COUPON_FAILED_KEY_PREFIX + COUPON_CODE);
    }

    @Test
    @DisplayName("스케줄러 기반 동시성 테스트: 선착순 5개 쿠폰 발급, 나머지 실패 및 알림 전송 검증")
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

        // 스케줄러 실행
        couponScheduler.processCouponRequests();

        // Redis에 저장된 데이터 검증 (성공한 사용자 목록 확인)
        Set<String> issuedUsers = redisTemplate.opsForSet().members(COUPON_ISSUED_KEY_PREFIX + COUPON_CODE);
        assertNotNull(issuedUsers);
        assertEquals(5, issuedUsers.size(), "발급된 사용자 수는 5명이어야 합니다.");

        System.out.println("Redis 발급된 사용자 목록: " + issuedUsers);

        // 1 실패한 사용자 목록 확인
        Set<String> failedUsers = redisTemplate.opsForSet().members(COUPON_FAILED_KEY_PREFIX + COUPON_CODE);
        assertNotNull(failedUsers);
        assertEquals(userIds.size() - 5, failedUsers.size(), "실패한 사용자 수는 3명이어야 합니다.");

        System.out.println("Redis 실패한 사용자 목록: " + failedUsers);

        // 2 쿠폰 발급 실패한 사용자에게 알림 전송 테스트
        couponSchedulerApplicationService.processFailedCouponRequests();

        // 3 알림 전송 후 Redis에서 삭제되었는지 확인
        Set<String> failedUsersAfterNotification = redisTemplate.opsForSet().members(COUPON_FAILED_KEY_PREFIX + COUPON_CODE);
        assertTrue(failedUsersAfterNotification == null || failedUsersAfterNotification.isEmpty(),
            "알림 전송 후 실패 사용자 목록이 삭제되어야 합니다.");

        System.out.println("알림 전송 후 실패한 사용자 목록 (삭제 확인): " + failedUsersAfterNotification);

        // DB 상태 검증 (남아있는 쿠폰 개수 확인)
        long issuedCouponCount = couponRepository.countAvailableCoupons(COUPON_CODE);
        assertEquals(0, issuedCouponCount, "DB에 남아있는 쿠폰 개수는 0이어야 합니다.");

        executorService.shutdown();
    }

}
