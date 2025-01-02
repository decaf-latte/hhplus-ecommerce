# ERD

![erd](./img/erd.png)

> [참고사항]
> - 주문/결제 API 요구사항 중  "사용자 식별자와 (상품 ID, 수량) **목록**을 입력받아 주문하고 결제를 수행하는 API 를 작성"해야 하므로 Cart 및 CartItem 을 임의로 추가함.

## 1. 테이블 간의 관계

### User와 BalanceHistory (1:N 관계)
- 한 사용자는 여러 잔액 변동 이력을 가질 수 있습니다.
- `user_id`를 통해 `BalanceHistory`에서 특정 사용자의 잔액 변동 내역을 추적할 수 있습니다.

### User와 Order (1:N 관계)
- 한 사용자는 여러 주문을 생성할 수 있습니다.
- `user_id`를 통해 `Order` 테이블에서 사용자의 주문 내역을 확인할 수 있습니다.

### Order와 OrderItem (1:N 관계)
- 한 주문에 여러 상품(`OrderItem`)이 포함될 수 있습니다.
- `order_id`를 통해 특정 주문에 포함된 상품 항목을 추적할 수 있습니다.

### Order와 Payment (1:1 관계)
- 한 주문은 하나의 결제 정보와 연결됩니다.
- `order_id`를 통해 해당 주문의 결제 상태와 금액을 확인할 수 있습니다.

### User와 UserCoupon (1:N 관계)
- 한 사용자는 여러 쿠폰(`UserCoupon`)을 보유할 수 있습니다.
- `user_id`를 통해 특정 사용자가 보유한 쿠폰 정보를 관리합니다.

### Coupon과 UserCoupon (1:N 관계)
- 하나의 쿠폰은 여러 사용자에게 발급될 수 있습니다.
- `coupon_id`를 통해 특정 쿠폰이 어떤 사용자에게 발급되었는지 추적할 수 있습니다.

### OrderItem과 Product (N:1 관계)
- 여러 주문 항목(`OrderItem`)은 하나의 상품(`Product`)과 연결됩니다.
- `product_id`를 통해 특정 주문에 포함된 상품 정보를 확인할 수 있습니다.


### User와 Cart (1:1 관계)
- 한 사용자는 하나의 카트를 소유할 수 있습니다.
- user_id를 통해 특정 사용자의 카트를 식별할 수 있습니다.

### Cart와 CartItem (1:N 관계)
- 하나의 카트에는 여러 개의 상품(CartItem)이 담길 수 있습니다.
- cart_id를 통해 특정 카트에 포함된 상품들을 조회할 수 있습니다.

### CartItem과 Product (N:1 관계)
- 여러 카트 항목(CartItem)은 하나의 상품(Product)과 연결됩니다.
- product_id를 통해 각 카트 항목의 상품 정보를 확인할 수 있습니다.
---

## 2. 주요 설계 의도

### User 테이블에 `balance` 컬럼이 포함된 이유
#### 실시간 조회 성능
- 사용자의 현재 잔액을 빠르게 확인하기 위해 `balance`를 `User` 테이블에 포함.
- 이를 통해 매번 `BalanceHistory`를 합산하지 않고도 현재 잔액을 바로 조회 가능.

#### 정합성 유지
- `BalanceHistory`를 통해 모든 잔액 변동 이력을 추적하면서, `User.balance`는 실시간 데이터를 반영.
- 데이터 변경 시 `BalanceHistory`에 기록을 추가하고, `User.balance`를 업데이트하여 동기화.

### BalanceHistory 테이블의 역할
#### 잔액 변동 추적
- 잔액 충전(`CHARGE`), 사용(`PAYMENT`), 환불(`REFUND`) 등 모든 변동 이력을 기록.
- 데이터 투명성과 감사(Audit)를 위해 모든 변동 내역을 보존.

#### 정합성 검증
- 필요 시 `BalanceHistory`의 데이터를 합산하여 `User.balance`와의 정합성을 검증 가능.

### Order와 Payment의 1:1 관계
#### 명확한 결제 데이터 관리
- 한 주문은 하나의 결제로 처리되므로 1:1 관계를 유지.
- 결제 수단(`method`)과 상태(`status`)를 `Payment` 테이블에서 관리.

### Coupon 테이블과 UserCoupon 테이블
#### Coupon
- 쿠폰의 기본 정보를 저장하며, 재고(`stock`)과 만료일(`expiration_date`)을 포함.

#### UserCoupon
- 사용자가 보유한 쿠폰 정보를 관리.
- 특정 사용자가 쿠폰을 언제 사용했는지(`used_at`), 현재 상태가 무엇인지(`status`) 추적.

### Cart 테이블의 역할
- 사용자가 주문 전 상품을 추가/삭제하며 관리할 수 있는 임시 저장 공간 역할을 합니다.
- 사용자별 독립적인 카트를 관리하여 주문 생성 전에 상품 목록과 수량을 조정할 수 있습니다.
### CartItem 테이블의 역할
- Cart와 Product 간 다대다(N:M) 관계를 처리하기 위한 중간 테이블입니다. 
- 각 상품의 수량과 관련 정보를 별도로 관리합니다. 
- 확장성을 고려해 상품별 추가 정보(예: 할인, 옵션 등)를 저장할 수 있습니다
---

## 3. 각 테이블의 역할 및 주요 컬럼 설명

### User
- 사용자 기본 정보와 현재 잔액(`balance`) 관리.
- `created_at`과 `updated_at`으로 사용자의 생성 및 수정 이력 추적.

### BalanceHistory
- 사용자의 잔액 변동 내역을 기록.
- `type` 컬럼으로 변동 유형(`CHARGE`, `PAYMENT`, `REFUND`)을 구분.
- `current_balance`로 변동 후의 최종 잔액 확인 가능.

### Product
- 상품 기본 정보와 재고(`stock`) 관리.
- 상품 가격(`price`) 변경 시 이력을 `updated_at`으로 추적 가능.

### Order
- 사용자가 생성한 주문 정보를 관리.
- 주문 상태(`status`)로 `PENDING`, `COMPLETED`, `CANCELED` 등의 상태를 추적.

### OrderItem
- 주문에 포함된 각 상품 항목 정보를 관리.
- 각 항목의 수량(`quantity`)과 가격(`price`) 기록.

### Payment
- 주문과 연결된 결제 정보를 관리.
- 결제 수단(`method`)과 상태(`status`)로 결제 성공/실패 여부를 관리.

### Coupon
- 쿠폰의 기본 정보와 재고(`stock`), 만료일(`expiration_date`)을 관리.
- 쿠폰 코드(`code`)를 통해 특정 쿠폰을 식별.

### UserCoupon
- 사용자가 보유한 쿠폰과 상태(`ACTIVE`, `USED`, `EXPIRED`)를 관리.
- `used_at`으로 쿠폰 사용 시점 추적 가능.

### Cart
- 사용자별로 하나의 카트를 관리하며, 주문 전 상품 목록을 임시로 저장.

### CartItem
- 카트에 담긴 상품 정보와 수량을 관리.
