package kr.hhplus.be.server.service.order.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TopOrderItemVO {

    private Long productId;
    private Long salesCount;

    @Builder
    public TopOrderItemVO(Long productId, Long salesCount) {
        this.productId = productId;
        this.salesCount = salesCount;
    }
}
