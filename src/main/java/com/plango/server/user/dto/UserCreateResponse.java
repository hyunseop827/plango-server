package com.plango.server.user.dto;

/**
 * record class for user creationg response.
 *
 * @param publicId user's public ID
 * @param nickname
 * @param mbti
 */
public record UserCreateResponse(
        String publicId,
        String nickname,
        String mbti
) {
}
