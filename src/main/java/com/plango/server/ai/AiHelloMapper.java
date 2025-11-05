package com.plango.server.ai;

import com.plango.server.ai.dto.AiHelloRequest;
import com.plango.server.ai.dto.AiHelloResponse;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

// FACADE 느낌 형태로 묶어서 다 해보자 일단
@Component
public class AiHelloMapper {

    // 객체 -> Json 파싱하는 오브젝트 매퍼
    private final ObjectMapper om;
    public AiHelloMapper(ObjectMapper om) { // 스프링이 관리하는 ObjectMapper 주입
        this.om = om;
    }

    // [서버 -> ai 요청]
    //들어온 이름을 AI 요청 객체로 만듦
    public AiHelloRequest buildAiHelloRequest(String nickname) {
        AiHelloRequest request = new AiHelloRequest(nickname,true);
        return request;
    }

    // 들어온 퍼플릭id를 AI에게 요청하는 객체로 만들고
    // 이 객체를 다시 json으로 쫙 뽑기
    //즉 지금 들어간 aiRequest name : String 반환
    public String buildUserJson(AiHelloRequest aiRequest) {
        try {
            // 디버깅용
            String pretty = om.writerWithDefaultPrettyPrinter().writeValueAsString(aiRequest);
            System.out.println(pretty);
            return om.writeValueAsString(aiRequest);
        } catch (Exception e) { throw new IllegalArgumentException(e); }
    }

    public String systemPromptJoke() {
        return """
            반드시 JSON만 출력하세요.
            출력 스키마(정확히 준수):
            { "msg": "string", "joke": "string" }
            
            규칙:
            - 다음에 올 user 역할 메시지는 입력 JSON입니다: 
            { "nickname": String, "addJoke": Boolean }
            - msg: nickname을 포함한 한국어 공손/친근 인사 한 문장.
            - addJoke가 true면 joke에 짧은 농담 한 문장, false면 빈 문자열("").
            - 다른 텍스트/마크다운/설명 금지.
        """;
    }
}
