package com.plango.server.ai.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plango.server.ai.dto.AiHelloRequest;
import org.springframework.stereotype.Component;

/**
 * Facade pattern.
 * Mapping AiHelloRequest to AiHelloResponse
 * Using object mapper to parse Object to JSON.
 */
@Component
public class AiHelloMapper {

    /**
     * Object Mapper. Injected in constructor.
     */
    private final ObjectMapper om;

    /**
     * Constructor.
     * @param om ObjectMapper.
     */
    public AiHelloMapper(ObjectMapper om) { // 스프링이 관리하는 ObjectMapper 주입
        this.om = om;
    }


    /**
     * Server to AI request. Convert nickname to AiHelloRequest
     * @param nickname
     * @return AiHelloRequest (addJoke = always true)
     */
    public AiHelloRequest buildAiHelloRequest(String nickname) {
        AiHelloRequest request = new AiHelloRequest(nickname, true);
        return request;
    }

    /**
     * Converting DTO as String
     * @param aiRequest
     * @return String
     */
    public String buildUserJson(AiHelloRequest aiRequest) {
        try {
            // Debug.
            String pretty = om.writerWithDefaultPrettyPrinter().writeValueAsString(aiRequest);
            System.out.println(pretty);
            return om.writeValueAsString(aiRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return system prompt.
     */
    public String systemPromptJoke() {
        return """
                    반드시 JSON만 출력하세요.
                    출력 스키마(정확히 준수):
                    { "msg": "string", "joke": "string" }
                
                    규칙:
                    - 다음에 올 user 역할 메시지는 입력 JSON입니다: 
                    { "nickname": String, "addJoke": Boolean }
                    - msg: nickname을 포함한 한국어 공손/친근 인사 한 문장.
                    - addJoke가 true면 joke에 랜덤한 상식 혹은 농담 한 문장, false면 빈 문자열("").
                    - 다른 텍스트/마크다운/설명 금지.
                """;
    }
}
