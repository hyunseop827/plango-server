package com.plango.server.ai.dto;

public record AiHelloRequest(
    String name,
    boolean addJoke
)
{}