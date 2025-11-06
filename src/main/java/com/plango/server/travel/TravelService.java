package com.plango.server.travel;

import com.plango.server.ai.AiService;
import com.plango.server.travel.dto.CompanionType;
import com.plango.server.travel.dto.TravelCreateRequest;
import com.plango.server.travel.dto.TravelCreateResponse;
import com.plango.server.travel.dto.TravelType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import java.sql.Date;

import java.time.LocalDate;
import java.util.List;

@Service
public class TravelService {

    private final TravelRepository travelRepository;
    private final AiService aiService;

    public TravelService(TravelRepository travelRepository,
                         AiService aiService) {
        this.travelRepository = travelRepository;
        this.aiService = aiService;
    }

    // DB 저장 후 -> 해당 정보를 ai 인데
    // NOTE DB 저장만 하는 것을 일단 구현
    // Create
    @Transactional
    public TravelCreateResponse createTravel(@RequestBody TravelCreateRequest req) {
        String userId = req.userId();
        String travelType = req.travelType().name();
        String travelLocation = req.travelLocation();
        //Date 형태
        LocalDate start = LocalDate.parse(req.startDate());
        LocalDate end = LocalDate.parse(req.endDate());

        // LocalDate → java.sql.Date (JPA가 MySQL DATE로 자동 매핑)
        Date sqlStart = Date.valueOf(start);
        Date sqlEnd = Date.valueOf(end);
        List<String> themes = req.themes();
        String theme1 = themes.get(0);
        String theme2 = themes.get(1);
        String theme3 = themes.get(2);
        String companionType = req.companionType().name();

        TravelEntity travelEntity = new TravelEntity(
                userId, travelType, travelLocation,
                startDate,endDate,theme1,theme2,theme3,
                companionType
        );

    }


}
