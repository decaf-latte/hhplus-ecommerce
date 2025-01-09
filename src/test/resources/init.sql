-- User 테이블
CREATE TABLE `user` (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        balance DECIMAL(10, 2) NOT NULL DEFAULT 0,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Product 테이블
CREATE TABLE product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         price DECIMAL(10, 2) NOT NULL,
                         stock INT NOT NULL,
                         status ENUM('SALE', 'SOLD_OUT','DISCONTINUED') NOT NULL,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Coupon 테이블
CREATE TABLE coupon (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        code VARCHAR(255) NOT NULL UNIQUE,
                        discount DOUBLE NOT NULL,
                        stock INT NOT NULL,
                        register_start_date DATETIME NOT NULL,
                        register_end_date DATETIME NOT NULL,
                        available_day INT,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- UserCoupon 테이블
CREATE TABLE user_coupon (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             coupon_id BIGINT NOT NULL,
                             status ENUM('ACTIVE', 'USED', 'EXPIRED') NOT NULL,
                             expired_at DATETIME NOT NULL,
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                             updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE,
                             FOREIGN KEY (coupon_id) REFERENCES coupon(id) ON DELETE CASCADE
);

-- BalanceHistory 테이블
CREATE TABLE balance_history (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 user_id BIGINT NOT NULL,
                                 type ENUM('CHARGE', 'USE', 'REFUND') NOT NULL,
                                 change_amount DECIMAL(10, 2) NOT NULL,
                                 created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
);

-- Order 테이블
CREATE TABLE `order` (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         total_price DECIMAL(10, 2) NOT NULL,
                         status ENUM('PENDING', 'COMPLETED', 'CANCELED') NOT NULL,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
);

-- OrderItem 테이블
CREATE TABLE order_item (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            order_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            quantity INT NOT NULL,
                            price DECIMAL(10, 2) NOT NULL,
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE,
                            FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- Payment 테이블
CREATE TABLE payment (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         order_id BIGINT NOT NULL,
                         amount DECIMAL(10, 2) NOT NULL,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE
);

-- PaymentCoupon 테이블
CREATE TABLE payment_coupon (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                payment_id BIGINT NOT NULL,
                                user_coupon_id BIGINT NOT NULL,
                                amount DECIMAL(10, 2) NOT NULL,
                                FOREIGN KEY (payment_id) REFERENCES payment(id) ON DELETE CASCADE,
                                FOREIGN KEY (user_coupon_id) REFERENCES user_coupon(id) ON DELETE CASCADE
);

-- PaymentBalance 테이블
CREATE TABLE payment_balance (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 payment_id BIGINT NOT NULL,
                                 balance_history_id BIGINT NOT NULL,
                                 amount DECIMAL(10, 2) NOT NULL,
                                 FOREIGN KEY (payment_id) REFERENCES payment(id) ON DELETE CASCADE,
                                 FOREIGN KEY (balance_history_id) REFERENCES balance_history(id) ON DELETE CASCADE
);

-- CartItem 테이블
CREATE TABLE cart_item (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           product_id BIGINT NOT NULL,
                           quantity INT NOT NULL,
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE,
                           FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);


-- User 데이터 삽입
INSERT INTO `user` (name, email, balance, created_at, updated_at)
VALUES
    ('Alice', 'alice@example.com', 2000.00, NOW(), NOW()),
    ('Bob', 'bob@example.com', 300.00, NOW(), NOW()),
    ('Charlie', 'charlie@example.com', 1500.00, NOW(), NOW());

-- Product 데이터 삽입
INSERT INTO product (name, price, stock, status, created_at, updated_at)
VALUES
    ('Laptop', 1000.00, 10, 'SALE', NOW(), NOW()),
    ('Smartphone', 800.00, 20, 'SALE', NOW(), NOW());

-- Coupon 데이터 삽입
INSERT INTO coupon (name, code, discount, stock, register_start_date, register_end_date, available_day, created_at, updated_at)
VALUES
    ('10% OFF', 'DISCOUNT10', 10.0, 100, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 30, NOW(), NOW()),
    ('20% OFF', 'DISCOUNT20', 20.0, 50, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 30, NOW(), NOW());

-- UserCoupon 데이터 삽입
INSERT INTO user_coupon (user_id, coupon_id, status, expired_at, created_at, updated_at)
VALUES
    (1, 1, 'ACTIVE', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
    (2, 2, 'ACTIVE', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW());

-- BalanceHistory 데이터 삽입
INSERT INTO balance_history (user_id, type, change_amount, created_at, updated_at)
VALUES
    (1, 'CHARGE', 2000.00, NOW(), NOW()),
    (2, 'CHARGE', 500.00, NOW(), NOW()),
    (3, 'CHARGE', 1500.00, NOW(), NOW()),
    (2, 'USE', -200.00, NOW(), NOW());

-- Order 데이터 삽입
INSERT INTO `order` (user_id, total_price, status, created_at, updated_at)
VALUES
    (1, 1200.00, 'COMPLETED', NOW(), NOW());

-- OrderItem 데이터 삽입
INSERT INTO order_item (order_id, product_id, quantity, price, created_at, updated_at)
VALUES
    (1, 1, 1, 1000.00, NOW(), NOW());

-- Payment 데이터 삽입
INSERT INTO payment (order_id, amount, created_at, updated_at)
VALUES
    (1, 1200.00, NOW(), NOW());

-- PaymentCoupon 데이터 삽입
INSERT INTO payment_coupon (payment_id, user_coupon_id, amount)
VALUES
    (1, 1, 120.00);

-- PaymentBalance 데이터 삽입
INSERT INTO payment_balance (payment_id, balance_history_id, amount)
VALUES
    (1, 1, 1080.00);

-- CartItem 데이터 삽입
INSERT INTO cart_item (user_id, product_id, quantity, created_at, updated_at)
VALUES
    (1, 2, 2, NOW(), NOW());
