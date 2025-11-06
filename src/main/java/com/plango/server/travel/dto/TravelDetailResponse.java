package com.plango.server.travel.dto;

import java.util.List;

public record TravelDetailResponse(
        String travelId, // 여행 순번
        String userPublicId, // 사용자 id
        TravelType travelType,
        String travelDest,
        String startDate,
        String endDate,
        List<String> themes,
        CompanionType companionType,
        List<Days> days, //여행 플랜에서 각 일차를 관리
        String createdAt // 언제 만든 여행 플랜인지
) {
    public record Days(
            int dayIndex, // 1일차, 2일차 .... n 일차
            List<Course> courses  //각 당일 코스
    ) {}
    public record Course(
            int order,     // 방문 순서 1... n
            String locationName, //해당 위치 이름
            Double lat, // 위도
            Double lng, // 경도
            String note, // 해당 위치 설명
            String theme, // 해당 위치에서 할 수 있는 테마
            Integer howLong // 해당 위치에서 ai가 정해준 충분한 시간
    ) {}
}
