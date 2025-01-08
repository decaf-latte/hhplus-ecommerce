package kr.hhplus.be.server.service.coupon.vo;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.coupon.code.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.service.user.vo.UserVO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Getter
@NoArgsConstructor
public class UserCouponVO {

    private Long id;
    private UserVO user;
    private CouponVO coupon;
    private CouponStatus status;
    private LocalDateTime expiredAt;

    @Builder
    public UserCouponVO(Long id, UserVO user, CouponVO coupon, CouponStatus status, LocalDateTime expiredAt) {
        this.id = id;
        this.user = user;
        this.coupon = coupon;
        this.status = status;
        this.expiredAt = expiredAt;
    }

    public static UserCouponVO from(UserCoupon userCoupon) {

        if (ObjectUtils.isEmpty(userCoupon)) {
            return null;
        }

        return UserCouponVO.builder()
                .id(userCoupon.getId())
                .user(UserVO.from(userCoupon.getUser()))
                .coupon(CouponVO.from(userCoupon.getCoupon()))
                .status(userCoupon.getStatus())
                .expiredAt(userCoupon.getExpiredAt())
                .build();
    }
}
