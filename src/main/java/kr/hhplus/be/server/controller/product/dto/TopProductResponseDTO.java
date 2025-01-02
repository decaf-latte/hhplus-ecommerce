package kr.hhplus.be.server.controller.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TopProductResponseDTO {

    public long id;
    public String name;
    public int salesCount;

    @Builder
    public TopProductResponseDTO(long id, String name, int salesCount) {
        this.id = id;
        this.name = name;
        this.salesCount = salesCount;
    }
}
