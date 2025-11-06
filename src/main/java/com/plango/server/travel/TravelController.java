package com.plango.server.travel;

import com.plango.server.travel.dto.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travels")
public class TravelController {

    private final TravelService travelService;

    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    //여행 생성 요청
    @PostMapping("/create")
    public String createTravel(@RequestBody TravelCreateRequest req) {
        return travelService.createTravel(req);
    }

    //모든 여행 간략하게 보여주기
    @GetMapping("/read/{publicId}")
    public List<TravelSummaryResponse> readAllTravel(@PathVariable String publicId) {
        return travelService.readAllTravel(publicId);
    }

    //특정 여행 정보 자세하게 보여주기
//    @GetMapping("/read/{travelId}")
//    public TravelDetailResponse readTravelDetail(@RequestParam String travelId) {
//        return travelService.readTravelDetial(travelId);
//    }
}
