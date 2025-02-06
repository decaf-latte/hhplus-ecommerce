package kr.hhplus.be.server.scheduler.product;

import kr.hhplus.be.server.controller.product.application.ProductApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProductSchedulerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ProductScheduler topProductsScheduler;

  @Autowired
  private ProductApplicationService productApplicationService;

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  private static final String CACHE_KEY = "topProducts::top_5_products";

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

  @BeforeEach
  void setup() {
    redisTemplate.delete(CACHE_KEY);
    productApplicationService.getTopProducts();
  }

  @Test
  @DisplayName("인기상품 조회 > 스케줄러 실행 후 Redis 캐싱 확인")
  void testSchedulerStoresDataInRedis() {
    topProductsScheduler.updateTopProductsCache();

    Object cachedValue = cacheManager.getCache("topProducts").get("top_5_products", List.class);
    assertNotNull(cachedValue, "스케줄러 실행 후 Redis에 캐싱된 데이터가 있어야 합니다.");
    System.out.println("Redis에 저장된 데이터: " + cachedValue);

    Long ttl = redisTemplate.getExpire(CACHE_KEY);
    assertNotNull(ttl);
    assertTrue(ttl > 0, "TTL이 설정되어 있어야 합니다.");
  }

  @Test
  @DisplayName("캐싱 vs DB 조회 성능 비교 테스트")
  void testCacheVsDbPerformance() throws Exception {
    // 1. Redis 캐시 초기화
    redisTemplate.delete(CACHE_KEY);

    // 2. DB에서 직접 조회하는 경우의 실행 시간 측정
    long startTimeDb = System.currentTimeMillis();
    mockMvc.perform(get("/api/v1/products/top")
                    .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(5)));
    long dbDuration = System.currentTimeMillis() - startTimeDb;

    System.out.println("DB 조회 실행 시간: " + dbDuration + "ms");

    // 3. Redis에 데이터가 캐싱되었는지 확인
    assertNotNull(redisTemplate.opsForValue().get(CACHE_KEY), "데이터가 캐시에 저장되어 있어야 합니다.");

    // 4. Redis에서 캐싱된 데이터를 가져오는 경우의 실행 시간 측정
    long startTimeCache = System.currentTimeMillis();
    mockMvc.perform(get("/api/v1/products/top")
                    .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(5)));
    long cacheDuration = System.currentTimeMillis() - startTimeCache;

    System.out.println("캐싱된 데이터 조회 실행 시간: " + cacheDuration + "ms");

    // 5. 캐싱이 DB 조회보다 빠른지 검증
    assertTrue(cacheDuration < dbDuration, "캐싱된 데이터 조회가 DB 조회보다 빨라야 합니다!");
  }
}
