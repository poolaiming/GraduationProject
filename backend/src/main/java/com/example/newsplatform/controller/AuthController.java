package com.example.newsplatform.controller;

import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String CAPTCHA_PREFIX = "captcha:";

    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;

    @GetMapping("/captcha")
    public ApiResponse<Map<String, String>> captcha() {
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        String key = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(CAPTCHA_PREFIX + key, code, 3, TimeUnit.MINUTES);

        String svg = "<svg xmlns='http://www.w3.org/2000/svg' width='120' height='40'>"
                + "<rect width='120' height='40' fill='#f5f7fa'/>"
                + "<text x='18' y='28' font-size='24' fill='#303133' font-family='Arial'>" + code + "</text>"
                + "</svg>";
        String captchaUrl = "data:image/svg+xml;base64," + Base64.getEncoder()
                .encodeToString(svg.getBytes(StandardCharsets.UTF_8));

        return ApiResponse.success(Map.of("captchaKey", key, "captchaUrl", captchaUrl));
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody RegisterRequest request) {
        validateCaptcha(request.getCaptchaKey(), request.getCaptchaCode());
        User user = userService.register(request.getUsername(), request.getPassword(), request.getNickname());
        return ApiResponse.success(user);
    }

    @PostMapping("/login")
    public ApiResponse<User> login(@RequestBody LoginRequest request) {
        validateCaptcha(request.getCaptchaKey(), request.getCaptchaCode());
        User user = userService.login(request.getUsername(), request.getPassword());
        return ApiResponse.success(user);
    }

    private void validateCaptcha(String captchaKey, String captchaCode) {
        if (!StringUtils.hasText(captchaKey) || !StringUtils.hasText(captchaCode)) {
            throw new IllegalArgumentException("验证码不能为空");
        }
        String redisKey = CAPTCHA_PREFIX + captchaKey;
        String expected = stringRedisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.hasText(expected)) {
            throw new IllegalArgumentException("验证码已过期，请刷新后重试");
        }
        if (!expected.equals(captchaCode.trim())) {
            throw new IllegalArgumentException("验证码错误");
        }
        stringRedisTemplate.delete(redisKey);
    }

    @Data
    private static class RegisterRequest {
        private String username;
        private String password;
        private String nickname;
        private String captchaKey;
        private String captchaCode;
    }

    @Data
    private static class LoginRequest {
        private String username;
        private String password;
        private String captchaKey;
        private String captchaCode;
    }
}
