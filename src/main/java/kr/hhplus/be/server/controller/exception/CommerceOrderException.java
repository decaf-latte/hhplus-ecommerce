package kr.hhplus.be.server.controller.exception;

import kr.hhplus.be.server.domain.common.ErrorCode;

public class CommerceOrderException extends CommerceException {
    public CommerceOrderException(ErrorCode errorCode) {
        super(errorCode);
    }

}
