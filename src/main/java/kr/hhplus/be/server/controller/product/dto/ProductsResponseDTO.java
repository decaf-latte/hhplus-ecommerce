package kr.hhplus.be.server.controller.product.dto;

import kr.hhplus.be.server.service.product.vo.ProductVO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductsResponseDTO {

    private long id;
    private String name;
    private BigDecimal price;
    private int currentStock;

    @Builder
    public ProductsResponseDTO(long id, String name, BigDecimal price, int currentStock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.currentStock = currentStock;
    }

    public static ProductsResponseDTO from(ProductVO productVO) {
    return ProductsResponseDTO.builder()
        .id(productVO.getId())
        .name(productVO.getName())
        .price(productVO.getPrice())
        .currentStock(productVO.getStock())
        .build();
    }
}
