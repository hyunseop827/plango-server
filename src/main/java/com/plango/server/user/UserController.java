package com.plango.server.user;

import com.plango.server.user.dto.UserCreateRequest;
import com.plango.server.user.dto.UserCreateResponse;
import com.plango.server.user.dto.UserReadResponse;
import com.plango.server.user.dto.UserUpdateRequest;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user layer.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/custom")
    public UserCreateResponse registerUser(@RequestBody UserCreateRequest req) {
        String publicId = userService.createUser(req);
        return new UserCreateResponse(publicId, req.nickname(), req.mbti());
    }

    @GetMapping("/{publicId}")
    public UserReadResponse getUser(@PathVariable String publicId) {
        return userService.getUserByPublicId(publicId);
    }

    @PutMapping("/{publicId}")
    public UserReadResponse updateUser(@PathVariable String publicId, @RequestBody UserUpdateRequest req) {
        return userService.updateUserByPublicId(publicId,req);
    }
}