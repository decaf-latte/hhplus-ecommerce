package kr.hhplus.be.server.controller.product.application;

import kr.hhplus.be.server.service.order.OrderItemService;
import kr.hhplus.be.server.service.product.ProductService;
import kr.hhplus.be.server.service.product.vo.ProductVO;
import kr.hhplus.be.server.service.product.vo.TopProductVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ProductApplicationServiceTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init.sql");

    @Autowired
    private ProductApplicationServiceImpl productApplicationService;

    @Test
    @DisplayName("상품 조회 성공")
    void getProducts_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        List<ProductVO> products = productApplicationService.getProduct(pageable);

        // Then
        assertNotNull(products);
        assertEquals(5, products.size());
        assertEquals("Laptop", products.get(0).getName());
        assertEquals("Smartphone", products.get(1).getName());
    }

    @Test
    @DisplayName("상위 판매 상품 조회 성공")
    void getTopProducts_Success() {
        // When
        List<TopProductVO> topProducts = productApplicationService.getTopProducts();

        // Then
        assertNotNull(topProducts);
        assertEquals(5, topProducts.size());
        assertEquals("Tablet", topProducts.get(0).getName());
        assertEquals(6, topProducts.get(0).getSalesCount());
    }
}
