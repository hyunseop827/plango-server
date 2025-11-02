package com.plango.server.ai;

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
    //id 입력받으면 그냥 단순하게 입력
    @GetMapping("/{id}")
    public String hiAi(@PathVariable String id){
        return aiService.generateGreeting(id);
    }
}
