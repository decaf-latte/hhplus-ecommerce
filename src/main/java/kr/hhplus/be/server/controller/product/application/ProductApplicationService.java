package kr.hhplus.be.server.controller.product.application;

import kr.hhplus.be.server.service.product.vo.ProductVO;

import kr.hhplus.be.server.service.product.vo.TopProductVO;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductApplicationService {

    List<ProductVO> getProduct(Pageable pageable);

    List<TopProductVO> getTopProducts();
}
