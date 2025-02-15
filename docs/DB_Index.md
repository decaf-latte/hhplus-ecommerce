# DB Index 를 통한 쿼리 성능 개선 

---
## 목차
* [Index 개념 정리](##Index 개념 정리)
  * [Index 란?](###Index-란?)
  * [Index의 장단점](###Index의-장단점)
  * [Index 사용이 적합한 경우](###Index-사용이-적합한-경우)
  * [Index Column의 기준](###Index-Column의-기준)
  * [단일 컬럼 Index vs 복합 컬럼 Index](###단일-컬럼-Index-vs-복합-컬럼-Index)
  * [Index 자료구조](###Index-자료구조)
  * [Index를 이용한 조회 유의사항](###Index를-이용한-조회-유의사항)
  * [Covering Index (커버링 인덱스)](###Covering-Index-(커버링-인덱스))
  * [Index 적용시 CUD 연산에서의 오버헤드가 발생](###Index-적용시-CUD-연산에서의-오버헤드가-발생)
  * [Sync Schedule Strategy](###Sync-Schedule-Strategy)
* [서비스 주요 조회 쿼리 - 쿼리 성능 개선을 위한 인덱스 적용 및 분석](###서비스-주요-조회-쿼리---쿼리-성능-개선을-위한-인덱스-적용-및-분석)

---
## Index 개념 정리

### Index 란?
![index](/docs/img/index01.png)
- 테이블의 검색 속도를 향상시키기 위해 사용하는 자료구조로 데이터와 데이터의 위치를 포함
- 만약 인덱스를 사용하지 않은 컬럼을 조회한다고 하면 Full Scan을 수행해야 하므로 처리 속도가 느려짐

### Index의 장단점 
- 장점
  - 조회 속도 향상: 데이터 검색 시 탐색 범위를 줄여 성능을 향상
  - ORDER BY 성능 개선: 정렬이 필요한 경우 인덱스를 활용하여 빠르게 정렬 가능
  - WHERE 조건 최적화: 조건 검색 시 인덱스를 사용하여 불필요한 데이터 조회를 줄일 수 있음
- 단점
  - CUD(INSERT, UPDATE, DELETE) 성능 저하: 데이터 변경이 발생할 때마다 인덱스를 갱신해야 하므로 오버헤드가 발생
  - 디스크 공간 추가 사용: 인덱스를 저장하기 위한 추가적인 스토리지가 필요
  - 잘못된 인덱스 사용 시 성능 저하: 불필요한 인덱스는 오히려 성능을 저하

### Index 사용이 적합한 경우
- WHERE 절에서 특정 컬럼을 자주 조회할 때 
- ORDER BY, GROUP BY 연산을 자주 사용할 때 
- JOIN 연산 시 조인 키(Column)에서 검색 성능을 높이고 싶을 때 
- 대량의 데이터를 처리하는 테이블에서 특정 조건으로 조회하는 경우 
- 자주 사용하는 컬럼에 대해 DISTINCT(중복 제거)를 수행하는 경우

### Index Column의 기준 : 검색 성능을 극대화하기 위해 다음과 같은 기준을 고려
- 조회가 자주 발생하는 컬럼
  - WHERE, JOIN, ORDER BY에 자주 사용되는 컬럼 
- 데이터의 변경이 적은 컬럼
  - UPDATE, DELETE가 빈번한 컬럼은 인덱스 비용이 높아짐
- 선택도가 높은(카디널리티가 높은) 컬럼
  - 중복된 값이 적은 컬럼이 효과적
- WHERE 절에서 자주 사용되는 컬럼
  - 특히 범위 검색(>, <, BETWEEN)에서 중요

#### 카디널리티란?
- 특정 컬럼에서 **서로 다른 값의 개수(유니크한 값의 개수)** 를 의미
  - 높은 카디널리티(High Cardinality): 값이 다양하게 분포 (예: 주민등록번호, 이메일)
  - 낮은 카디널리티(Low Cardinality): 특정 값이 많이 중복 (예: 성별, 국가 코드)

### 단일 컬럼 Index vs 복합 컬럼 Index
- 단일 컬럼 인덱스 
  - 하나의 컬럼을 기준으로 생성되는 인덱스. 
  - CREATE INDEX idx_name ON users(name); 
  - 장점
    - 특정 컬럼을 검색할 때 빠름
  - 단점
    - 여러 컬럼을 조합한 검색에서는 비효율적 
- 복합 컬럼 인덱스 
  - 여러 개의 컬럼을 조합하여 생성하는 인덱스. 
  - CREATE INDEX idx_name_age ON users(name, age); 
  - 장점
    - 다중 컬럼 검색 성능 향상. 
  - 단점
    - 왼쪽부터 순차적으로 검색해야 최적화됨 
    - (예: WHERE name = 'John' AND age = 25는 최적화되지만, WHERE age = 25만 사용하면 인덱스가 활용되지 않음)

### Index 자료구조
1) **B-Tree 인덱스**
- 대부분의 데이터베이스(MySQL, PostgreSQL, Oracle)에서 기본적으로 사용하는 자료구조
- 균형 트리 구조를 유지하여 검색 속도를 향상
- 정렬된 데이터를 빠르게 탐색 가능
2) **Hash 인덱스**
- Key-Value 형태로 데이터를 저장
- ```=``` 연산에 대해 빠르게 조회 가능 
- 범위 검색(>, <)에서는 비효율적
3) **Bitmap 인덱스**
- 값의 개수가 적은 컬럼(예: 성별, 국가 코드)에 적합
- 비트맵을 사용하여 공간을 절약하고, AND/OR 연산 최적화

### Index를 이용한 조회 유의사항
- **인덱스를 타지 않는 경우**
  - 함수가 적용된 컬럼 (WHERE UPPER(name) = 'JOHN')
  - LIKE '%keyword%' (앞에 %가 붙으면 인덱스 활용 불가)
  - OR 연산을 사용할 경우 인덱스 최적화 어려움. 
- **Index가 필요한 경우**
  - 범위 검색 (>, <, BETWEEN)
  - JOIN 연산 시 조인 키가 WHERE 절에 포함되는 경우 
  - ORDER BY, GROUP BY가 자주 사용될 경우

### Covering Index (커버링 인덱스)
- 쿼리 실행 시 인덱스만으로 데이터를 조회할 수 있는 인덱스
- 즉, 테이블을 조회하지 않고 인덱스만으로 데이터를 반환할 수 있어 성능이 크게 향상

### Index 적용시 CUD 연산에서의 오버헤드가 발생
- INSERT: 새로운 데이터를 추가할 때 인덱스도 함께 갱신해야 하므로 성능 저하 발생 
- UPDATE: 인덱싱된 컬럼을 변경하면 기존 인덱스를 삭제하고 다시 생성해야 하므로 부하 증가
- DELETE: 데이터를 삭제하면 인덱스에서도 제거해야 하므로 추가적인 비용 발생
#### 해결방법
- 필요한 곳에만 인덱스 적용: 불필요한 인덱스를 줄여 오버헤드를 최소화 
- 배치 업데이트 활용: 실시간 인덱스 업데이트를 줄이고 배치로 처리
- 인덱스 재구성(Rebuild) 주기적으로 수행: 필요하지 않은 인덱스는 정리

### Sync Schedule Strategy
- 데이터 변경 시 인덱스를 언제, 어떻게 갱신할지 결정하는 전략
- 즉시 동기화 (Immediate Sync)
  - 데이터가 변경될 때마다 인덱스를 즉시 갱신 
  - 장점: 항상 최신 데이터가 반영됨
  - 단점: 데이터 변경이 많을 경우 성능 부하가 큼 
- 지연 동기화 (Lazy Sync)
  - 변경된 데이터를 바로 인덱스에 반영하지 않고, 일정 주기마다 일괄 갱신
  - 장점: 변경이 빈번한 경우에도 성능 부하가 낮음
  - 단점: 인덱스가 최신 데이터를 반영하지 못할 수도 있음
- 비동기 동기화 (Asynchronous Sync)
  - 트랜잭션과 별도로 비동기 작업을 실행하여 인덱스를 업데이트
  - 장점: 애플리케이션의 응답 속도를 유지하면서도 인덱스를 적절히 갱신 가능
  - 단점: 동기화가 늦어질 경우 최신 데이터와 인덱스 간 불일치 가능
- 배치 업데이트 활용 (Batch Update)
  - 일정 시간마다 변경된 데이터를 모아 한 번에 인덱스를 갱신하는 방식
  - 보통 야간 배치 작업에서 수행되며, **지연 동기화(Lazy Sync)**와 유사한 개념
  - 장점: 실시간으로 인덱스를 갱신할 필요가 없기 때문에 성능 부담이 적음
  - 단점: 배치 주기에 따라 인덱스 데이터가 최신 상태가 아닐 가능성 있음

### 실행 계획 (EXPLAIN)
- 데이터베이스가 쿼리를 실행하는 방식과 성능을 분석하는 도구
- 실행 계획을 통해 인덱스가 잘 활용되는지 확인 가능
- MySQL에서는 EXPLAIN을 사용하여 실행 계획을 확인 가능

``` sql
 EXPLAIN SELECT * FROM users WHERE name = 'John';
```

- 실행 계획 주요 항목

| 필드명          | 설명                                      |
|---------------|-----------------------------------------|
| `id`          | 실행 단계 (Step)                         |
| `select_type` | 쿼리 유형 (SIMPLE, DERIVED 등)          |
| `table`       | 사용된 테이블                           |
| `type`        | 접근 방식 (ALL, INDEX, RANGE, REF 등)   |
| `possible_keys` | 사용 가능한 인덱스                    |
| `key`         | 실제 사용된 인덱스                      |
| `rows`        | 조회 예상 행 수                         |
| `Extra`       | 추가 정보 (Using index, Using where 등) |

- 중요 지표 
  - type : 접근 방법 -> 인덱스를 사용하는지, 테이블을 전체 스캔하는지 등의 정보를 제공 
  - key : 사용된 인덱스 -> 어떤 인덱스를 사용하는지 확인 가능

---
## 서비스 주요 조회 쿼리 - 쿼리 성능 개선을 위한 인덱스 적용 및 분석
- 자주 조회하는 쿼리, 복잡합 쿼리 




**OrderItem**
- findTopOrderItemsLast3Days
  ``` sql
  SELECT new kr.hhplus.be.server.service.order.vo.TopOrderItemVO(oi.product.id, SUM(oi.quantity))
  FROM OrderItem oi
  LEFT JOIN Order o ON oi.order.id = o.id
  WHERE oi.createdAt >= :threeDaysAgo AND o.status = 'COMPLETED'
  GROUP BY oi.product.id
  ORDER BY SUM(oi.quantity) DESC
  ```
  - 최근 3일간 가장 많이 주문된 상품을 조회하는 쿼리로 **복잡한 쿼리**로 인덱스 생성 필요
  - 복합 인덱스 생성 
    ``` sql
    CREATE INDEX idx_orderitem_created_orderid ON order_item (created_at, order_id, product_id);
    CREATE INDEX idx_orderitem_group ON order_item (product_id, created_at, order_id, quantity);
    ```
    - created_at, order_id, product_id
      - created_at으로 먼저 필터링한 후, order_id와 product_id로 정렬
    - product_id, created_at, order_id, quantity
      - GROUP BY로 인한 임시 테이블 사용을 줄이기 위함
      - 커버링 인덱스(Covering Index) 적용 : GROUP BY와 SUM(quantity)를 처리할 때 별도의 정렬 없이 인덱스를 직접 활용 가능

  - 테스트 데이터 건 수 : OrderItem 210만 건 , Order 200만 건
  - 인덱스 적용 전
    - 실행시간 : 936 ms
      - `7 rows retrieved starting from 1 in 957 ms (execution: 936 ms, fetching: 21 ms)`
    - Explain 결과
    ```sql
    -> Sort: `SUM(oi.quantity)` DESC  (actual time=912..912 rows=7 loops=1)
    -> Table scan on <temporary>  (actual time=912..912 rows=7 loops=1)
        -> Aggregate using temporary table  (actual time=912..912 rows=7 loops=1)
            -> Nested loop inner join  (cost=962965 rows=342157) (actual time=0.0684..871 rows=182600 loops=1)
                -> Filter: (oi.created_at >= TIMESTAMP'2025-02-08 00:00:00')  (cost=210382 rows=684314) (actual time=0.0549..523 rows=312995 loops=1)
                    -> Table scan on oi  (cost=210382 rows=2.05e+6) (actual time=0.0533..451 rows=2.1e+6 loops=1)
                -> Filter: (o.`status` = 'COMPLETED')  (cost=1 rows=0.5) (actual time=972e-6..0.00101 rows=0.583 loops=312995)
                    -> Single-row index lookup on o using PRIMARY (id=oi.order_id)  (cost=1 rows=1) (actual time=809e-6..834e-6 rows=1 loops=312995)
    ```
    - 실행계획 분석
      - oi(Order Items) 테이블에서 created_at 기준으로 필터링 : 인덱스가 없어 Full Table Scan 발생
      - oi.order_id = o.id 조건에서 Orders 테이블의 PRIMARY KEY가 활용 > oi.order_id에 인덱스 추가로 조인 최적화
  - 인덱스 적용 후
    - 실행시간 : 537 ms
      - `7 rows retrieved starting from 1 in 549 ms (execution: 537 ms, fetching: 12 ms)`
    - Explain 결과
    ```sql
    -> Sort: `SUM(oi.quantity)` DESC  (actual time=743..743 rows=7 loops=1)
    -> Stream results  (cost=834701 rows=6) (actual time=208..743 rows=7 loops=1)
        -> Group aggregate: sum(oi.quantity)  (cost=834701 rows=6) (actual time=208..742 rows=7 loops=1)
            -> Nested loop inner join  (cost=763480 rows=309099) (actual time=105..735 rows=182600 loops=1)
                -> Filter: (oi.created_at >= TIMESTAMP'2025-02-08 00:00:00')  (cost=213870 rows=618198) (actual time=105..550 rows=312995 loops=1)
                    -> Covering index scan on oi using idx_orderitem_group  (cost=213870 rows=2.05e+6) (actual time=0.141..478 rows=2.1e+6 loops=1)
                -> Filter: (o.`status` = 'COMPLETED')  (cost=0.789 rows=0.5) (actual time=456e-6..496e-6 rows=0.583 loops=312995)
                    -> Single-row index lookup on o using PRIMARY (id=oi.order_id)  (cost=0.789 rows=1) (actual time=297e-6..321e-6 rows=1 loops=312995)

    ```
    - 실행계획 분석
      - 기존에는 `Full Table Scan`을 수행했지만 **Covering Index Scan**을 사용하여 `created_at` 필터링 속도가 개선
      - `"Using temporary table"`이 사라지고 **Stream results 방식**으로 데이터가 처리되어 정렬 비용이 감소
      - `Nested Loop Join`에서 `Orders` 테이블의 `PRIMARY KEY` 활용이 최적화되어 조인 성능이 향상
  - 실행 시간 비교 : **인덱스를 적용하여 쿼리 성능이 향상되었다**
    - 936 ms -> 537 ms : 399ms 단축
    - 실행 시간이 약 42.6% 줄어들어 거의 절반 수준으로 최적화

---

**BalanceHistory**
- findByUserWithLock(User user)
    ``` sql
    SELECT b FROM BalanceHistory b WHERE b.user = :user;
    ```
  - 주문 결제시에 사용자의 잔액을 조회하는 쿼리로 **자주 조회됨**
  - `user`가 외래키로 인덱스 생성이 이미 되어 있어 추가 인덱스 생성 불필요

---

**Order**
- findByUserIdWithLock(Long userId)
  ``` sql
  SELECT o FROM Order o WHERE o.user.id = :userId;
  ```
  - 사용자의 주문 목록을 조회하는 쿼리로 **자주 조회됨**
  - `user_id`이 외래키로 인덱스 생성이 이미 되어 있어 추가 인덱스 생성 불필요

---

**User**
- findById (Long userId)
    ``` sql
    SELECT u FROM User u WHERE u.id = :userId;
    ```
    - 사용자 정보를 조회하는 쿼리로 **자주 조회됨**
  - `id`가 pk 값으로 인덱스 생성이 이미 되어 있어 추가 인덱스 생성 불필요

**Product**
- findById (Long id)
    ``` sql
    SELECT p FROM Product p WHERE p.id = :id;
    ```
  - 상품 정보를 조회하는 쿼리로 **자주 조회됨**
  - `id`가 pk 값으로 인덱스 생성이 이미 되어 있어 추가 인덱스 생성 불필요 



