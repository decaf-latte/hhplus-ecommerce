# 동시성 제어 시나리오 분석

## 동시성 제어란?

**동시성 제어(Concurrency Control)** 는 여러 작업(트랜잭션)이 동시에 실행되는 환경에서 데이터의 무결성, 일관성을 보장하고 **경쟁 상태(Race Condition)**
를 방지하기 위한 메커니즘

### 왜 동시성 제어가 필요한가?

현대 시스템에서는 여러 사용자가 동일한 데이터베이스를 동시에 읽거나 수정하는 상황에서 적절한 동시성 제어 없이 작업이 처리되면 다음과 같은 문제가 발생 가능

- Dirty Read
  - 하나의 트랜잭션이 변경한 데이터를 커밋 전에 다른 트랜잭션이 읽는 경우. 
  - 커밋되지 않은 데이터가 롤백되면 일관성이 깨짐. 
- Non-Repeatable Read
  - 하나의 트랜잭션 동안 동일한 데이터를 두 번 읽었을 때, 다른 트랜잭션에 의해 데이터가 수정되어 값이 달라지는 경우. 
- Phantom Read
  - 하나의 트랜잭션 동안 동일한 조건으로 데이터를 조회했을 때, 다른 트랜잭션에 의해 데이터가 삽입 또는 삭제되어 결과가 달라지는 경우. 
- Lost Update
  - 여러 트랜잭션이 동일 데이터를 동시에 수정하면서 마지막 트랜잭션의 결과만 반영되고 나머지는 손실되는 경우.

## 동시성 제어 방식 종류

### Lock 이란?

- 대표적인 동시성 제어 기법 중 하나로 데이터베이스의 일관성과 무결성을 유지하기 위해 **트랜잭션의 순차적 진행을 보장할 수 있는 직렬화 장치**
- 일반적으로 락은 둘 혹은 그 이상의 사용자가 **동시에 같은 데이터를 접근하는 것을 방지** 하기 위해서 사용
- 트랜잭션만으로는 해결할 수 없는 **요청이 유실되는 경우**가 발생하는 **갱신 손실 문제**나 동시에 발생하는 **따닥 요청**등을 해결할 때 사용 
- 즉, **데이터의 일관성과 무결성을 지키기 위해 락을 사용**한다 

### DB Lock

#### Lock의 관점
1. DBMS 내부에서 관리하는 락의 유형 : 데이터베이스 엔진이 내부적으로 데이터를 보호하기 위해 설정
2. 애플리케이션에서 구현한 락의 유형 : 코드로 동시성을 제어하는 방식 

#### DBMS 내부에서 관리하는 락의 유형

- 트랜잭션 락
  - 트랜잭션 단위로 데이터 무결성을 보호하는 락
  - DBMS 내부에서 자동으로 관리되므로 개발자가 명시적으로 설정할 필요가 없음
  1. Row Lock
     - 트랜잭션이 데이터에 접근할 때 데이터베이스 내부에서 자동으로 설정
     - 공유 락 (Shared Lock, S Lock)
       - 데이터를 읽는 동안 다른 트랜잭션이 데이터를 쓰지 못하도록 잠금
     - 배타 락 (Exclusive Lock, X Lock)
       - 데이터를 수정하는 동안 다른 트랜잭션이 데이터를 읽거나 쓰지 못하도록 잠금
  2. Table Lock 
     - 테이블 단위의 대량 작업에서 사용되는 특정 테이블 전체 잠금
  3. Intent Lock
     - 세부적인 락(행 락 등)을 설정하기 전에 테이블 수준에서 예약한 락 
  4. Deadlock
     - DB락의 잘못된 사용으로 인해 발생가능한 교착 상태
     - 트랜잭션 간 자원 획득 순서의 불일치

#### 애플리케이션에서 구현하는 락의 종류
1. 낙관적 락 Optimistic Lock
   - 일단 수정할 데이터 조회 시, 자원에 락을 걸어서 선점하지 말고 커밋할때 **동시성 문제가 발생하면 그때 처리하자는 방법론**
   - 트랜잭션 대부분이 충돌이 발생하지 않아 동시성 문제가 발생하지 않는다고 **낙관적으로 가정하는 방법**
   - 일반적으로 JPA가 제공하는 Version의 의미를 가지고 있는 컬럼을 사용
     - 읽어올 때 Version이랑 수정 후 트랜잭션이 commit 되는 시점의 Version이 다르면 충돌이 일어남
   - 데이터 충돌 가능성을 낮게 가정하고, 애플리케이션 코드에서 @Version 필드 등을 활용해 구현
     - 충돌 처리 로직이 필요
2. 비관적 락 Pessimistic Lock
   - 일단 조회 시, 미리 자원에 Lock을 걸어서 동시성 문제가 발생하지 못하게 미리 처리하자는 방법론
   - 트랜잭션이 시작될 때, 충돌이 발생할 것이라고 비관적으로 보고 수정할 데이터를 조회할 때부터 먼저 락을 거는 방법
   - 데이터를 보호하기 위해 데이터베이스의 내부 잠금 메커니즘(공유락, 배타락)을 활용
     - ````sql
       -- 공유락 예시
       SELECT * FROM products WHERE id = 1 LOCK IN SHARE MODE;
       -- 다른 트랜잭션이 이 행을 읽을 수 있지만, 수정은 불가.
       
       -- 배타락 예시
       SELECT * FROM products WHERE id = 1 FOR UPDATE;
       -- 다른 트랜잭션이 이 행을 읽거나 수정할 수 없음.
   - 주로 SQL 쿼리에 "SELECT FOR UPDATE" 구문을 사용하면서 시작하고 버전 정보는 사용하지 않음
   - DBMS의 락 메커니즘을 사용해 동작하며, 개발자가 SQL 또는 JPA를 활용해 명시적으로 설정
   - 비관적 락 사용시 락을 획득할 때까지 트랜잭션이 대기하는데 무한정 기다릴 수 없으므로 타임아웃 시간을 줄 수 있음

#### **TX 락 vs 비관적 락 vs 낙관적 락**

| **특징**     | **TX 락**                  | **비관적 락**              | **낙관적 락**              |
|------------|---------------------------|------------------------|------------------------|
| **적용 위치**  | DBMS 내부 (Row, Table 등) 관리 | DBMS의 행/테이블 잠금 기능 활용   | 애플리케이션 코드로 구현          |
| **동작 방식**  | 트랜잭션 단위로 데이터 보호           | 작업 전에 데이터에 잠금을 설정      | 데이터 충돌 시점에 충돌 여부 확인    |
| **성능**     | 데이터 충돌 방지, 동시성 저하 가능      | 동시성 저하 가능              | 동시성이 높음, 충돌 발생 시 롤백 필요 |
| **사용 사례**  | DBMS 전반에 자동으로 설정됨         | 재고 관리, 은행 거래 등 충돌 가능 작업 | 충돌 가능성이 낮은 데이터 처리      |
| **구현 난이도** | 쉬움                        | 보통                     | 복잡                     |


### Redis 분산락
- Redis는 단일 스레드로 모든 클라이언트 요청을 처리 => 원자성을 보장 
- 클라이언트 요청은 Redis 내부 큐에 순차적으로 쌓이고, 한 번에 하나씩 처리하여 동시성 제어가 가능

#### Lettuce : 스핀락

- 비동기 이벤트 기반의 Redis 클라이언트 라이브러리로, Netty를 기반으로 설계
- `SETNX`명령어를 사용하여 락을 원자적으로 설정
- lock 획득 실패 시, 일정 시간/횟수 동안 Lock 획득을 재시도
  - 이로인한 네트워크 비용 발생 가능
  - 재시도에 지속적으로 실패할 시, 스레드 점유 등 문제 발생
- 락 관리 로직(Deadlock 방지, TTL 갱신 등)은 직접 처리해야 함
- `spring-data-redis`를 이용하면 기본적으로 제공되므로 구현이 간단함 

#### Redisson : Pub/Sub

- redis pub/sub 구독 기능을 이용해 lock 을 제어
- 락 획득을 실패 했을 시에, “구독” 하고 차례가 될 때까지 이벤트를 기다리는 방식을 이용해 효율적인 Lock 관리가 가능
- “구독” 한 subscriber 들 중 먼저 선점한 작업만 Lock 해제가 가능하므로 안정적으로 원자적 처리가 가능
- 직접 구현, 혹은 라이브러리를 이용할 때 해당 방식의 구현이 달라질 수 있으므로 주의해서 사용해야 함
- `redisson-spring-boot-starter` 의존성을 추가 필요

#### Lettuce vs Redisson

| **특징**          | **Lettuce**                                                                                     | **Redisson**                     |
|-----------------|-------------------------------------------------------------------------------------------------|----------------------------------|
| **사용 난이도**      | 보통 (직접 Redis 명령어로 락 구현)                                                                         | 쉬움 (내장된 락 API 제공)          |
| **Deadlock 방지** | TTL 설정 필요                                                                                       | Watchdog 메커니즘으로 자동 관리    |
| **비동기/리액티브 지원** | 지원                                                                                              | 지원                              |
| **추가 기능**       | 단순한 락 구현에 적합                                                                                    | Fair Lock, Semaphore 등 고급 기능 제공 |
| **성능**          | 가벼움 (필요한 로직만 구현)                                                                                | 무거움 (다양한 기능 제공으로 인한 오버헤드) |


### **선택 기준**

#### **Lettuce**
- Redis 락을 직접 구현하고 관리하고 싶을 때.
- 단순한 락 구현이 필요한 경우.
- 가벼운 라이브러리를 선호하거나 애플리케이션에서 락 관리를 명확히 제어하고 싶을 때.

#### **Redisson**
- 복잡한 락 관리 로직을 간단히 처리하고 싶을 때.
- 추가적인 기능(Fair Lock, CountDown Latch 등)이 필요한 경우.
- Deadlock 방지와 TTL 자동 갱신이 필요한 경우.


---

## 동시성 문제 발생 시나리오 및 해결 방안 고려


### 1. 주문 시 상품 재고 차감 시나리오


```

* 시나리오: 한 상품에 대해 여러 사용자가 동시에 주문 요청을 하게 될 경우

* 예상 이슈: 한 상품에 대해 여러 사용자가 동시에 주문을 요청하면, 재고 차감이 동시 수행되어 재고 부족 상황에서 주문이 잘못 처리될 수 있음

* 해결: DB 락 (비관적 락)

* 이유 : 재고 차감은 단일 트랜잭션 안에서 정확히 처리되어야 하고 데이터베이스에서 재고를 직접 관리하므로, 정확하고 강력한 트랜잭션 보장이 필요하므로 비관적 락을 선택하였다.

```


### 2. 결제 시 사용자 잔액 차감 시나리오


```

시나리오: 같은 결제 요청이 동시에 중복 요청이 발생하는 경우

예상 이슈: 동일한 결제 요청이 중복 발생하여 사용자의 잔액이 중복 차감될 수 있음

해결: DB 락 (비관적 락)

이유: 결제와 같은 금융 거래는 강력한 데이터 무결성을 보장해야 하므로 DB 락을 사용하여 트랜잭션 내에서 중복 처리를 방지

```


### 3. 사용자 잔액 충전 시나리오


```

시나리오: 같은 잔액 충전 요청이 동시에 중복 요청이 발생하는 경우

예상 이슈: 동일한 충전 요청이 중복 발생하여 잔액이 의도한 금액보다 더 많이 충전되는 문제가 발생

해결: DB 락 (비관적 락)

이유: 잔액 충전도 데이터 무결성을 보장해야 하며, DB 락을 통해 정확하게 처리 가능

```

### 4. 선착순 쿠폰 발급 시나리오

```

시나리오: 선착순 쿠폰 발급 시 여러 사용자가 동시에 발급 요청을 하는 경우

예상 이슈: 동일한 쿠폰에 대해 여러 사용자가 중복 요청을 보낼 경우, 쿠폰 재고보다 더 많은 쿠폰이 발급될 수 있음

해결: DB 락 (비관적 락)

이유: 데이터베이스에서 트랜잭션 단위로 관리하여 쿠폰 재고 초과 발급 방지

```
---

## 시나리오별 제어 방식 비교
- 구현은 1회만 작성

### 1. 주문 시 상품 재고 차감 시나리오

#### (1) DB락 - 낙관적 락

- 구현
```java
@Entity
public class Product {

    ...

    @Version
    private Integer version; // 버전 필드 추가
}

```
- 데이터 조회시 @Version 필드값을 함께 조회

```java

public interface ProductService {
    
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Product> getProductByProductId (long id);

}
```
- version 컬럼은 JPA가 자동으로 관리하며, 데이터 수정 시마다 값이 증가

- 복잡성
  - JPA의 @Lock 어노테이션을 사용하여 간단하게 락을 걸 수 있었기에, 복잡성은 굉장히 낮다
  
- 성능

  ![성능1](/docs/img/1.png)
  ![성능2](/docs/img/2.png)


#### (2) DB락 - 비관적 락
- 구현
```java

public interface ProductService {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> getProductByProductId (long id);

}
```
- 복잡성
  - JPA의 @Lock 어노테이션을 사용하여 간단하게 락을 걸 수 있었기에, 복잡성은 굉장히 낮다
- 성능

  ![성능3](/docs/img/3.png)
  ![성능4](/docs/img/4.png)

### 2. 결제 시 사용자 잔액 차감 시나리오

#### (1) DB락 - 낙관적 락

- 성능

  ![성능9](/docs/img/9.png)
  ![성능10](/docs/img/10.png)

#### (2) DB락 - 비관적 락

- 성능

  ![성능11](/docs/img/11.png)
  ![성능12](/docs/img/12.png)

### 3. 사용자 잔액 충전 시나리오

#### (1) DB락 - 낙관적 락

- 성능

  ![성능13](/docs/img/13.png)
  ![성능14](/docs/img/14.png)

#### (2) DB락 - 비관적 락

- 성능

  ![성능15](/docs/img/15.png)
  ![성능16](/docs/img/16.png)


### 4. 선착순 쿠폰 발급 시나리오

#### (1) DB락 - 낙관적 락

- 성능

  ![성능5](/docs/img/5.png)
  ![성능6](/docs/img/6.png)


#### (2) DB락 - 비관적 락

- 성능

  ![성능7](/docs/img/7.png)
  ![성능8](/docs/img/8.png)



