package com.plango.server.exception;

public class ApiNotWorkingException extends RuntimeException {
    public String where;
    public String message;
    public String errorMsg;
    public ApiNotWorkingException(String where, String message, String errorMsg) {
        this.where = where;
        this.message = message;
        this.errorMsg = errorMsg;
    }
    public String getWhere() {
        return where;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
