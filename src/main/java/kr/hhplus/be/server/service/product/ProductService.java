package kr.hhplus.be.server.service.product;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.service.product.vo.ProductVO;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductVO> getProducts(Pageable pageable);

    Optional<Product> getProductByProductId (long id);

}
