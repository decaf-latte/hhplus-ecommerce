# **이커머스 API 명세서**

---

## **1. 상품 조회 API**

### **설명**
- 상품 정보(ID, 이름, 가격, 잔여수량)를 조회합니다.
- 조회 시점의 상품별 **정확한 잔여수량**을 반환합니다.

### **Request**
- **URI**: `/api/v1/products`
- **Method**: `GET`
- **Headers**:
    - `Content-Type`: `application/json`

### **Response**
- **Status**: `200 OK`
- **Body**:
  ```json
  {
    "message": "SUCCESS",
    "isSuccess": true,
    "data": [
        {
            "productId": 1,
            "name": "상품A",
            "price": 1000,
            "currentStock": 15
        },
        {
            "productId": 2,
            "name": "상품B",
            "price": 2000,
            "currentStock": 20
        }
    ]
  }

### **Error**
- **Status**: `404 Not Found`
- **Body**:
  ```json
  {
  "code": 404,
  "message": "Products Not Found"
  }
  
---

## **2. 잔액 충전 / 조회 API**

### **잔액 충전**
- 결제에 사용될 금액을 충전합니다.
- 사용자 식별자를 기반으로 충전할 금액을 받아 잔액 충전합니다.

### **Request**
- **URI**: `/api/v1/balance/charge`
- **Method**: `POST`
- **Headers**:
    - `Content-Type`: `application/json`
- **Body**:
  ```json
  {
  "userId": 1,
  "amount": 5000
  }

### **Response**
- **Status**: `200 OK`
- **Body**:
  ```json
  {
  "message": "SUCCESS",
  "isSuccess": true,
  "data": {
    "userId": 1,
    "currentBalance": 15000
  }
  }

### **잔액 조회**
- 사용자 식별자로 사용자 잔액 조회합니다.

### **Request**
- **URI**: `/api/v1/balance/{userId}`
- **Method**: `GET`
- **Headers**:
    - `Content-Type`: `application/json`

### **Response**
- **Status**: `200 OK`
- **Body**:
  ```json
  {
  "message": "SUCCESS",
  "isSuccess": true,
  "data": {
    "userId": 1,
    "currentBalance": 15000
  }
  }

### **Error**
- **Status**: `404 Not Found`
- **Body**:
  ```json
  {
  "code": 404,
  "message": "User Not Found"
  }

---

## **3. 주문 / 결제 API**

### **설명**
- 사용자 식별자와 상품 목록을 입력받아 주문하고 결제를 수행합니다.
- 결제는 미리 충전된 잔액을 기반으로 수행하며 성공 시 잔액을 차감합니다.

### **Request**
- **URI**: `/api/v1/orders`
- **Method**: `POST`
- **Headers**:
    - `Content-Type`: `application/json`
- **Body**:
  ```json
  
  {
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2.
      "price": 1000
    },
    {
      "productId": 2,
      "quantity": 1,
      "price": 1000
    }
  ]
  }

### **Response**
- **Status**: `201 Created`
- **Body**:
  ```json
  {
  "message": "SUCCESS",
  "isSuccess": true,
  "data": {
    "orderId": 1001,
    "userId": 1,
    "price": 2050,
    "discount": 1000,
    "finalPrice": 1050,
    "remainingBalance": 12950,
    "status": "COMPLETED",
    "orderDate": "2025-01-01T12:00:00Z"
  }
  }
  

### **Error**
- **Status**: `409 Conflict`
- **Body**:
  ```json
  {
  "code": 409,
  "message": "Insufficient Balance or Stock"
  }
  
---
## **4. 상위 상품 조회 API**

### **설명**
- 최근 3일간 판매량이 가장 높은 상위 5개 상품을 조회합니다.
- 
### **Request**
- **URI**: `/api/v1/products/top`
- **Method**: `GET`
- **Headers**:
    - `Content-Type`: `application/json`

### **Response**
- **Status**: `200 OK`
- **Body**:
  ```json
  {
    "message": "SUCCESS",
    "isSuccess": true,
    "data": [
        {
            "productId": 1,
            "name": "상품B",
            "salesCount": 200
        },
        {
            "productId": 2,
            "name": "상품C",
            "salesCount": 180
        },
        {
            "productId": 3,
            "name": "상품Z",
            "salesCount": 150
        },
        {
            "productId": 5,
            "name": "상품E",
            "salesCount": 100
        },
        {
            "productId": 8,
            "name": "상품O",
            "salesCount": 80
        }
    ]
  }


### **Error**
- **Status**: `404 Not Found`
- **Body**:
  ```json
  {
  "code": 404,
  "message": "No Sales Data Found"
  }


---

## **5. 선착순 쿠폰 기능 API**

### **선착순 쿠폰 발급**

### **Request**
- **URI**: `/api/v1/coupons/issue`
- **Method**: `POST`
- **Headers**:
    - `Content-Type`: `application/json`
- **Body**:
  ```json
  {
  "userId": 1
  "couponCode":"AAA"
  }

### **Response**
- **Status**: `201 Created`
- **Body**:
  ```json
  {
    "message": "SUCCESS",
    "isSuccess": true,
    "data": [
        {
            "couponId": 5001,
            "userId": 1,
            "discount": 1000,
            "issueDate": "2025-01-01T12:00:00Z",
            "expireDate": "2025-01-10T23:59:59Z"
        }
    ]
    }

### **보유 쿠폰 목록 조회**
- 사용자 식별자로 사용자 잔액 조회합니다.

### **Request**
- **URI**: `/api/v1/coupons/{userId}`
- **Method**: `GET`
- **Headers**:
    - `Content-Type`: `application/json`

### **Response**
- **Status**: `200 OK`
- **Body**:
  ```json
  {
    "message": "SUCCESS",
    "isSuccess": true,
    "data": [
        {
            "couponId": 5001,
            "userId": 1,
            "couponCode": "AAA",
            "discount": 1000,
            "status": "ACTIVE",
            "issueDate": "2025-01-01T12:00:00Z",
            "expireDate": "2025-01-10T23:59:59Z"
        }
    ]
    }


### **Error**
- **Status**: `404 Not Found`
- **Body**:
  ```json
  {
  "code": 404,
  "message": "No Coupons Found"
  }

