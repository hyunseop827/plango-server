package com.plango.server.ai.dto;

/**
 * Travel Creating DTO from Backend to AI API.
 * created in Backend, everything is converted to String.
 *
 * @param travelType
 * @param travelDest
 * @param startDate
 * @param endDate
 * @param theme1
 * @param theme2
 * @param theme3
 * @param userMbti
 * @param companionType
 */
public record AiTravelRequest(
        String travelType,
        String travelDest,
        String startDate,
        String endDate,
        String theme1,
        String theme2,
        String theme3,
        String userMbti,
        String companionType
) {
}
