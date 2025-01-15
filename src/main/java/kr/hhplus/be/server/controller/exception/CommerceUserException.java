package kr.hhplus.be.server.controller.exception;

import kr.hhplus.be.server.domain.common.ErrorCode;

public class CommerceUserException extends CommerceException {
    public CommerceUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
