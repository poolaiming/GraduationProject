package com.example.newsplatform.service;

import java.util.Map;

public interface AdminStatisticsService {

    Map<String, Object> getDashboard();

    Map<String, Object> getInteractionTrend(int days);
}
