package com.plango.server.ai.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plango.server.travel.dto.TravelCreateRequest;

public class AiTravelMapper {

    private final ObjectMapper mapper;
    public AiTravelMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

//    // 요청을 ai형태로 바꿈
//    public AiTravelRequest getTravelRequest(TravelCreateRequest req) {
//        AiTravelRequest aiTravelRequest = new AiTravelRequest()
//    }
}
