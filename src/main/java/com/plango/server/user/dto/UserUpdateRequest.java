package com.plango.server.user.dto;

/**
 * DTO to update Userinfo
 * @param nickname
 * @param mbti
 */
public record UserUpdateRequest(
        String nickname,
        String mbti)
{}
