package com.plango.server.user.dto;

/**
 * record class for user creation.
 *
 * @param nickname
 * @param mbti
 */
public record UserCreateRequest(
        String nickname,
        String mbti
) {
}
