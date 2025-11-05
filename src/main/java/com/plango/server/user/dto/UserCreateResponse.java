package com.plango.server.user.dto;

public record UserCreateResponse(
        String publicId,
        String nickname,
        String mbti
) {}
