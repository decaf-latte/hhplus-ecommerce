package kr.hhplus.be.server.controller.exception;

import kr.hhplus.be.server.domain.common.ErrorCode;

public class CommerceProductException extends CommerceException {
    public CommerceProductException(ErrorCode errorCode) {
        super(errorCode);
    }

}
