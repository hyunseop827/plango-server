package com.plango.server.user;

public record UserCreateRequest(
        String name, //사용자 닉네임
        String mbti //사용자 MBTI
) {}
