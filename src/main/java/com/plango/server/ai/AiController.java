package com.plango.server.ai;

import com.plango.server.ai.dto.AiHelloResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 나중에 여행 정보 받으면 -> Aiservice -> travelService -> travelRepository
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    //NOTE AI 테스트
    @GetMapping("/{publicId}")
    public AiHelloResponse AiHi(@PathVariable String publicId) {
        return aiService.generateGreeting(publicId);
    }
}
