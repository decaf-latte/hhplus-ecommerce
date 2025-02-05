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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ProductSchedulerTest {

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
  private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
          .withDatabaseName("hhplus")
          .withUsername("application")
          .withPassword("application")
          .withInitScript("init.sql");

  static {
    MYSQL_CONTAINER.start();
  }

  @BeforeEach
  void setup() {
    // Redis 캐시 초기화
    redisTemplate.delete(CACHE_KEY);
    productApplicationService.getTopProducts();
  }

  @Test
  @DisplayName("인기상품 조회 > 스케줄러 실행 후 Redis 캐싱 확인")
  void testSchedulerStoresDataInRedis() {
    // 1 스케줄러 실행
    topProductsScheduler.updateTopProductsCache();

    // 2 Redis에서 데이터 확인
    Object cachedValue = cacheManager.getCache("topProducts").get("top_5_products", List.class);
    assertNotNull(cachedValue, "스케줄러 실행 후 Redis에 캐싱된 데이터가 있어야 합니다.");
    System.out.println("Redis에 저장된 데이터: " + cachedValue);

    // 3 TTL 확인
    Long ttl = redisTemplate.getExpire(CACHE_KEY);
    assertNotNull(ttl);
    assertTrue(ttl > 0, "TTL이 설정되어 있어야 합니다.");
  }
}
