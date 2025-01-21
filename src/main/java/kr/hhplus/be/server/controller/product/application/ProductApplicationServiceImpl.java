package kr.hhplus.be.server.controller.product.application;

import java.util.List;
import java.util.stream.Collectors;
import kr.hhplus.be.server.controller.exception.CommerceProductException;
import kr.hhplus.be.server.domain.common.ErrorCode;
import kr.hhplus.be.server.service.order.OrderItemService;
import kr.hhplus.be.server.service.order.vo.TopOrderItemVO;
import kr.hhplus.be.server.service.product.ProductService;
import kr.hhplus.be.server.service.product.vo.ProductVO;
import kr.hhplus.be.server.service.product.vo.TopProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
                            .orElseThrow(() -> new CommerceProductException(ErrorCode.PRODUCT_NOT_EXIST));

                    return new TopProductVO(
                            productVO.getId(),
                            productVO.getName(),
                            orderItem.getSalesCount()
                    );
                })
                .collect(Collectors.toList());
    }


}
