package kr.hhplus.be.server.scheduler.product;

import kr.hhplus.be.server.controller.product.application.ProductApplicationService;
import kr.hhplus.be.server.service.product.vo.TopProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductScheduler {

    private final ProductApplicationService productApplicationService;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 00:00 실행
    @CachePut(value = "topProducts", key = "'top_5_products'") // 캐시 갱신
    public List<TopProductVO> updateTopProductsCache() {
        log.info(" [스케줄러 실행] 3일간 최다 판매 상품을 Redis에 저장합니다.");

        // 1 최근 3일간 최다 판매 상품 조회
        List<TopProductVO> topProducts = productApplicationService.getTopProducts();

        if (topProducts == null || topProducts.isEmpty()) {
            log.error("최근 3일간 판매된 상품이 없습니다.");
            return new ArrayList<>();
        }

        log.info(" [스케줄러 완료] 최다 판매 상품 5개가 Redis에 저장되었습니다.");

        return topProducts;
    }

}
