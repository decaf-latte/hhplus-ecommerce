package kr.hhplus.be.server.controller.coupon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CouponIssueRequestDTO {
    private long userId;
    private String couponCode;

    @Builder
    public CouponIssueRequestDTO(long userId, String couponCode) {
        this.userId = userId;
        this.couponCode = couponCode;
    }
}
