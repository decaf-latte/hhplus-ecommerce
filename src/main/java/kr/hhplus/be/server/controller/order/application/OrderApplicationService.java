package kr.hhplus.be.server.controller.order.application;

import java.util.List;
import kr.hhplus.be.server.service.order.vo.OrderVO;

public interface OrderApplicationService {

    OrderVO payOrder(List<Long> cartItemIds, Long userCouponId);
}
