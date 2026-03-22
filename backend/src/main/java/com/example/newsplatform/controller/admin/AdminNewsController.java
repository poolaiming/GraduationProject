package com.example.newsplatform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.NewsTag;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.mapper.NewsTagMapper;
import com.example.newsplatform.service.NewsService;
import com.example.newsplatform.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

    private final NewsService newsService;
    private final NewsTagMapper newsTagMapper;
    private final UserService userService;

    @GetMapping("/page")
    public ApiResponse<Page<Map<String, Object>>> page(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                                       @RequestParam(value = "keyword", required = false) String keyword,
                                                       @RequestParam(value = "status", required = false) Integer status,
                                                       @RequestParam(value = "tagId", required = false) Long tagId,
                                                       @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) wrapper.eq(News::getCategoryId, categoryId);
        if (StringUtils.hasText(keyword)) wrapper.like(News::getTitle, keyword);
        if (status != null) wrapper.eq(News::getStatus, status);

        if (tagId != null) {
            List<Long> ids = newsTagMapper.selectList(new LambdaQueryWrapper<NewsTag>().eq(NewsTag::getTagId, tagId))
                    .stream().map(NewsTag::getNewsId).distinct().collect(Collectors.toList());
            if (ids.isEmpty()) {
                return ApiResponse.success(new Page<>(pageNum, pageSize, 0));
            }
            wrapper.in(News::getId, ids);
        }

        wrapper.orderByDesc(News::getCreateTime);
        Page<News> page = newsService.page(Page.of(pageNum, pageSize), wrapper);

        List<Long> authorIds = page.getRecords().stream().map(News::getAuthorId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> authorMap = userService.listByIds(authorIds).stream()
                .collect(Collectors.toMap(User::getId, u -> StringUtils.hasText(u.getNickname()) ? u.getNickname() : u.getUsername()));

        List<Map<String, Object>> records = page.getRecords().stream().map(n -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", n.getId());
            m.put("title", n.getTitle());
            m.put("categoryId", n.getCategoryId());
            m.put("authorId", n.getAuthorId());
            m.put("authorName", authorMap.getOrDefault(n.getAuthorId(), "--"));
            m.put("summary", n.getSummary());
            m.put("content", n.getContent());
            m.put("coverImage", n.getCoverImage());
            m.put("videoUrl", n.getVideoUrl());
            m.put("viewCount", n.getViewCount());
            m.put("collectCount", n.getCollectCount());
            m.put("likeCount", n.getLikeCount());
            m.put("status", n.getStatus());
            m.put("reviewRemark", n.getReviewRemark());
            m.put("publishTime", n.getPublishTime());
            m.put("createTime", n.getCreateTime());
            m.put("updateTime", n.getUpdateTime());
            return m;
        }).collect(Collectors.toList());

        Page<Map<String, Object>> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(records);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<News> detail(@PathVariable("id") Long id) {
        return ApiResponse.success(newsService.getById(id));
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> create(@RequestBody CreateNewsRequest request) {
        News news = new News();
        news.setTitle(request.getTitle());
        news.setCategoryId(request.getCategoryId());
        news.setAuthorId(request.getAuthorId());
        news.setSummary(request.getSummary());
        news.setContent(request.getContent());
        news.setCoverImage(request.getCoverImage());
        news.setVideoUrl(request.getVideoUrl());
        news.setStatus(request.getStatus() == null ? 2 : request.getStatus());
        news.setReviewRemark(request.getReviewRemark());
        if (news.getStatus() == 2) news.setPublishTime(LocalDateTime.now());

        boolean ok = newsService.save(news);
        if (ok && request.getTagIds() != null) {
            for (Long tagId : request.getTagIds()) {
                if (tagId == null) continue;
                NewsTag rel = new NewsTag();
                rel.setNewsId(news.getId());
                rel.setTagId(tagId);
                newsTagMapper.insert(rel);
            }
        }
        return ApiResponse.success(ok);
    }

    @PostMapping("/import")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Integer> importNews(@RequestBody List<CreateNewsRequest> list) {
        if (list == null || list.isEmpty()) return ApiResponse.success(0);
        int success = 0;
        for (CreateNewsRequest req : list) {
            if (!StringUtils.hasText(req.getTitle()) || !StringUtils.hasText(req.getContent())) continue;
            if (req.getAuthorId() == null) continue;
            create(req);
            success++;
        }
        return ApiResponse.success(success);
    }

    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> update(@PathVariable("id") Long id, @RequestBody UpdateNewsRequest request) {
        News existed = newsService.getById(id);
        if (existed == null) {
            throw new IllegalArgumentException("新闻不存在");
        }

        News news = new News();
        news.setId(id);
        news.setTitle(request.getTitle());
        news.setCategoryId(request.getCategoryId());
        news.setAuthorId(request.getAuthorId());
        news.setSummary(request.getSummary());
        news.setContent(request.getContent());
        news.setCoverImage(request.getCoverImage());
        news.setVideoUrl(request.getVideoUrl());
        news.setStatus(request.getStatus());
        news.setReviewRemark(request.getReviewRemark());
        if (request.getStatus() != null && request.getStatus() == 2 && existed.getPublishTime() == null) {
            news.setPublishTime(LocalDateTime.now());
        }

        boolean updated = newsService.updateById(news);
        if (updated) {
            newsTagMapper.delete(new LambdaQueryWrapper<NewsTag>().eq(NewsTag::getNewsId, id));
            if (request.getTagIds() != null) {
                for (Long tagId : request.getTagIds()) {
                    if (tagId == null) continue;
                    NewsTag rel = new NewsTag();
                    rel.setNewsId(id);
                    rel.setTagId(tagId);
                    newsTagMapper.insert(rel);
                }
            }
        }

        return ApiResponse.success(updated);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateStatus(@PathVariable("id") Long id,
                                             @RequestParam("status") Integer status,
                                             @RequestParam(value = "reviewRemark", required = false) String reviewRemark) {
        News news = new News();
        news.setId(id);
        news.setStatus(status);
        if (status == 2) {
            news.setPublishTime(LocalDateTime.now());
            news.setReviewRemark(null);
        }
        if (status == 3) {
            news.setReviewRemark(reviewRemark);
        }
        return ApiResponse.success(newsService.updateById(news));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") Long id) {
        return ApiResponse.success(newsService.removeById(id));
    }

    @Data
    private static class CreateNewsRequest {
        private String title;
        private Long categoryId;
        private Long authorId;
        private String summary;
        private String content;
        private String coverImage;
        private String videoUrl;
        private Integer status;
        private String reviewRemark;
        private List<Long> tagIds;
    }

    @Data
    private static class UpdateNewsRequest {
        private String title;
        private Long categoryId;
        private Long authorId;
        private String summary;
        private String content;
        private String coverImage;
        private String videoUrl;
        private Integer status;
        private String reviewRemark;
        private List<Long> tagIds;
    }
}
