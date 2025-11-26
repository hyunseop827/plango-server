package com.plango.server.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 404 -> no resource.
     * 502 -> service isn't working
     * @param ex exceptions.
     * @return response.
     */
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(DataNotFoundException ex) {
        String errMsg = String.format("에러 메시지: " + ex.getMessage() +"| 발생 위치: " + ex.getWhere() + " //로그: " +ex.getErrorMsg());
        return ResponseEntity.status(404)
                .body(new ErrorResponse("DATA_NOT_FOUND",errMsg));
    }

    @ExceptionHandler(ApiNotWorkingException.class)
    public ResponseEntity<ErrorResponse> handleNotWorking(ApiNotWorkingException ex) {
        String errMsg = String.format("에러 메시지: " + ex.getMessage() +"| 발생 위치: " + ex.getWhere() + " //로그: " +ex.getErrorMsg());
        return ResponseEntity.status(502)
                .body(new ErrorResponse("API_NOT_WORKING", errMsg));
    }
}

