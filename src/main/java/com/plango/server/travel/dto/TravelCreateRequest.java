package com.plango.server.travel.dto;

import java.util.List;

/**
 * TravelCreateRequest from front-end
 *
 * @param userPublicId  user's public ID
 * @param travelType
 * @param travelDest
 * @param startDate
 * @param endDate
 * @param themes
 * @param companionType
 */
public record TravelCreateRequest(
        String userPublicId,
        TravelType travelType,
        String travelDest,
        String startDate,
        String endDate,
        List<String> themes,
        CompanionType companionType
) {
}