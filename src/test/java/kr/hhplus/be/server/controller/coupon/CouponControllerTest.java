package kr.hhplus.be.server.controller.coupon;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.config.dto.ResponsePageDTO;
import kr.hhplus.be.server.controller.coupon.dto.CouponResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;


@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
class CouponControllerTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application")
            .withInitScript("init.sql");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("쿠폰 발급 성공")
    void issueCouponSuccess() throws Exception {

        long userId = 3L;
        String couponCode = "DISCOUNT10";

        // When & Then
        mockMvc.perform(post("/api/v1/coupons/issue")
                        .contentType("application/json")
                        .content(String.format("{\"userId\": %d, \"couponCode\": \"%s\"}", userId, couponCode)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("보유 쿠폰 목록 조회 테스트 - 페이징 적용")
    void getUserCouponsWithPaging() throws Exception {

        long userId = 1L;

        MvcResult result = mockMvc.perform(get("/api/v1/coupons/" + userId)
                        .param("page", "0") // 페이지 번호
                        .param("size", "10") // 페이지 크기
                        .param("sort", "couponName,asc")) // 정렬
                .andExpect(status().isOk())
                .andReturn();

        ResponsePageDTO<CouponResponseDTO> responsePageDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<ResponsePageDTO<CouponResponseDTO>>() {}
        );

        CouponResponseDTO response = responsePageDTO.getData();

        assertNotNull(response); // 응답이 null이 아닌지 확인
        assertFalse(response.getUserCoupons().isEmpty()); // 쿠폰 목록이 비어 있지 않은지 확인
        assertEquals(1, response.getUserCoupons().size()); // init.sql 기준, 보유 쿠폰이 1개인지 확인
        assertEquals("10% OFF", response.getUserCoupons().get(0).getCouponName()); // 쿠폰 이름 확인
    }
}