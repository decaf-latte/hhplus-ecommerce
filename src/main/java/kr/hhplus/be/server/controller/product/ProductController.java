package kr.hhplus.be.server.controller.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.config.dto.ResponseDTO;
import kr.hhplus.be.server.config.dto.ResponsePageDTO;
import kr.hhplus.be.server.controller.product.application.ProductApplicationService;
import kr.hhplus.be.server.controller.product.dto.ProductsResponseDTO;
import kr.hhplus.be.server.controller.product.dto.TopProductResponseDTO;
import kr.hhplus.be.server.service.product.ProductService;
import kr.hhplus.be.server.service.product.vo.ProductVO;
import kr.hhplus.be.server.service.product.vo.TopProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "상품 API", description = "상품 목록 조회 / 3일간 최다 판매상품 5가지 조회 API")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductApplicationService productApplicationService;

    //상품 조회 API
    @Operation(summary = "상품 목록 조회 ", description = "판매중인 상품 목록을 조회합니다.")
    @GetMapping("/api/v1/products")
    public ResponsePageDTO<List<ProductsResponseDTO>> getProducts(Pageable pageable) {

        List<ProductVO> productVOList =
                productApplicationService.getProduct(pageable);

        List<ProductsResponseDTO> productsResponseDTOList =
                productVOList.stream()
                        .map(ProductsResponseDTO::from).toList();

        return ResponsePageDTO.success(
                productsResponseDTOList,
                pageable.getPageNumber(),
                productVOList.size()
        );
    }

    //판매 상위 5개 상품 조회 API
    @Operation(summary = "3일간 최다 판매상품 5가지 조회", description = "조회 일자 기준 3일간 최다 판매 상품 5개 조회")
    @GetMapping("/api/v1/products/top")
    public ResponseDTO<List<TopProductResponseDTO>> getTopProducts() {

        List<TopProductVO> topProductVOList =
                productApplicationService.getTopProducts();

        List<TopProductResponseDTO> topProductResponseDTOList =
                topProductVOList.stream()
                        .map(TopProductResponseDTO::from).toList();

        return ResponseDTO.success(topProductResponseDTOList);
    }
}
