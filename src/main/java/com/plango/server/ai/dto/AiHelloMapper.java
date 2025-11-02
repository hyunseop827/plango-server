package com.plango.server.ai.dto;

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
    public AiHelloRequest buildAiHelloRequest(String userName) {
        AiHelloRequest request = new AiHelloRequest(userName,true);
        return request;
    }

    //aiRequest 객체를 다시 JSON으로 변환하는 함수
    //즉 지금 들어간 aiRequest name : String 반환
    public String buildUserJson(AiHelloRequest aiRequest) {
        try {
            // 디버깅용
            String pretty = om.writerWithDefaultPrettyPrinter().writeValueAsString(aiRequest);
            System.out.println(pretty);
            return om.writeValueAsString(aiRequest); }
        catch (Exception e) { throw new IllegalArgumentException(e); }
    }

    public String systemPrompt() {
        return """
          반드시 JSON만 출력하세요.
          출력 스키마(정확히 준수):
          { "msg": "string", "joke": "string" }
          규칙:
          - user JSON에는 name, addJoke(boolean)이 옵니다.
          - msg: 한국어 공손/친근 인사 한 문장.
          - addJoke가 true면 joke에 '아주 짧은' 농담 한 문장, false면 빈 문자열("")을 넣으세요.
          - 다른 텍스트/마크다운/설명 금지.
        """;
    }

    // [ai -> 서버]
    public String toPlainText(AiHelloResponse ai) {
        return ai.msg();
    }
}
