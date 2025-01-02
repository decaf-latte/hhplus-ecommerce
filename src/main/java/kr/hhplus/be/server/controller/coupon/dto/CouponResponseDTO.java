package kr.hhplus.be.server.controller.coupon.dto;

import kr.hhplus.be.server.domain.coupon.code.CouponStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CouponResponseDTO {
    private long couponId;
    private long userId;
    private String couponCode;
    private int discount;
    private CouponStatus status;
    private LocalDateTime issueDate;
    private LocalDateTime expireDate;

    @Builder
    public CouponResponseDTO(long couponId, long userId, String couponCode, int discount, CouponStatus status, LocalDateTime issueDate, LocalDateTime expireDate) {
        this.couponId = couponId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.discount = discount;
        this.status = status;
        this.issueDate = issueDate;
        this.expireDate = expireDate;
    }
}
