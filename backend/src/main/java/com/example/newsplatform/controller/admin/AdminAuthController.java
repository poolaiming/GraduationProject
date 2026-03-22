package com.example.newsplatform.controller.admin;

import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.Admin;
import com.example.newsplatform.service.AdminService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminService adminService;

    @PostMapping("/login")
    public ApiResponse<Admin> login(@RequestBody LoginRequest request) {
        Admin admin = adminService.login(request.getUsername(), request.getPassword());
        return ApiResponse.success(admin);
    }

    @Data
    private static class LoginRequest {
        private String username;
        private String password;
    }
}
