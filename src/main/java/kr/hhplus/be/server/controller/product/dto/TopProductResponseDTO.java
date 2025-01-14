package kr.hhplus.be.server.controller.product.dto;

import kr.hhplus.be.server.service.product.vo.ProductVO;
import kr.hhplus.be.server.service.product.vo.TopProductVO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TopProductResponseDTO {

  private long id;
  private String name;
  private long salesCount;

  @Builder
  public TopProductResponseDTO(long id, String name, long salesCount) {
    this.id = id;
    this.name = name;
    this.salesCount = salesCount;
  }

  public static TopProductResponseDTO from(TopProductVO productVO) {
    return TopProductResponseDTO.builder()
        .id(productVO.getId())
        .name(productVO.getName())
        .salesCount(productVO.getSalesCount())
        .build();
  }
}
