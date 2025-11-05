package com.plango.server.ai.dto;

public record AiHelloRequest(
    String nickname,
    boolean addJoke
){}