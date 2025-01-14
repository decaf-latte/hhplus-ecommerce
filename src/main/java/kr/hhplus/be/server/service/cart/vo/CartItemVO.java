package kr.hhplus.be.server.service.cart.vo;

import kr.hhplus.be.server.service.product.vo.ProductVO;
import kr.hhplus.be.server.service.user.vo.UserVO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartItemVO {

    private Long id;
    private UserVO user;
    private ProductVO product;
    private int quantity;
}
