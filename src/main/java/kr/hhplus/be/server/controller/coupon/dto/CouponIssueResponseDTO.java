package kr.hhplus.be.server.controller.coupon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CouponIssueResponseDTO {
    private long couponId;
    private long userId;
    private String couponCode;
    private int discount;
    private LocalDateTime issueDate;
    private LocalDateTime expireDate;

    @Builder
    public CouponIssueResponseDTO(long couponId, long userId, String couponCode, int discount, LocalDateTime issueDate, LocalDateTime expireDate) {
        this.couponId = couponId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.discount = discount;
        this.issueDate = issueDate;
        this.expireDate = expireDate;
    }
}
