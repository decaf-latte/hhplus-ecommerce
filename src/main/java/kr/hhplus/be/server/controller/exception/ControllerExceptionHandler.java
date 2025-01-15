package kr.hhplus.be.server.controller.exception;

import kr.hhplus.be.server.config.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseDTO<Object> handleException(Exception e) {
        log.error("Unexpected Exception: ", e);
        return ResponseDTO.fail("내부적인 오류 발생.", null);
    }

    @ExceptionHandler(CommerceUserException.class)
    public ResponseEntity<ResponseDTO<Object>> handleUserException(CommerceUserException e) {
        log.error("UserException: {}", e.getMessage());
        return ResponseEntity
            .status(e.getStatus())
            .body(ResponseDTO.fail(e.getMessage(), e.getErrorCode()));
    }

    @ExceptionHandler(CommerceProductException.class)
    public ResponseEntity<ResponseDTO<Object>> handleProductException(CommerceProductException e) {
        log.error("ProductException: {}", e.getMessage());
        return ResponseEntity
            .status(e.getStatus())
            .body(ResponseDTO.fail(e.getMessage(), e.getErrorCode()));
    }

    @ExceptionHandler(CommerceCouponException.class)
    public ResponseEntity<ResponseDTO<Object>> handleCouponException(CommerceCouponException e) {
        log.error("CouponException: {}", e.getMessage());
        return ResponseEntity
            .status(e.getStatus())
            .body(ResponseDTO.fail(e.getMessage(), e.getErrorCode()));
    }

    @ExceptionHandler(CommerceOrderException.class)
    public ResponseEntity<ResponseDTO<Object>> handleOrderException(CommerceOrderException e) {
        log.error("OrderException: {}", e.getMessage());
        return ResponseEntity
            .status(e.getStatus())
            .body(ResponseDTO.fail(e.getMessage(), e.getErrorCode()));
    }

}
