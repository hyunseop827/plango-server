package com.plango.server.user;

import com.plango.server.user.dto.UserCreateRequest;
import com.plango.server.user.dto.UserCreateResponse;
import com.plango.server.user.dto.UserReadResponse;
import org.springframework.web.bind.annotation.*;

@RestController //해당 어노테이션 붙으면 알아서 자동으로 JSON 직렬화 및 역직렬화함
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 임의 키 부여 -> DB 저장 -> DTO 리턴
    // 유저 저장
    @PostMapping("/custom")
    public UserCreateResponse registerUser(@RequestBody UserCreateRequest req) {
        String publicId = userService.createUser(req);
        return new UserCreateResponse(req.nickname(), req.mbti(), publicId);
    }

    // 유저 불러오기
    @GetMapping("/{publicId}")
    public UserReadResponse getUser(@PathVariable String publicId) {
        return userService.getUserByPublicId(publicId);
    }
}