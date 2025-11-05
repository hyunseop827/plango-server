package com.plango.server.user.dto;

public record UserReadResponse(
        String publicId,
        String name,
        String mbti
) {
}
