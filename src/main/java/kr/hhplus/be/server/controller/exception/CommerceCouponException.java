package kr.hhplus.be.server.controller.exception;

import kr.hhplus.be.server.domain.common.ErrorCode;

public class CommerceCouponException extends CommerceException {
    public CommerceCouponException(ErrorCode errorCode) {
        super(errorCode);
    }

}
