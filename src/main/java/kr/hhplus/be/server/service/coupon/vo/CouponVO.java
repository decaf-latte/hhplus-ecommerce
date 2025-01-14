package kr.hhplus.be.server.service.coupon.vo;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Getter
@NoArgsConstructor
public class CouponVO {

    private Long id;
    private String name;
    private String code;
    private double discount;
    private int stock;
    private LocalDateTime registerStartDate;
    private LocalDateTime registerEndDate;
    private Integer availableDay;

    @Builder
    public CouponVO(Long id, String name, String code, double discount, int stock, LocalDateTime registerStartDate,
                    LocalDateTime registerEndDate, Integer availableDay) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.discount = discount;
        this.stock = stock;
        this.registerStartDate = registerStartDate;
        this.registerEndDate = registerEndDate;
        this.availableDay = availableDay;
    }

    public static CouponVO from(Coupon coupon) {

        if (ObjectUtils.isEmpty(coupon)) {
            return null;
        }

        return CouponVO.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .code(coupon.getCode())
                .discount(coupon.getDiscount())
                .stock(coupon.getStock())
                .registerStartDate(coupon.getRegisterStartDate())
                .registerEndDate(coupon.getRegisterEndDate())
                .availableDay(coupon.getAvailableDay())
                .build();
    }
}
