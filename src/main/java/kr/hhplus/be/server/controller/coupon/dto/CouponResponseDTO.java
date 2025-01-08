package kr.hhplus.be.server.controller.coupon.dto;

import kr.hhplus.be.server.domain.coupon.code.CouponStatus;
import kr.hhplus.be.server.service.coupon.vo.UserCouponVO;
import lombok.*;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDTO {

    private List<UserCouponResponseData> userCoupons;

    @Getter
    @NoArgsConstructor
    public static class UserCouponResponseData {

        private Long userCouponId;
        private Long userId;
        private Long couponId;
        private String couponName;
        private double couponDiscount;
        private CouponStatus status;
        private LocalDateTime expiredAt;

        @Builder
        public UserCouponResponseData(Long userCouponId, Long userId, Long couponId, String couponName, double couponDiscount, CouponStatus status, LocalDateTime expiredAt) {
            this.userCouponId = userCouponId;
            this.userId = userId;
            this.couponId = couponId;
            this.couponName = couponName;
            this.couponDiscount = couponDiscount;
            this.status = status;
            this.expiredAt = expiredAt;
        }

        public static UserCouponResponseData from(UserCouponVO userCouponVO) {

            if (ObjectUtils.isEmpty(userCouponVO)) {
                return null;
            }

            return UserCouponResponseData.builder()
                    .userCouponId(userCouponVO.getId())
                    .userId(userCouponVO.getUser().getId())
                    .couponId(userCouponVO.getCoupon().getId())
                    .couponName(userCouponVO.getCoupon().getName())
                    .couponDiscount(userCouponVO.getCoupon().getDiscount())
                    .status(userCouponVO.getStatus())
                    .expiredAt(userCouponVO.getExpiredAt())
                    .build();
        }
    }
}
