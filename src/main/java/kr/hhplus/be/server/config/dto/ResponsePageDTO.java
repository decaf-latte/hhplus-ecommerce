package kr.hhplus.be.server.config.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponsePageDTO<T> {

    private String message;
    private boolean isSuccess;
    private int page;
    private int size;
    private T data;

    @Builder
    public ResponsePageDTO(String message, boolean isSuccess, int page, int size, T data) {
        this.message = message;
        this.isSuccess = isSuccess;
        this.page = page;
        this.size = size;
        this.data = data;
    }

    public static <T> ResponsePageDTO<T> success(T data, int page, int size) {

        return ResponsePageDTO.<T>builder()
                .message("SUCCESS")
                .isSuccess(true)
                .page(page)
                .size(size)
                .data(data)
                .build();
    }

    public static <T> ResponsePageDTO<T> fail(T data) {

        return ResponsePageDTO.<T>builder().message("FAIL").isSuccess(false).data(data).build();
    }

    public static <T> ResponsePageDTO<T> fail(String message, T data) {

        return ResponsePageDTO.<T>builder().message(message).isSuccess(false).data(data).build();
    }
}
