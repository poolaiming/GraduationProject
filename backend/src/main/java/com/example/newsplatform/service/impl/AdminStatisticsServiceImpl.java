package com.example.newsplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.newsplatform.entity.ForumComment;
import com.example.newsplatform.entity.ForumLike;
import com.example.newsplatform.entity.NewsCollect;
import com.example.newsplatform.entity.NewsComment;
import com.example.newsplatform.entity.NewsLike;
import com.example.newsplatform.mapper.*;
import com.example.newsplatform.service.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    private final UserMapper userMapper;
    private final NewsMapper newsMapper;
    private final NewsCategoryMapper newsCategoryMapper;
    private final ForumPostMapper forumPostMapper;
    private final NewsLikeMapper newsLikeMapper;
    private final ForumLikeMapper forumLikeMapper;
    private final NewsCollectMapper newsCollectMapper;
    private final NewsCommentMapper newsCommentMapper;
    private final ForumCommentMapper forumCommentMapper;

    @Override
    public Map<String, Object> getDashboard() {
        Map<String, Object> map = new HashMap<>();
        map.put("userCount", userMapper.selectCount(null));
        map.put("newsCount", newsMapper.selectCount(null));
        map.put("categoryCount", newsCategoryMapper.selectCount(null));
        map.put("forumPostCount", forumPostMapper.selectCount(null));
        map.put("userPublishedCount", newsMapper.selectCount(
                new LambdaQueryWrapper<com.example.newsplatform.entity.News>()
                        .eq(com.example.newsplatform.entity.News::getStatus, 2)
                        .isNotNull(com.example.newsplatform.entity.News::getAuthorId)));
        return map;
    }

    @Override
    public Map<String, Object> getInteractionTrend(int days) {
        int safeDays = Math.max(1, Math.min(days, 30));
        List<String> dates = new ArrayList<>();
        List<Long> likes = new ArrayList<>();
        List<Long> collects = new ArrayList<>();
        List<Long> comments = new ArrayList<>();

        LocalDate today = LocalDate.now();
        for (int i = safeDays - 1; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            LocalDateTime start = d.atStartOfDay();
            LocalDateTime end = d.plusDays(1).atStartOfDay();

            long newsLike = newsLikeMapper.selectCount(new LambdaQueryWrapper<NewsLike>()
                    .ge(NewsLike::getCreateTime, start)
                    .lt(NewsLike::getCreateTime, end));
            long forumLike = forumLikeMapper.selectCount(new LambdaQueryWrapper<ForumLike>()
                    .ge(ForumLike::getCreateTime, start)
                    .lt(ForumLike::getCreateTime, end));
            long collect = newsCollectMapper.selectCount(new LambdaQueryWrapper<NewsCollect>()
                    .ge(NewsCollect::getCreateTime, start)
                    .lt(NewsCollect::getCreateTime, end));
            long newsComment = newsCommentMapper.selectCount(new LambdaQueryWrapper<NewsComment>()
                    .ge(NewsComment::getCreateTime, start)
                    .lt(NewsComment::getCreateTime, end));
            long forumComment = forumCommentMapper.selectCount(new LambdaQueryWrapper<ForumComment>()
                    .ge(ForumComment::getCreateTime, start)
                    .lt(ForumComment::getCreateTime, end));

            dates.add(d.toString());
            likes.add(newsLike + forumLike);
            collects.add(collect);
            comments.add(newsComment + forumComment);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("dates", dates);
        map.put("likes", likes);
        map.put("collects", collects);
        map.put("comments", comments);
        return map;
    }
}
