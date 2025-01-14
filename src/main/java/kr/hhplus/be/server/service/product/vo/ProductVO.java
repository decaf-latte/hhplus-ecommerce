package kr.hhplus.be.server.service.product.vo;

import kr.hhplus.be.server.domain.product.code.ProductStatus;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ProductVO {

    private Long id;
    private String name;
    private BigDecimal price;
    private int stock;
    private ProductStatus status;

    @Builder
    public ProductVO(Long id, String name, BigDecimal price, int stock, ProductStatus status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    public static ProductVO from(Product product) {

        if (ObjectUtils.isEmpty(product)) {
            return null;
        }

        return ProductVO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .status(product.getStatus())
                .build();
    }
}
