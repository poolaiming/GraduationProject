package com.example.newsplatform.controller.admin;

import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.service.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final AdminStatisticsService statisticsService;

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return ApiResponse.success(statisticsService.getDashboard());
    }

    @GetMapping("/interaction-trend")
    public ApiResponse<Map<String, Object>> interactionTrend(@RequestParam(value = "days", defaultValue = "7") int days) {
        return ApiResponse.success(statisticsService.getInteractionTrend(days));
    }
}
