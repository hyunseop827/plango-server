package com.plango.server.travel;

import com.plango.server.ai.AiService;
import com.plango.server.travel.dto.TravelCreateRequest;
import com.plango.server.travel.dto.TravelSummaryResponse;
import com.plango.server.travel.entity.TravelEntity;
import com.plango.server.user.UserEntity;
import com.plango.server.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDate;
import java.util.List;

@Service
public class TravelService {

    private final TravelRepository travelRepository;
    private final UserService userService;
    private final AiService aiService;

    public TravelService(TravelRepository travelRepository,
                         AiService aiService, UserService userService) {
        this.travelRepository = travelRepository;
        this.aiService = aiService;
        this.userService = userService;
    }

    // DB 저장 후 -> 해당 정보를 ai 인데
    // NOTE DB 저장만 하는 것을 일단 구현 -> 그냥 단순 문자열 리턴
    // Create
    @Transactional
    public String createTravel(@RequestBody TravelCreateRequest req) {
        String publicId = req.userPublicId();
        // 유저 찾기
        UserEntity ue = userService.getUserEntityByPublicId(publicId);
        //목적지 추출
        String travelDest = req.travelDest();
        //Date 형태 변환
        LocalDate start = LocalDate.parse(req.startDate());
        LocalDate end = LocalDate.parse(req.endDate());
        // 테마 추출
        List<String> themes = req.themes();
        String theme1 = themes.get(0);
        String theme2 = themes.get(1);
        String theme3 = themes.get(2);

        TravelEntity travelEntity = new TravelEntity(
                ue, travelDest, start,end,
                req.travelType(), req.companionType(), theme1,
                theme2, theme3
        );

        travelRepository.saveAndFlush(travelEntity);

        return "완료";
    }

    //Read 모든 여행
    public List<TravelSummaryResponse> readAllTravel(String publicId) {
        //유저의 내부 키 빠르게 찾기
        UserEntity ue = userService.getUserEntityByPublicId(publicId);
        List<TravelEntity> travels = travelRepository.findByUser(ue);

        return travels.stream()
                .map(t -> new TravelSummaryResponse(
                        t.getTravelId(),
                        t.getTravelType(),
                        t.getTravelDest(),
                        t.getTravelStart().toString(),
                        t.getTravelEnd().toString(),
                        List.of(t.getTravelTheme1(), t.getTravelTheme2(), t.getTravelTheme3()),
                        t.getCompanionType()
                ))
                .toList();
    }


}
