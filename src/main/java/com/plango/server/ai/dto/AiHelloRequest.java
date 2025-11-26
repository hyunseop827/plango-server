package com.plango.server.ai.dto;

/**
 * Define DTO from frontend
 * nickname and addjoke = true;
 *
 * @param nickname variable for username
 * @param addJoke  variable for boolean flag
 */
public record AiHelloRequest(
        String nickname,
        boolean addJoke
) {
}