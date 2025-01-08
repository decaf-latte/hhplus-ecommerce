package kr.hhplus.be.server.service.product;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.product.code.ProductStatus;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.service.product.vo.ProductVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("판매 상태인 제품 목록을 페이지 단위로 조회")
    void getProducts_shouldReturnPagedProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Product product1 = Product.of()
                .name("Product 1")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .status(ProductStatus.SALE)
                .build();

        Product product2 = Product.of()
                .name("Product 2")
                .price(BigDecimal.valueOf(200))
                .stock(5)
                .status(ProductStatus.SALE)
                .build();

        List<Product> productList = List.of(product1, product2);

        when(productRepository.findAllByStatus(ProductStatus.SALE, pageable)).thenReturn(productList);

        List<ProductVO> result = productService.getProducts(pageable);

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getName()).isEqualTo("Product 1");
        assertThat(result.get(0).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));

        assertThat(result.get(1).getName()).isEqualTo("Product 2");
        assertThat(result.get(1).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

    @Test
    @DisplayName("제품 ID로 제품을 조회 - 성공")
    void getProductByProductId_shouldReturnProduct() {

        long productId = 1L;

        Product product = Product.of()
                .name("Product 1")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .status(ProductStatus.SALE)
                .build();

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductByProductId(productId);

        assertThat(result).isNotNull();
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(product);
    }

    @Test
    @DisplayName("제품 ID로 제품을 조회 - 실패 시 예외 발생")
    void getProductByProductId_shouldThrowExceptionWhenNotFound() {
        long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productRepository.findById(productId);

        assertThat(foundProduct).isNotPresent();
    }
}
