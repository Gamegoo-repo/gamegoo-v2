package com.gamegoo.gamegoo_v2.exception.common;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.gamegoo.gamegoo_v2.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ApiResponse<?>> globalException(GlobalException ex) {
        ApiResponse<?> errorResponse = ApiResponse.builder()
                .status(ex.getStatus())
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        ApiResponse<Object> errorResponse = ApiResponse
                .builder()
                .status(BAD_REQUEST)
                .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .code("VALID_ERROR")
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof MismatchedInputException mismatchedInputException) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.of(BAD_REQUEST,
                            mismatchedInputException.getPath().get(0).getFieldName() + " 필드의 값이 잘못되었습니다."));
        }

        return ResponseEntity.badRequest()
                .body(ApiResponse.of(BAD_REQUEST, "확인할 수 없는 형태의 데이터가 들어왔습니다"));
    }

}
