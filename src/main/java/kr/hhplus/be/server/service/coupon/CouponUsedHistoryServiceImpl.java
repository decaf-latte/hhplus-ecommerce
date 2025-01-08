package kr.hhplus.be.server.service.coupon;

import kr.hhplus.be.server.domain.coupon.code.CouponUsedType;
import kr.hhplus.be.server.domain.coupon.entity.CouponUsedHistory;
import kr.hhplus.be.server.domain.coupon.repository.CouponUsedHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponUsedHistoryServiceImpl implements CouponUsedHistoryService{

    private final CouponUsedHistoryRepository couponUsedHistoryRepository;

    @Override
    public CouponUsedHistory save(Long userId, Long userCouponId) {
        CouponUsedHistory couponUsedHistory = CouponUsedHistory.of()
                .userId(userId)
                .userCouponId(userCouponId)
                .usedType(CouponUsedType.USED)
                .build();

        return couponUsedHistoryRepository.save(couponUsedHistory);
    }
}
