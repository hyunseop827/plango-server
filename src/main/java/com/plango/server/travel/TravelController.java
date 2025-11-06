package com.plango.server.travel;

import com.plango.server.travel.dto.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/travels")
public class TravelController {

    private final TravelService travelService;

    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    //여행 정보 받기
    @GetMapping("/create")
    public TravelCreateResponse createTravel(@RequestBody TravelCreateRequest req) {
        return travelService.createTravel(req);
    }

}
