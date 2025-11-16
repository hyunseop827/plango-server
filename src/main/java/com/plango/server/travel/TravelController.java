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
    public TravelDetailResponse createTravel(@RequestBody TravelCreateRequest req) {
        return travelService.createTravel(req);
    }

    //모든 여행 간략하게 보여주기c
    //앞으로 다가올 여행
    @GetMapping("/read/upcoming/{publicId}")
    public List<TravelSummaryResponse> readAllUpcomingTravel(@PathVariable String publicId) {
        return travelService.readAllUpcomingTravel(publicId);
    }

    //이미 지난 여행
    @GetMapping("/read/finished/{publicId}")
    public List<TravelSummaryResponse> readAllFinishedTravel(@PathVariable String publicId) {
        return travelService.readAllFinishedTravel(publicId);
    }

    //진행 중인 여행
    @GetMapping("/read/ongoing/{publicId}")
    public List<TravelSummaryResponse> readAllOngoingTravel(@PathVariable String publicId) {
        return travelService.readAllOngoingTravel(publicId);
    }

    //특정 여행 정보 자세하게 보여주기
    @GetMapping("/read/{travelId}")
    public TravelDetailResponse readTravelDetail(@PathVariable String travelId) {
        return travelService.readTravelDetail(travelId);
    }

    //여행 삭제 하기
    @DeleteMapping("/delete/{travelId}")
    public TravelDeleteResponse deleteTravel(@PathVariable String travelId) {
        return travelService.deleteTravel(travelId);
    }
}
