package kr.hhplus.be.server.controller.exception;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.config.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseDTO<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseDTO.fail(e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseDTO<Object> handleIllegalStateException(IllegalStateException e) {
        return ResponseDTO.fail(e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({Exception.class, InterruptedException.class})
    public ResponseDTO<Object> handleException(Exception e) {
        return ResponseDTO.fail("내부적인 오류 발생.", null);
    }
}
