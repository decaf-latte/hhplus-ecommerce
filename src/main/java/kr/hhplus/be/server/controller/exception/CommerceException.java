package kr.hhplus.be.server.controller.exception;

import kr.hhplus.be.server.domain.common.ErrorCode;
import org.springframework.http.HttpStatus;


public abstract class CommerceException extends RuntimeException {
    private final ErrorCode errorCode;

    public CommerceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }
}
