package kr.hhplus.be.server.service.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import kr.hhplus.be.server.domain.cart.entity.CartItem;
import kr.hhplus.be.server.domain.cart.repository.CartItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartItemServiceImpl cartItemService;

    @Test
    @DisplayName("장바구니 아이템을 ID로 가져옵니다")
    void getCartItemsByIds() {
        List<Long> cartItemIds = List.of(1L, 2L);
        CartItem item1 = CartItem.of().user(null).product(null).quantity(1).build();
        CartItem item2 = CartItem.of().user(null).product(null).quantity(2).build();
        when(cartItemRepository.findByUserWithLock(cartItemIds)).thenReturn(List.of(item1, item2));

        List<CartItem> result = cartItemService.getCartItemsByIds(cartItemIds);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getQuantity());
        assertEquals(2, result.get(1).getQuantity());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 장바구니 아이템을 가져올 때 빈 리스트를 반환합니다")
    void getCartItemsByIds_empty() {
        List<Long> cartItemIds = List.of(3L, 4L);
        when(cartItemRepository.findByUserWithLock(cartItemIds)).thenReturn(Collections.emptyList());

        List<CartItem> result = cartItemService.getCartItemsByIds(cartItemIds);

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("장바구니 아이템을 삭제합니다")
    void deleteCartItems() {
        CartItem item1 = CartItem.of().user(null).product(null).quantity(1).build();
        CartItem item2 = CartItem.of().user(null).product(null).quantity(2).build();
        List<CartItem> cartItems = List.of(item1, item2);

        cartItemService.deleteCartItems(cartItems);

        verify(cartItemRepository, times(1)).deleteAll(cartItems);
    }
}