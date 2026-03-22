package com.example.newsplatform.controller;

import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PutMapping("/profile")
    public ApiResponse<User> updateProfile(@RequestBody UpdateProfileRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        User user = new User();
        user.setId(request.getUserId());
        user.setNickname(request.getNickname());
        user.setAvatar(request.getAvatar());
        userService.updateById(user);

        User latest = userService.getById(request.getUserId());
        if (latest != null) {
            latest.setPassword(null);
        }
        return ApiResponse.success(latest);
    }

    @PutMapping("/password")
    public ApiResponse<Boolean> changePassword(@RequestBody ChangePasswordRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (!StringUtils.hasText(request.getOldPassword()) || !StringUtils.hasText(request.getNewPassword())) {
            throw new IllegalArgumentException("原密码和新密码不能为空");
        }

        User user = userService.getById(request.getUserId());
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new IllegalArgumentException("账号不存在或已被禁用");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("原密码不正确");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        boolean ok = userService.updateById(user);
        return ApiResponse.success(ok);
    }

    @PutMapping("/cancel-request")
    public ApiResponse<Boolean> requestCancelAccount(@RequestBody CancelAccountRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return ApiResponse.success(userService.requestAccountCancellation(request.getUserId()));
    }

    /**
     * 提交新闻工作者申请（2=待审核）
     */
    @PostMapping("/journalist/register")
    public ApiResponse<Boolean> registerJournalist(@RequestBody RegisterJournalistRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        User user = userService.getById(request.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new IllegalArgumentException("账号状态异常，无法申请");
        }
        if (user.getIsJournalist() != null && user.getIsJournalist() == 1) {
            throw new IllegalArgumentException("你已是新闻工作者，无需重复申请");
        }
        if (user.getIsJournalist() != null && user.getIsJournalist() == 2) {
            throw new IllegalArgumentException("申请已提交，请等待管理员审核");
        }

        User updating = new User();
        updating.setId(request.getUserId());
        updating.setIsJournalist(2);
        return ApiResponse.success(userService.updateById(updating));
    }

    @Data
    private static class UpdateProfileRequest {
        private Long userId;
        private String nickname;
        private String avatar;
    }

    @Data
    private static class ChangePasswordRequest {
        private Long userId;
        private String oldPassword;
        private String newPassword;
    }

    @Data
    private static class CancelAccountRequest {
        private Long userId;
    }

    @Data
    private static class RegisterJournalistRequest {
        private Long userId;
    }
}
