package kr.hhplus.be.server.controller.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductsResponseDTO {

    private long id;
    private String name;
    private int price;
    private int currentStock;

    @Builder
    public ProductsResponseDTO(long id, String name, int price, int currentStock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.currentStock = currentStock;
    }
}
