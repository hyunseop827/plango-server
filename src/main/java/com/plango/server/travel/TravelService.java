package com.plango.server.travel;

import com.plango.server.ai.AiService;
import com.plango.server.exception.DataNotFoundException;
import com.plango.server.travel.dto.TravelCreateRequest;
import com.plango.server.travel.dto.TravelDeleteResponse;
import com.plango.server.travel.dto.TravelDetailResponse;
import com.plango.server.travel.dto.TravelSummaryResponse;
import com.plango.server.travel.entity.TravelCourseEntity;
import com.plango.server.travel.entity.TravelDayEntity;
import com.plango.server.travel.entity.TravelEntity;
import com.plango.server.travel.repos.TravelCourseRepository;
import com.plango.server.travel.repos.TravelDayRepository;
import com.plango.server.travel.repos.TravelRepository;
import com.plango.server.user.UserEntity;
import com.plango.server.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for travel.
 * Finding and processing data by searching with user Public ID
 */
@Service
public class TravelService {

    private final TravelRepository travelRepository;
    private final TravelDayRepository travelDayRepository;
    private final TravelCourseRepository travelCourseRepository;
    private final UserService userService;
    private final AiService aiService;

    public TravelService(TravelRepository travelRepository,
                         TravelDayRepository travelDayRepository,
                         TravelCourseRepository travelCourseRepository,
                         AiService aiService,
                         UserService userService) {
        this.travelRepository = travelRepository;
        this.travelDayRepository = travelDayRepository;
        this.travelCourseRepository = travelCourseRepository;
        this.aiService = aiService;
        this.userService = userService;
    }

    // upcoming travels
    @Transactional(readOnly = true)
    public List<TravelSummaryResponse> readAllUpcomingTravel(String publicId) {
        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        UserEntity ue = userService.getUserEntityByPublicId(publicId);
        List<TravelEntity> travels =
                travelRepository.findByUserAndTravelStartAfterOrderByTravelStartAsc(ue, today);

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

    // past travel
    @Transactional(readOnly = true)
    public List<TravelSummaryResponse> readAllFinishedTravel(String publicId) {
        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        UserEntity ue = userService.getUserEntityByPublicId(publicId);
        List<TravelEntity> travels =
                travelRepository.findByUserAndTravelEndBeforeOrderByTravelEndDesc(ue, today);

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

    // travel in progress
    @Transactional(readOnly = true)
    public List<TravelSummaryResponse> readAllOngoingTravel(String publicId) {
        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        UserEntity ue = userService.getUserEntityByPublicId(publicId);
        List<TravelEntity> travels =
                travelRepository.findByUserAndTravelStartLessThanEqualAndTravelEndGreaterThanEqualOrderByTravelStartAsc(ue, today, today);

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

    // read certain travel
    @Transactional(readOnly = true)
    public TravelDetailResponse readTravelDetail(String travelId) {
        Long t_id = Long.parseLong(travelId);
        Optional<TravelEntity> travel = travelRepository.findByTravelId(t_id);
        if (travel.isPresent()) {
            TravelEntity t = travel.get();
            List<TravelDetailResponse.Days> days = t.getTravelDays().stream()
                    .sorted(Comparator.comparingInt(TravelDayEntity::getTravelDayIndex))
                    .map(d -> new TravelDetailResponse.Days(
                            d.getTravelDayIndex(),
                            // Course -> DTO
                            d.getCourses().stream()
                                    .sorted(Comparator.comparingInt(TravelCourseEntity::getLocationOrder))
                                    .map(c -> new TravelDetailResponse.Course(
                                            c.getLocationOrder(),
                                            c.getLocationName(),
                                            c.getLocationLat(),
                                            c.getLocationLng(),
                                            c.getLocationNote(),
                                            c.getLocationTheme(),
                                            c.getLocationTime()
                                    ))
                                    .toList()
                    ))
                    .toList();
            return new TravelDetailResponse(
                    t.getTravelId(),
                    t.getUser().getPublicId(),
                    t.getTravelType(),
                    t.getTravelDest(),
                    t.getTravelStart().toString(),
                    t.getTravelEnd().toString(),
                    List.of(t.getTravelTheme1(), t.getTravelTheme2(), t.getTravelTheme3()),
                    t.getCompanionType(),
                    days,
                    t.getCreatedDate().toString()
            );
        } else throw new DataNotFoundException("TravelService", "해당 여행 정보 없음", "");
    }


    /**
     * Create Travel, insert record and returning it as user level DTO
     *
     * @param req creating request.
     * @return
     */
    @Transactional
    public TravelDetailResponse createTravel(TravelCreateRequest req) {
        List<TravelDetailResponse.Days> days = aiService.generateTravel(req);

        // Insert record in database - 1
        String publicId = req.userPublicId();
        UserEntity ue = userService.getUserEntityByPublicId(publicId);
        String travelDest = req.travelDest();
        LocalDate start = LocalDate.parse(req.startDate());
        LocalDate end = LocalDate.parse(req.endDate());
        List<String> themes = req.themes();
        String theme1 = themes.get(0);
        String theme2 = themes.get(1);
        String theme3 = themes.get(2);

        TravelEntity travelEntity = new TravelEntity(
                ue, travelDest, start, end,
                req.travelType(), req.companionType(), theme1,
                theme2, theme3
        );

        travelRepository.saveAndFlush(travelEntity);

        // Insert record in database - 2
        for (TravelDetailResponse.Days d : days) {
            TravelDayEntity day = new TravelDayEntity(travelEntity, d.dayIndex());
            travelDayRepository.save(day);

            for (TravelDetailResponse.Course c : d.courses()) {
                TravelCourseEntity course = new TravelCourseEntity(
                        day,
                        c.locationName(),
                        c.lat(), c.lng(),
                        c.note(), c.theme(),
                        c.howLong(), c.order()
                );
                travelCourseRepository.save(course);
            }
        }

        // returning and parsing user DTO
        return new TravelDetailResponse(
                travelEntity.getTravelId(),
                req.userPublicId(),
                req.travelType(),
                req.travelDest(),
                req.startDate(),
                req.endDate(),
                req.themes(),
                req.companionType(),
                days,
                travelEntity.getCreatedDate().toString()
        );
    }

    // Delete
    public TravelDeleteResponse deleteTravel(String travelId) {
        TravelDeleteResponse response = new TravelDeleteResponse("deleted", travelId);

        Optional<TravelEntity> travelEntity = travelRepository.findByTravelId(Long.valueOf(travelId));
        if (travelEntity.isPresent()) {
            TravelEntity t = travelEntity.get();
            travelRepository.delete(t);

            return response;
        } else throw new DataNotFoundException("TravelService", "해당 여행 정보 없음", "");
    }

    /**
     * If user does not like what AI says, user can retry.
     * Delete records and generate again.
     */
    @Transactional
    public TravelDetailResponse retryTravel(String travelId) {
        Long id = Long.valueOf(travelId);
        Optional<TravelEntity> certainTravel = travelRepository.findByTravelId(id);

        if (certainTravel.isPresent()) {
            TravelEntity t = certainTravel.get();

            TravelCreateRequest req = new TravelCreateRequest
                    (t.getUser().getPublicId(),
                            t.getTravelType(),
                            t.getTravelDest(),
                            t.getTravelStart().toString(),
                            t.getTravelEnd().toString(),
                            List.of(t.getTravelTheme1(),
                                    t.getTravelTheme2(),
                                    t.getTravelTheme3()),
                            t.getCompanionType()
                    );

            // DB
            t.getTravelDays().clear();
            travelRepository.flush();

            // generate new plan
            List<TravelDetailResponse.Days> days = aiService.generateTravel(req);

            for (TravelDetailResponse.Days d : days) {
                TravelDayEntity day = new TravelDayEntity(t, d.dayIndex());
                travelDayRepository.save(day);

                for (TravelDetailResponse.Course c : d.courses()) {
                    TravelCourseEntity course = new TravelCourseEntity(
                            day, c.locationName(), c.lat(), c.lng(),
                            c.note(), c.theme(), c.howLong(), c.order()
                    );
                    travelCourseRepository.save(course);
                }
            }
            
            return new TravelDetailResponse(
                    t.getTravelId(),
                    req.userPublicId(),
                    req.travelType(),
                    req.travelDest(),
                    req.startDate(),
                    req.endDate(),
                    req.themes(),
                    req.companionType(),
                    days,
                    t.getCreatedDate().toString()
            );

        } else throw new DataNotFoundException("TravelService", "해당 여행 정보 없음", "");
    }

}
