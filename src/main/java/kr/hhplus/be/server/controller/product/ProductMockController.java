package kr.hhplus.be.server.controller.product;

import kr.hhplus.be.server.config.dto.ResponseDTO;
import kr.hhplus.be.server.controller.product.dto.ProductsResponseDTO;
import kr.hhplus.be.server.controller.product.dto.TopProductResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductMockController {

    //상품 조회 API - 성공
    @GetMapping("/api/v1/products/mock")
    public ResponseDTO<List<ProductsResponseDTO>> getProductsSuccess() {
        return ResponseDTO.success(List.of(
                ProductsResponseDTO.builder()
                        .id(1L)
                        .name("상품A")
                        .price(1000)
                        .currentStock(15)
                        .build(),
                ProductsResponseDTO.builder()
                        .id(2L)
                        .name("상품B")
                        .price(2000)
                        .currentStock(20)
                        .build()
        ));
    }

    //상품 조회 API - 실패
    @GetMapping("/api/v1/products/mock/error")
    public ResponseDTO<List<ProductsResponseDTO>> getProductsFail() {
        return ResponseDTO.fail("Products Not Found", null);
    }

    //판매 상위 5개 상품 조회 API
    @GetMapping("/api/v1/products/top/mock")
    public ResponseDTO<List<TopProductResponseDTO>> getTopProductsSuccess() {
        return ResponseDTO.success(List.of(
                TopProductResponseDTO.builder()
                        .id(1L)
                        .name("상품B")
                        .salesCount(200)
                        .build(),
                TopProductResponseDTO.builder()
                        .id(2L)
                        .name("상품C")
                        .salesCount(180)
                        .build(),
                TopProductResponseDTO.builder()
                        .id(3L)
                        .name("상품Z")
                        .salesCount(150)
                        .build(),
                TopProductResponseDTO.builder()
                        .id(5L)
                        .name("상품E")
                        .salesCount(100)
                        .build(),
                TopProductResponseDTO.builder()
                        .id(8L)
                        .name("상품O")
                        .salesCount(80)
                        .build()
        ));
    }

    //판매 상위 5개 상품 조회 API - 실패
    @GetMapping("/api/v1/products/top/mock/error")
    public ResponseDTO<List<TopProductResponseDTO>> getTopProductsFail() {
        return ResponseDTO.fail("No Sales Data Found", null);
    }

}
