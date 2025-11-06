package com.plango.server.ai.dto;

public record AiTravelRequest(
        String travelType,
        String travelDest,
        String startDate,
        String endDate,
        String theme1,
        String theme2,
        String theme3,
        String companionType,
        String userMbti
){}
