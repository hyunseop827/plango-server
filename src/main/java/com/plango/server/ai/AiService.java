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

/**
 * Service layer for AI generated travel.
 * Has objects for parsing, building, requesting.
 * This part has so many issues with AI generating,
 * the whole process is workable by the god
 */
@Service
public class AiService {

    private final UserService userService;
    private final AiHelloMapper aiHelloMapper;
    private final AiTravelMapper aiTravelMapper;
    private final ObjectMapper mapper;
    private final ChatClient chat;

    /**
     * Injecting objects to reach DB and other service layers
     * @param userService user service layer
     * @param aiHelloMapper Mapper
     * @param aiTravelMapper Mapper
     * @param mapper Object Mapper
     * @param chatClientBuilder prompts
     */
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

    /**
     * Searching user by user's public ID
     * Public ID is to identify certain user.
     * In function, it parses objects, build response and return result.
     * @param publicId
     * @return gretting with jokes
     */
    public AiHelloResponse generateGreeting(String publicId) {
        String nickname = userService.getUserNicknameByPublicId(publicId);

        AiHelloRequest req = aiHelloMapper.buildAiHelloRequest(nickname);
        String userJson = aiHelloMapper.buildUserJson(req);

        try{
            AiHelloResponse ai = chat
                    .prompt()
                    .system(aiHelloMapper.systemPromptJoke()) // inserting hard rule
                    .user(userJson) // 추가적인 요청사항
                    .options(ChatOptions.builder().temperature(0.1).build())
                    .call()
                    .entity(AiHelloResponse.class); // parsing JSON to AiHelloResponse

            return ai;
        }
        catch (Exception e){
            throw new ApiNotWorkingException("AiService","AI 인사 응답 오류",e.getMessage());
        }
    }

    /**
     * Returning AI generated travel request as Days
     * @param req Travel create reqeust
     * @return Input parameters for nomalize TravelDetailResponse.Days
     */
    public List<TravelDetailResponse.Days> generateTravel(TravelCreateRequest req) {

        // 1. processing user request to AI request
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


                // print out for logging
                String raw = response.content();
                System.out.println("===== AI travel raw response =====");
                System.out.println(raw);

                String json = extractJson(raw);
                System.out.println("===== AI travel cleaned json =====");
                System.out.println(json);


                AiTravelResponse detail = mapper.readValue(json, AiTravelResponse.class);

                return normalize(detail);
            }
            // AI API trows exceptions.
            catch (Exception e) {
                System.out.println("=== AI travel error === ai error");
                System.out.println("attempt = " + attempt);
                System.out.println("type    = " + e.getClass().getName());
                System.out.println("message = " + e.getMessage());

                String msg = e.getMessage();
                boolean retryable = false;

                if (msg != null) {
                    if (msg.contains("503") || msg.contains("502") || msg.contains("429")) {
                        retryable = true;
                    }
                }

                // defending exceptions and retry logic
                if (retryable && attempt < maxRetry) {
                    try {
                        Thread.sleep(300L * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                // by the time it crashes 3 times, I'm done with this shit
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

    // Normalize AI response
    public List<TravelDetailResponse.Days> normalize(AiTravelResponse res) {
        // 1. null check
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
                continue;
            }

            List<TravelDetailResponse.Course> outCourse = new ArrayList<>();
            int autoOrder = 1;

            // parsing AI Courses to User Courses.
            for (AiTravelResponse.AiCourse course : srcCourse) {
                if (course == null) continue;

                String locationName = course.getLocationName();
                if (locationName == null || locationName.isBlank()) {
                    continue;
                }

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

    /**
     * function for deleting ``` when AI generates response.
     * @param raw raw response from AI
     * @return
     */
    private String extractJson(String raw) {
        if (raw == null) {
            return null;
        }

        String cleaned = raw.trim();

        // 1 - if it starts with ```
        if (cleaned.startsWith("```")) {
            int firstNewline = cleaned.indexOf('\n');
            if (firstNewline != -1) {
                cleaned = cleaned.substring(firstNewline + 1);
            }

            int lastFence = cleaned.lastIndexOf("```");
            if (lastFence != -1) {
                cleaned = cleaned.substring(0, lastFence);
            }

            cleaned = cleaned.trim();
        }

        // 2 - if it has description in JSON
        int braceIdx   = cleaned.indexOf('{');
        int bracketIdx = cleaned.indexOf('[');

        int jsonStart = -1;
        if (braceIdx >= 0 && bracketIdx >= 0) {
            jsonStart = Math.min(braceIdx, bracketIdx);
        } else if (braceIdx >= 0) {
            jsonStart = braceIdx;
        } else if (bracketIdx >= 0) {
            jsonStart = bracketIdx;
        }

        if (jsonStart > 0) {
            cleaned = cleaned.substring(jsonStart);
        }

        return cleaned.trim();
    }

}