package kr.hhplus.be.server.controller.product.application;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.service.order.OrderItemService;
import kr.hhplus.be.server.service.order.vo.TopOrderItemVO;
import kr.hhplus.be.server.service.product.ProductService;
import kr.hhplus.be.server.service.product.vo.ProductVO;
import kr.hhplus.be.server.service.product.vo.TopProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductApplicationServiceImpl implements ProductApplicationService{

    private final ProductService productService;
    private final OrderItemService orderItemService;

    @Override
    public List<ProductVO> getProduct(Pageable pageable) {
        return productService.getProducts(pageable);
    }

    @Override
    public List<TopProductVO> getTopProducts() {

        List<TopOrderItemVO> topOrderItemVOList
                = orderItemService.getTopOrderItems();

        return topOrderItemVOList.stream()
                .map(orderItem -> {
                    ProductVO productVO = productService.getProductByProductId(orderItem.getProductId())
                            .map(ProductVO::from)
                            .orElseThrow(() -> new EntityNotFoundException("Product not found."));

                    return new TopProductVO(
                            productVO.getId(),
                            productVO.getName(),
                            orderItem.getSalesCount()
                    );
                })
                .collect(Collectors.toList());
    }


}
