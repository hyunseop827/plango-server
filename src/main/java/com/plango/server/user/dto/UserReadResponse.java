package com.plango.server.user.dto;

/**
 * record class for user read their info
 * @param publicId
 * @param nickname
 * @param mbti
 */
public record UserReadResponse(
        String publicId,
        String nickname,
        String mbti
) {
}