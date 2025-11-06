package com.plango.server.travel.dto;

import java.util.List;

public record TravelCreateRequest(
        String userId, // 유저 id
        TravelType travelType, // 여행 타입
        String travelLocation, // 여행지
        String startDate, // 여행 시작일
        String endDate, //여행 종료일
        List<String> themes, //여행 테마 선택
        CompanionType companionType // 여행 동행자
) { }