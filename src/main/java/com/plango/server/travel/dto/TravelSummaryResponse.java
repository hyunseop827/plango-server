package com.plango.server.travel.dto;

import java.util.List;

/**
 * Travel response but in summary
 *
 * @param travelId
 * @param travelType
 * @param travelDest
 * @param startDate
 * @param endDate
 * @param themes
 * @param companionType
 */
public record TravelSummaryResponse(
        Long travelId,
        TravelType travelType,
        String travelDest,
        String startDate,
        String endDate,
        List<String> themes,
        CompanionType companionType
) {
}
