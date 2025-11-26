package com.plango.server.exception;

/**
 * DTO for error response.
 *
 * @param code    response error code
 * @param message custom message
 */
public record ErrorResponse(String code, String message) {
}
