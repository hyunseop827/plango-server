package com.plango.server.user.dto;

public record UserCreateRequest(
        String name, //사용자 닉네임
        String mbti //사용자 MBTI
) {}
