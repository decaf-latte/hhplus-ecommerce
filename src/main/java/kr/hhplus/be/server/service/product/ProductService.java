package kr.hhplus.be.server.service.product;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.service.product.vo.ProductVO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;

public interface ProductService {

    List<ProductVO> getProducts(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> getProductByProductIdWithLock(long id);

}
