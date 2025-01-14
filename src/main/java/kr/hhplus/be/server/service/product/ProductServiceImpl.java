package kr.hhplus.be.server.service.product;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.product.code.ProductStatus;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.service.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductVO> getProducts(Pageable pageable) {
        List<Product> products = productRepository.findAllByStatus(ProductStatus.SALE, pageable);

        return products.stream()
                .map(ProductVO::from)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> getProductByProductId(long id) {
        return productRepository.findById(id);
    }

}
