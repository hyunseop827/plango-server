package com.plango.server.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Define return DTO from Backend, returning joke with username
 *
 * @param msg  short phrase of greeting message with username
 * @param joke AI generate joke
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AiHelloResponse(
        String msg,
        String joke
) {
}
