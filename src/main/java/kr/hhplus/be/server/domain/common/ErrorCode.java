package kr.hhplus.be.server.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // USER
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "INSUFFICIENT_BALANCE", "잔액이 부족합니다."),
    CHARGE_AMOUNT_NOT_VALID(HttpStatus.BAD_REQUEST, "CHARGE_AMOUNT_NOT_VALID", "충전금액이 유효하지 않습니다."),
    USER_NOT_EXIST(HttpStatus.NOT_FOUND, "USER_NOT_EXIST", "존재하지 않는 사용자입니다."),

    // PRODUCT
    PRODUCT_INSUFFICIENT_INVENTORY(HttpStatus.BAD_REQUEST, "PRODUCT_INSUFFICIENT_INVENTORY", "재고가 부족합니다."),
    PRODUCT_NOT_EXIST(HttpStatus.NOT_FOUND, "PRODUCT_NOT_EXIST", "상품 정보가 존재하지 않습니다."),
    INVALID_PRODUCT_QUANTITY(HttpStatus.BAD_REQUEST, "INVALID_PRODUCT_QUANTITY", "잘못된 상품 수량입니다."),

    // COUPON
    COUPON_NOT_EXIST(HttpStatus.NOT_FOUND, "COUPON_NOT_EXIST", "존재하지 않는 쿠폰입니다."),
    COUPON_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "COUPON_NOT_AVAILABLE", "사용할 수 없는 쿠폰입니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "COUPON_EXPIRED", "쿠폰 유효 기간이 만료되었습니다."),
    COUPON_ALREADY_ISSUED(HttpStatus.CONFLICT, "COUPON_ALREADY_ISSUED", "이미 발급된 쿠폰입니다."),
    INSUFFICIENT_COUPON_STOCK(HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_COUPON_STOCK", "쿠폰 재고가 부족합니다."),
    INVALID_COUPON_REGISTRATION_PERIOD(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_COUPON_REGISTRATION_PERIOD", "쿠폰 등록 기간이 유효하지 않습니다."),


    // ORDER
    ORDER_NOT_EXIST(HttpStatus.NOT_FOUND, "ORDER_NOT_EXIST", "존재하지 않는 주문입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 에러 입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "필수값이 누락되었습니다."),
    CART_ITEM_COUNT_MISMATCH(HttpStatus.BAD_REQUEST, "CART_ITEM_COUNT_MISMATCH", "카트 아이템 개수가 일치하지 않습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
