package com.plango.server.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 나중에 앱에서 리턴 받을 때 만약 코드가 404라면 오류임을 처리하면 된다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    //404 -> 리소스 없음
    //502 -> 외부 API 불가
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(DataNotFoundException ex) {
        String errMsg = String.format("에러 메시지: " + ex.getMessage() +" //발생 위치: " + ex.getWhere() + " //로그: " +ex.getErrorMsg());
        return ResponseEntity.status(404)
                .body(new ErrorResponse("DATA_NOT_FOUND",errMsg));
    }

    @ExceptionHandler(ApiNotWorkingException.class)
    public ResponseEntity<ErrorResponse> handleNotWorking(ApiNotWorkingException ex) {
        String errMsg = String.format("에러 메시지: " + ex.getMessage() +" //발생 위치: " + ex.getWhere() + " //로그: " +ex.getErrorMsg());
        return ResponseEntity.status(502)
                .body(new ErrorResponse("API_NOT_WORKING", errMsg));
    }
}

