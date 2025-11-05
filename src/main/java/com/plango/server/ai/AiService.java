package com.plango.server.ai;

import com.plango.server.ai.dto.AiHelloRequest;
import com.plango.server.ai.dto.AiHelloResponse;
import com.plango.server.user.UserService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    //NOTE AI 테스트
    private final UserService userService; //유저의 정보를 가져올 서비스
    private final AiHelloMapper aiHelloMapper; // 입력된 정보를 가공하고 객체 혹은 json으로 바꿀 매퍼
    private final ChatClient chat; // AI 호츨

    // 생성자 주입
    public AiService(UserService userService,
                     AiHelloMapper aiHelloMapper,
                     ChatClient.Builder chatClientBuilder) {
        this.userService = userService;
        this.aiHelloMapper = aiHelloMapper;
        this.chat = chatClientBuilder.build();
    }

    public AiHelloResponse generateGreeting(String publicId){
        //이름 가져오기
        String nickname = userService.getUserNicknameByPublicId(publicId);

        //매퍼 (내부에서 유저 ID로 ai 요청 DTO 만들고, 해당 DTO를 json으로 빼기
        AiHelloRequest req = aiHelloMapper.buildAiHelloRequest(nickname);
        String userJson = aiHelloMapper.buildUserJson(req);

        AiHelloResponse ai = chat
                .prompt()
                .system(aiHelloMapper.systemPromptJoke()) //항상 지켜야 할 규칙 대입
                .user(userJson) // 추가적인 요청사항
                .options(ChatOptions.builder().temperature(0.4).build())
                .call()
                .entity(AiHelloResponse.class); // ← {"msg":"..."} 를 AiResponse로 매핑

        System.out.println(ai);
        //DTO 객체 리턴
        return ai;
    }


}
