package com.docbrief.common;

import com.docbrief.common.resposne.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비교분석 처리 예외 핸들러
     * ComparisonProcessingException 발생 시 처리
     *
     * @param e ComparisonProcessingException
     * @return 에러 응답
     */
    @ExceptionHandler(ComparisonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleComparisonProcessingException(ComparisonProcessingException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("ComparisonProcessingException occurred. code={}, message={}", errorCode.getCode(), e.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode.getCode(), e.getMessage()));
    }

    /**
     * 일반 비즈니스 예외 핸들러
     * BusinessException의 서브클래스들을 처리
     *
     * @param e BusinessException
     * @return 에러 응답
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("BusinessException occurred. code={}, message={}", errorCode.getCode(), e.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode.getCode(), e.getMessage()));
    }

    /**
     * 처리되지 않은 모든 예외의 핸들러
     *
     * @param e Exception
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e){
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("Unhandled exception occurred. code={}, message={}", errorCode.getCode(), e.getMessage(), e);
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode.getCode(), e.getMessage()));
    }

}
