package com.plango.server.ai;

import com.plango.server.ai.dto.*;
import com.plango.server.ai.mapper.*;
import com.plango.server.exception.ApiNotWorkingException;
import com.plango.server.travel.dto.*;
import com.plango.server.user.UserService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AiService {

    //NOTE AI 테스트
    private final UserService userService; //유저의 정보를 가져올 서비스
    private final AiHelloMapper aiHelloMapper; // 입력된 정보를 가공하고 객체 혹은 json으로 바꿀 매퍼
    private final AiTravelMapper aiTravelMapper;
    private final ChatClient chat; // AI 호츨

    // 생성자 주입
    public AiService(UserService userService,
                     AiHelloMapper aiHelloMapper,
                     AiTravelMapper aiTravelMapper,
                     ChatClient.Builder chatClientBuilder) {
        this.userService = userService;
        this.aiHelloMapper = aiHelloMapper;
        this.aiTravelMapper = aiTravelMapper;
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
                    .options(ChatOptions.builder().temperature(0.6).build())
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
    public List<TravelDetailResponse.Days> generateTravel(TravelCreateRequest req) {
        //여행 요청 데이터 가공
        String userMbti = userService.getUserMbtiByPublicId(req.userPublicId());
        AiTravelRequest aiTravelRequest = aiTravelMapper.translateAi(req, userMbti);
        String userJson = aiTravelMapper.buildUserJson(aiTravelRequest);
        try{
            AiTravelResponse detail = chat
                    .prompt()
                    .system(aiTravelMapper.systemPrompt())
                    .user(userJson)
                    .options(ChatOptions.builder().temperature(0.8).build())
                    .call()
                    .entity(AiTravelResponse.class);

            //응답을 깔끔하게 traveldetailresponse.day로 바꾸기
            // 추가되는 것은
            return normalize(detail);
        }
        catch (Exception e){
            throw new ApiNotWorkingException("AiService","AI 여행 생성 응답 오류",e.getMessage());
        }
    }

    //NOTE AI 응답 정규화
    public List<TravelDetailResponse.Days> normalize(AiTravelResponse res) {
        int count = res.days().size(); //총 몇일 짜리 인가..
        // 일자 먼저 생성
        List<AiTravelResponse.AiDay> src = res.days();
        List<TravelDetailResponse.Days> out = new ArrayList<>(count);

        int dayIndex = 1; //일수 정규화
        for(int i = 0 ; i < count ; i++){
            List<AiTravelResponse.AiCourse>  srcCourse = src.get(i).courses();
            List<TravelDetailResponse.Course> outCourse = new ArrayList<>();

            for(AiTravelResponse.AiCourse course : srcCourse){

                Integer order = course.order();
                String locationName = course.locationName();
                Double lat = course.lat();
                Double lng = course.lng();
                String note = course.note();
                String theme = course.theme();
                Integer howLong = course.howLong();

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
            out.add(new TravelDetailResponse.Days(
                    dayIndex++,
                    outCourse
            ));
        }

        return out;
    }
}