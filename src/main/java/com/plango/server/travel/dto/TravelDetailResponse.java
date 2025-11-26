package com.plango.server.travel.dto;

import java.util.List;

/**
 * Detailed travel plan response from back-end to front-end
 *
 * @param travelId
 * @param userPublicId
 * @param travelType
 * @param travelDest
 * @param startDate
 * @param endDate
 * @param themes
 * @param companionType
 * @param days
 * @param createdAt
 */
public record TravelDetailResponse(
        Long travelId,
        String userPublicId, // 사용자 id
        TravelType travelType,
        String travelDest,
        String startDate,
        String endDate,
        List<String> themes,
        CompanionType companionType,
        List<Days> days,
        String createdAt
) {
    /**
     * Each travel days.
     * @param dayIndex
     * @param courses
     */
    public record Days(
            int dayIndex,
            List<Course> courses
    ) {
    }

    /**
     * Each day plan has specific plans.
     * @param order
     * @param locationName
     * @param lat
     * @param lng
     * @param note
     * @param theme
     * @param howLong
     */
    public record Course(
            int order,
            String locationName,
            Double lat,
            Double lng,
            String note,
            String theme,
            Integer howLong
    ) {
    }
}
