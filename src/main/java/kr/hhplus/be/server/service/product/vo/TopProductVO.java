package kr.hhplus.be.server.service.product.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TopProductVO {
    private Long id;
    private String name;
    private Long salesCount;

    @Builder
    public TopProductVO(Long id, String name, Long salesCount) {
        this.id = id;
        this.name = name;
        this.salesCount = salesCount;
    }

}
