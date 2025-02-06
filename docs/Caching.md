## 캐시 분석 보고서

### 캐시란?

- 자주 사용하는 데이터를 더 빠르게 접근할 수 있도록 메모리(RAM) 등에 저장하는 기술

### 캐싱이란?

- 데이터의 원래 소스보다 더 빠르고 효율적으로 액세스 할 수 있도록 임시 데이터 저장소
    - `application level` **메모리 캐시**
        - 애플리케이션의 메모리에 데이터를 저장해두고 요청에 대해 데이터를 빠르게 접근해 변환하여 성능 향상 효과를 얻는 방법
    - `external level` **별도의 캐시 서비스**
        - 별도의 캐시 저장소 또는 이를 담당하는 API 서버를 통해 캐시 환경을 제공하는 방법
          ex) Redis, Nginx 캐시, CDN

### 캐시를 사용하는 이유

- 동일한 데이터에 반복적으로 접근하는 상황이 많을 때 사용하는 것이 효과적
- 잘 변하지 않는 데이터일수록 캐시를 사용하는 것이 효과적
- 데이터에 접근 시 복잡한 로직이 필요한 경우 사용하면 효과적

---

## 캐시 전략 

### Read-Through
![Read-Through](/docs/img/read-through.png)
- 애플리케이션이 데이터를 요청하면 캐시에서 먼저 조회하고 없으면(캐시 미스) DB에서 가져와 캐시에 저장한 후 반환
- 즉 캐시는 DB와 연결되어 있고 데이터 조회가 항상 캐시를 통해 이루어 짐

- 장점
  - 캐시가 자동으로 데이터를 관리하므로 애플리케이션이 캐시 갱신을 직접 신경 쓰지 않아도 됨
  - 자주 조회되는 데이터의 성능 최적화에 적합
- 단점
  - 캐시 미스 발생 시 조회 속도가 느려질 수 있음
  - 실시간 데이터 업데이트가 필요한 경우, 최신 데이터 반영이 어려울 수 있음

### Write-Through
![Write-Through](/docs/img/write-through.png)

- 데이터를 DB에 저장하면서 동시에 캐시에도 저장하는 방식
- 읽기와 쓰기 모두 캐시를 통해 수행
- 장점
  - 캐시와 DB 간의 데이터 일관성이 보장
  - 한 번 저장된 데이터는 항상 캐시에 존재하므로 캐시 미스가 적음
- 단점
  - 쓰기 작업 시 캐시와 DB 모두 갱신해야 하므로 성능 저하 가능
  - 캐시에 불필요한 데이터가 저장될 가능성이 있음

### Cache Aside (Lazy loading)
![Cache Aside](/docs/img/cache-aside.png)

- 데이터 조회 시 먼저 캐시를 확인하고, 캐시에 없으면 DB에서 가져와 캐시에 저장하는 전략
- 장점
  - 불필요한 캐시 쓰기를 방지할 수 있어 성능 최적화 가능
  - 애플리케이션이 캐싱을 직접 제어 가능
- 단점
  - 첫 번째 요청 시 캐시 미스가 발생하여 성능 저하 가능
  - 데이터 일관성 문제

### Write-Back (Write-Behind)
![Write-Back](/docs/img/Write-Back.png)

- 데이터를 먼저 캐시에 저장하고, 일정 시간이 지나거나 특정 조건이 충족되면 DB에 반영
- 쓰기 성능이 중요한 경우 사용
- 장점
  - 쓰기 성능이 매우 빠름
  - 데이터가 자주 변경되는 경우 DB 부하를 줄일 수 있음
- 단점
  - 캐시 장애 발생 시 데이터 유실 가능
  - DB와 일관성이 깨질 위험이 있음


### 정리
| 전략 | 설명 | 장점 | 단점 | 사용 사례 |
|------|------|------|------|----------|
| **Read-Through** | 캐시를 먼저 조회하고 없으면 DB에서 가져와 캐시에 저장 | - 캐시가 자동으로 최신 데이터를 유지<br>- 애플리케이션이 캐시 로직을 직접 관리할 필요 없음 | - 캐시 미스 시 응답 속도 저하<br>- 실시간 업데이트가 어려울 수 있음 | - 제품 상세 정보<br>- 사용자 프로필 조회 |
| **Write-Through** | 데이터를 DB에 저장하면서 동시에 캐시에도 저장 | - 캐시와 DB 간 데이터 일관성 유지<br>- 자주 사용되는 데이터의 캐시 적중률 증가 | - 쓰기 성능이 저하될 수 있음<br>- 불필요한 캐시 저장 가능 | - 사용자 정보 저장<br>- 주문 상태 관리 |
| **Write-Around** | 데이터를 캐시에 저장하지 않고 직접 DB에 저장, 조회 시 Read-Through 방식으로 캐싱 | - 불필요한 캐시 저장을 방지<br>- 캐시 낭비 최소화 | - 첫 조회 시 캐시 미스로 인해 성능 저하<br>- 자주 조회되는 데이터가 아니면 캐싱 효과 미비 | - 변경이 적고 자주 조회되지 않는 데이터 |
| **Write-Back (Write-Behind)** | 데이터를 먼저 캐시에 저장하고, 일정 주기마다 DB로 반영 | - 쓰기 성능이 가장 빠름<br>- 자주 변경되는 데이터에 유리 | - 캐시 장애 발생 시 데이터 유실 가능<br>- DB와의 일관성 유지가 어려울 수 있음 | - 실시간 로그 저장<br>- IoT 센서 데이터 |


## 캐시 스탬피드 현상 
![캐시 스탬피드](/docs/img/cache stamped.png)

- 캐시가 만료되었을 때 동시에 많은 요청이 DB로 몰려 성능이 급격히 저하되는 현상

### 캐시 스탬피드 발생 과정
1. 캐시 만료 → 캐시된 데이터가 삭제됨
2. 다수의 요청이 동시에 캐시를 조회 → 캐시에 데이터가 없음 (캐시 미스 Cache Miss 발생!)
3. 모든 요청이 동시에 DB에 접근 → DB 부하 급증
4. 성능 저하 또는 서버 다운 

### 캐시 스탬피드 방지 전략
1. 캐시 만료 시간을 랜덤하게 설정 (TTL 랜덤화)
   - TTL 값을 고정하지 않고 약간의 랜덤성을 부여하여 동시 만료를 방지
2. 캐시 프리로드 (Preload)
   - 캐시 만료 시간이 되기 전에 미리 캐시를 갱신하는 방법
3. 더블 캐싱(Double Caching)
   - 기존 캐시가 만료되기 전에 새로운 캐시를 미리 준비해 놓음
4. 분산 락 (Mutex Lock)
   - 동시에 여러 요청이 DB를 조회하지 못하도록 "락(Lock)"을 거는 방법

---

## Redis

### Redis란?

- **Remote Dictionary Server**
- 고성능 오픈 소스 **인메모리 키-값 데이터 구조 저장소**이다.
- **데이터베이스, 캐시, 메시지 브로커** 등으로 사용
- 주로 **빠른 데이터 액세스가 필요한 애플리케이션**에서 활용


- **Redis 특징**
    - **인메모리 저장**: 모든 데이터를 메모리에 저장하여 **빠른 읽기와 쓰기 성능**을 제공
    - **다양한 자료 구조**: String, Lists, Sets, Hash, Sorted Set 등 다양한 자료 구조를 지원
    - **영속성(AOF, RDB 지원)**: 데이터를 디스크에 저장하여 **서버 장애 발생 시에도 복구 가능**
    - **싱글 스레드**: 기본적으로 **단일 스레드 이벤트 루프**로 동작하며, **원자적 연산을 보장**하여 데이터의 일관성 유지에 용이
    - **고가용성 및 확장성 지원**: Redis Sentinel을 통한 **고가용성(HA)** 및 Redis Cluster를 통한 **수평적 확장(Sharding)** 가능

### Redis 자료구조

# Redis 자료구조 정리

| 자료구조 | 설명 | 주요 명령어 | 사용 사례 |
|----------|------|-----------|----------|
| **String** | 가장 기본적인 키-값 형태 (문자열, 숫자, JSON 등 저장 가능) | `SET key value`, `GET key`, `INCR key` | - 사용자 세션 관리<br>- 캐시 (JSON, 문자열) |
| **List** | 연결 리스트 형태로 값 저장 (FIFO 또는 LIFO 방식) | `LPUSH key value`, `RPUSH key value`, `LPOP key`, `RPOP key`, `LRANGE key start stop` | - 작업 큐 (Queue)<br>- 채팅 메시지 저장 |
| **Set** | 중복을 허용하지 않는 데이터 집합 | `SADD key value`, `SREM key value`, `SMEMBERS key` | - 태그 저장<br>- 중복 제거 |
| **Sorted Set (ZSet)** | 값과 함께 점수(score)를 저장하여 정렬 가능 | `ZADD key score value`, `ZRANGE key start stop WITHSCORES`, `ZREM key value` | - 리더보드 (랭킹)<br>- 인기 검색어 |
| **Hash** | 필드-값(Field-Value) 쌍을 저장하는 자료구조 | `HSET key field value`, `HGET key field`, `HGETALL key` | - 사용자 프로필<br>- 객체 저장 (JSON 대체) |
| **Bitmap** | 비트 단위로 데이터를 저장하는 구조 (0과 1) | `SETBIT key offset value`, `GETBIT key offset`, `BITCOUNT key` | - 사용자 접속 여부 체크 (출석 체크)<br>- 플래그 저장 |
| **HyperLogLog** | 고유한 데이터 개수를 대략적으로 계산 (메모리 절약) | `PFADD key value`, `PFCOUNT key` | - UV (Unique Visitor) 카운트 |
| **Stream** | 로그 및 실시간 데이터 처리 (Pub/Sub 개선 버전) | `XADD key * field value`, `XREAD key` | - 이벤트 로그 저장<br>- 실시간 데이터 처리 |
| **Geo** | 위치(위도, 경도) 기반 데이터를 저장 및 조회 | `GEOADD key longitude latitude member`, `GEODIST key member1 member2` | - 주변 가게 검색<br>- 위치 기반 서비스 |
| **Pub/Sub** | 실시간 메시징 시스템 (발행-구독 모델) | `PUBLISH channel message`, `SUBSCRIBE channel` | - 실시간 알림<br>- 채팅 시스템 |

**Redis는 다양한 자료구조를 지원하며, 각 자료구조는 특정한 사용 사례에 최적화되어 있음!**



---
## 시나리오에서 캐싱을 적용할 로직

### 상위 상품 통계 조회 데이터

- **로직 설명**
  - 3일간 가장 많은 주문을 한 상품 상위 5개의 통계 데이터를 조회

- **캐싱 적용 이유**
    - 해당 데이터를 참조하기 위해서는 **복잡한 로직**이 필요
    - 해당 데이터는 모든 사용자에게 공통적으로 보여지는 데이터 이므로 다른 데이터보다 상대적으로 **조회가 빈번**하다고 판단
    - 3일간 주문 상위 5개의 통계는 사용자에게 실시간으로 정확하게 보여야 하는 데이터가 아님
        - 캐시 만료 시간만큼 동일한 통계 데이터를 사용자에게 제공해도 무방

- **데이터 조회 세부 로직**

    ```java
    /*
     * 지난 3일간 주문 상품들을 조회하여 가장 많이 주문된 상품 상위 5개를 조회
     * 
     */
    @Query("SELECT new kr.hhplus.be.server.service.order.vo.TopOrderItemVO(oi.product.id, SUM(oi.quantity)) " +
            "FROM OrderItem oi " +
            "LEFT JOIN Order o ON oi.order.id = o.id " +
            "WHERE oi.createdAt >= :threeDaysAgo AND o.status = 'COMPLETED' " +
            "GROUP BY oi.product.id " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<TopOrderItemVO> findTopOrderItemsLast3Days(@Param("threeDaysAgo") LocalDateTime threeDaysAgo, Pageable pageable);
    
    /*
     * 위 조회된 주문 상세 데이터에서 상품 상세 ID 리스트를 가져와 해당 리스트로 상품 정보를 조회
     */
   @Override
  public List<TopProductVO> getTopProducts() {
    List<TopOrderItemVO> topOrderItemVOList = orderItemService.getTopOrderItems();

    return topOrderItemVOList.stream()
        .map(
            orderItem -> {
              ProductVO productVO =
                  productService
                      .getProductByProductIdWithLock(orderItem.getProductId())
                      .map(ProductVO::from)
                      .orElseThrow(() -> new CommerceProductException(ErrorCode.PRODUCT_NOT_EXIST));

              return new TopProductVO(
                  productVO.getId(), productVO.getName(), orderItem.getSalesCount());
            })
        .collect(Collectors.toList());
  }
    ```

- **캐시 TTL (Time To Live) 설정**
    - 설정 시간: 24시간
    - 이유: 과거의 판매내역을 기준으로 상품 집계를 하는 것이므로 매일 00시에 새로운 데이터로 갱신해도 무방하기 때문에 하루를 기준으로 캐시 만료 시간을 설정

### 캐싱 적용 테스트
```java
 // DB에서 직접 조회하는 경우의 실행 시간 측정
    long startTimeDb = System.currentTimeMillis();
    mockMvc.perform(get("/api/v1/products/top")
                    .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(5)));
    long dbDuration = System.currentTimeMillis() - startTimeDb;
    
    System.out.println("DB 조회 실행 시간: " + dbDuration + "ms");

    assertNotNull(redisTemplate.opsForValue().get(CACHE_KEY), "데이터가 캐시에 저장되어 있어야 합니다.");

    // Redis에서 캐싱된 데이터를 가져오는 경우의 실행 시간 측정
    long startTimeCache = System.currentTimeMillis();
    mockMvc.perform(get("/api/v1/products/top")
                    .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(5)));
    long cacheDuration = System.currentTimeMillis() - startTimeCache;

    System.out.println("캐싱된 데이터 조회 실행 시간: " + cacheDuration + "ms");


```
![캐싱 적용 테스트](/docs/img/top5.png)

#### 캐싱 적용 결과
- **DB 조회 실행 시간**: 118ms
- **캐싱된 데이터 조회 실행 시간**: 6ms
- 약 20배 정도 빨라진걸 확인 가능함
