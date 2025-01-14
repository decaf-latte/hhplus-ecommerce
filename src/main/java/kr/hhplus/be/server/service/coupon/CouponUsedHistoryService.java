package kr.hhplus.be.server.service.coupon;

import kr.hhplus.be.server.domain.coupon.entity.CouponUsedHistory;

public interface CouponUsedHistoryService {

    CouponUsedHistory save(Long userId, Long userCouponId);
}
