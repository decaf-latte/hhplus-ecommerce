package kr.hhplus.be.server.config.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseDTO<T> {

    private String message;
    private boolean isSuccess;
    private T data;

    @Builder
    public ResponseDTO(String message, boolean isSuccess, T data) {
        this.message = message;
        this.isSuccess = isSuccess;
        this.data = data;
    }

    public static <T> ResponseDTO<T> success(T data) {

        return ResponseDTO.<T>builder()
                .message("SUCCESS")
                .isSuccess(true)
                .data(data)
                .build();
    }

    public static <T> ResponseDTO<T> fail(T data) {

        return ResponseDTO.<T>builder()
                .message("FAIL")
                .isSuccess(false)
                .data(data)
                .build();
    }

    public static <T> ResponseDTO<T> fail(String message, T data) {

        return ResponseDTO.<T>builder()
                .message(message)
                .isSuccess(false)
                .data(data)
                .build();
    }
}

