package kr.hhplus.be.server.service.coupon;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import kr.hhplus.be.server.domain.coupon.code.CouponUsedType;
import kr.hhplus.be.server.domain.coupon.entity.CouponUsedHistory;
import kr.hhplus.be.server.domain.coupon.repository.CouponUsedHistoryRepository;
import kr.hhplus.be.server.service.coupon.CouponUsedHistoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponUsedHistoryServiceTest {

    @Mock
    private CouponUsedHistoryRepository couponUsedHistoryRepository;

    @InjectMocks
    private CouponUsedHistoryServiceImpl couponUsedHistoryService;

    @Test
    @DisplayName("쿠폰 사용 내역을 성공적으로 저장")
    void saveCouponUsedHistory_Success() {
        Long userId = 1L;
        Long userCouponId = 1L;
        CouponUsedHistory couponUsedHistory = CouponUsedHistory.of()
                .userId(userId)
                .userCouponId(userCouponId)
                .usedType(CouponUsedType.USED)
                .build();
        when(couponUsedHistoryRepository.save(any(CouponUsedHistory.class))).thenReturn(couponUsedHistory);

        CouponUsedHistory result = couponUsedHistoryService.save(userId, userCouponId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(userCouponId, result.getUserCouponId());
        assertEquals(CouponUsedType.USED, result.getUsedType());
    }
}