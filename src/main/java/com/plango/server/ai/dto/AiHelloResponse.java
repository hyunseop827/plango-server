package com.plango.server.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiHelloResponse(
        String msg,
        String joke
){}
