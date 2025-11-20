package com.plango.server.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plango.server.ai.dto.*;
import com.plango.server.ai.mapper.*;
import com.plango.server.exception.ApiNotWorkingException;
import com.plango.server.travel.dto.*;
import com.plango.server.user.UserService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import java.util.*;
import com.google.genai.errors.ServerException;

@Service
public class AiService {

    //NOTE AI 테스트
    private final UserService userService; //유저의 정보를 가져올 서비스
    private final AiHelloMapper aiHelloMapper; // 입력된 정보를 가공하고 객체 혹은 json으로 바꿀 매퍼
    private final AiTravelMapper aiTravelMapper;
    private final ObjectMapper mapper; // ← 추가
    private final ChatClient chat; // AI 호츨

    // 생성자 주입
    public AiService(UserService userService,
                     AiHelloMapper aiHelloMapper,
                     AiTravelMapper aiTravelMapper,
                     ObjectMapper mapper,
                     ChatClient.Builder chatClientBuilder) {
        this.userService = userService;
        this.aiHelloMapper = aiHelloMapper;
        this.aiTravelMapper = aiTravelMapper;
        this.mapper = mapper;
        this.chat = chatClientBuilder.build();
    }

    public AiHelloResponse generateGreeting(String publicId) {
        //이름 가져오기
        String nickname = userService.getUserNicknameByPublicId(publicId);

        //매퍼 (내부에서 유저 ID로 ai 요청 DTO 만들고, 해당 DTO를 json으로 빼기
        AiHelloRequest req = aiHelloMapper.buildAiHelloRequest(nickname);
        String userJson = aiHelloMapper.buildUserJson(req);

        try{
            AiHelloResponse ai = chat
                    .prompt()
                    .system(aiHelloMapper.systemPromptJoke()) //항상 지켜야 할 규칙 대입
                    .user(userJson) // 추가적인 요청사항
                    .options(ChatOptions.builder().temperature(0.1).build())
                    .call()
                    .entity(AiHelloResponse.class); // ← {"msg":"..."} 를 AiResponse로 매핑

            //DTO 객체 리턴
            return ai;
        }
        catch (Exception e){
            throw new ApiNotWorkingException("AiService","AI 인사 응답 오류",e.getMessage());
        }
    }

    //NOTE AI 여행 계획 리턴
    //NOTE AI 여행 계획 리턴
    public List<TravelDetailResponse.Days> generateTravel(TravelCreateRequest req) {
        // 1. 여행 요청 데이터 가공
        String userMbti = userService.getUserMbtiByPublicId(req.userPublicId());
        AiTravelRequest aiTravelRequest = aiTravelMapper.translateAi(req, userMbti);
        String userJson = aiTravelMapper.buildUserJson(aiTravelRequest);

        int maxRetry = 3;

        for (int attempt = 1; attempt <= maxRetry; attempt++) {
            try {
                var response = chat
                        .prompt()
                        .system(aiTravelMapper.systemPrompt())
                        .user(userJson)
                        .options(ChatOptions.builder().temperature(0.1).build())
                        .call();

                String raw = response.content();
                System.out.println("===== AI travel raw response =====");
                System.out.println(raw);

                AiTravelResponse detail = mapper.readValue(raw, AiTravelResponse.class);

                // 정상 응답이면 바로 리턴
                return normalize(detail);
            }
            catch (Exception e) {
                // 어떤 예외가 실제로 오는지 확인용 로그
                System.out.println("=== AI travel error === aierror");
                System.out.println("attempt = " + attempt);
                System.out.println("type    = " + e.getClass().getName());
                System.out.println("message = " + e.getMessage());

                String msg = e.getMessage();
                boolean retryable = false;

                if (msg != null) {
                    //get code and check
                    if (msg.contains("503") || msg.contains("502") || msg.contains("429")) {
                        retryable = true;
                    }
                }

                //retry
                if (retryable && attempt < maxRetry) {
                    try {
                        Thread.sleep(300L * attempt); // 간단한 backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                //iam done with this shit
                throw new ApiNotWorkingException(
                        "AiService",
                        "AI 여행 생성 실패 (attempt=" + attempt + ")",
                        e.getClass().getName() + " / " + e.getMessage()
                );
            }
        }

        //완전한 방어 코드
        throw new ApiNotWorkingException(
                "AiService",
                "AI 여행 생성 재시도 모두 실패",
                "maxRetry = " + maxRetry
        );
    }

    //NOTE AI 응답 정규화
    public List<TravelDetailResponse.Days> normalize(AiTravelResponse res) {
        // 1. 최상위 null 방어
        if (res == null || res.getDays() == null || res.getDays().isEmpty()) {
            throw new ApiNotWorkingException(
                    "AiService",
                    "AI 여행 생성 응답 비어있음",
                    "days 가 null 또는 empty"
            );
        }

        List<AiTravelResponse.AiDay> src = res.getDays();
        List<TravelDetailResponse.Days> out = new ArrayList<>(src.size());

        int dayIndex = 1;

        for (AiTravelResponse.AiDay day : src) {
            if (day == null) continue;

            List<AiTravelResponse.AiCourse> srcCourse = day.getCourses();
            if (srcCourse == null || srcCourse.isEmpty()) {
                // 해당 일차에 코스가 없으면 스킵
                continue;
            }

            List<TravelDetailResponse.Course> outCourse = new ArrayList<>();
            int autoOrder = 1; // order가 null/이상일 때 다시 부여할 순서

            for (AiTravelResponse.AiCourse course : srcCourse) {
                if (course == null) continue;

                // locationName 없으면 그 코스는 버린다
                String locationName = course.getLocationName();
                if (locationName == null || locationName.isBlank()) {
                    continue;
                }

                // order가 null이거나 0/음수면 autoOrder로 재계산
                Integer order = course.getOrder();
                if (order == null || order <= 0) {
                    order = autoOrder++;
                }

                Double lat = course.getLat();
                Double lng = course.getLng();

                String note = course.getNote();
                if (note == null) note = "";

                String theme = course.getTheme();
                if (theme == null) theme = "";

                Integer howLong = course.getHowLong();
                // howLong이 없거나 이상하면 기본 120분
                if (howLong == null || howLong <= 0) {
                    howLong = 120;
                }

                outCourse.add(new TravelDetailResponse.Course(
                        order,
                        locationName,
                        lat,
                        lng,
                        note,
                        theme,
                        howLong
                ));
            }

            // 이 일차에 유효한 코스가 하나라도 있으면 Days 추가
            if (!outCourse.isEmpty()) {
                out.add(new TravelDetailResponse.Days(
                        dayIndex++,
                        outCourse
                ));
            }
        }

        if (out.isEmpty()) {
            throw new ApiNotWorkingException(
                    "AiService",
                    "AI 여행 생성 응답 코스 없음",
                    "모든 day 가 비어있음"
            );
        }

        return out;
    }

}