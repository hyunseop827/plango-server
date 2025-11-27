package com.plango.server.travel;

import com.plango.server.travel.dto.TravelCreateRequest;
import com.plango.server.travel.dto.TravelDeleteResponse;
import com.plango.server.travel.dto.TravelDetailResponse;
import com.plango.server.travel.dto.TravelSummaryResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for travel url.
 */
@RestController
@RequestMapping("/api/travels")
public class TravelController {

    private final TravelService travelService;

    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    @PostMapping("/create")
    public TravelDetailResponse createTravel(@RequestBody TravelCreateRequest req) {
        return travelService.createTravel(req);
    }

    @GetMapping("/read/upcoming/{publicId}")
    public List<TravelSummaryResponse> readAllUpcomingTravel(@PathVariable String publicId) {
        return travelService.readAllUpcomingTravel(publicId);
    }

    @GetMapping("/read/finished/{publicId}")
    public List<TravelSummaryResponse> readAllFinishedTravel(@PathVariable String publicId) {
        return travelService.readAllFinishedTravel(publicId);
    }

    @GetMapping("/read/ongoing/{publicId}")
    public List<TravelSummaryResponse> readAllOngoingTravel(@PathVariable String publicId) {
        return travelService.readAllOngoingTravel(publicId);
    }

    @GetMapping("/read/{travelId}")
    public TravelDetailResponse readTravelDetail(@PathVariable String travelId) {
        return travelService.readTravelDetail(travelId);
    }

    @DeleteMapping("/delete/{travelId}")
    public TravelDeleteResponse deleteTravel(@PathVariable String travelId) {
        return travelService.deleteTravel(travelId);
    }

    @PutMapping("/retry/{travelId}")
    public TravelDetailResponse retryTravel(@PathVariable String travelId) {
        return travelService.retryTravel(travelId);
    }
}
