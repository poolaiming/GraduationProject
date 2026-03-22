package com.example.newsplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.*;
import com.example.newsplatform.mapper.*;
import com.example.newsplatform.service.NewsCategoryService;
import com.example.newsplatform.service.NewsService;
import com.example.newsplatform.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final NewsCategoryService newsCategoryService;
    private final UserService userService;
    private final NewsLikeMapper newsLikeMapper;
    private final NewsCollectMapper newsCollectMapper;
    private final NewsCommentMapper newsCommentMapper;
    private final BrowseHistoryMapper browseHistoryMapper;
    private final TagMapper tagMapper;
    private final NewsTagMapper newsTagMapper;

    @GetMapping("/page")
    public ApiResponse<Page<News>> page(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                        @RequestParam(value = "authorId", required = false) Long authorId,
                                        @RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "authorName", required = false) String authorName,
                                        @RequestParam(value = "tagId", required = false) Long tagId,
                                        @RequestParam(value = "sortBy", required = false) String sortBy,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return ApiResponse.success(newsService.pageNews(categoryId, authorId, keyword, authorName, tagId, sortBy, pageNum, pageSize));
    }

    @GetMapping("/tags")
    public ApiResponse<List<Tag>> tags() {
        return ApiResponse.success(tagMapper.selectList(new LambdaQueryWrapper<Tag>().orderByAsc(Tag::getId)));
    }

    @GetMapping("/categories")
    public ApiResponse<List<NewsCategory>> categories() {
        return ApiResponse.success(newsCategoryService.list(
                new LambdaQueryWrapper<NewsCategory>().eq(NewsCategory::getStatus, 1).orderByAsc(NewsCategory::getSort).orderByAsc(NewsCategory::getId)
        ));
    }

    @GetMapping("/{id}/tags")
    public ApiResponse<List<Tag>> tagsByNews(@PathVariable("id") Long id) {
        List<Long> tagIds = newsTagMapper.selectList(new LambdaQueryWrapper<NewsTag>().eq(NewsTag::getNewsId, id))
                .stream().map(NewsTag::getTagId).distinct().collect(Collectors.toList());
        if (tagIds.isEmpty()) return ApiResponse.success(Collections.emptyList());
        return ApiResponse.success(tagMapper.selectBatchIds(tagIds));
    }

    @GetMapping("/{id}")
    public ApiResponse<News> detail(@PathVariable("id") Long id,
                                    @RequestParam(value = "userId", required = false) Long userId) {
        newsService.increaseViewCount(id);
        if (userId != null) {
            BrowseHistory history = new BrowseHistory();
            history.setUserId(userId);
            history.setNewsId(id);
            history.setBrowseTime(LocalDateTime.now());
            browseHistoryMapper.insert(history);
        }
        return ApiResponse.success(newsService.getById(id));
    }

    @GetMapping("/recommend")
    public ApiResponse<List<News>> recommend(@RequestParam("userId") Long userId,
                                             @RequestParam(value = "size", defaultValue = "6") int size) {
        return ApiResponse.success(newsService.recommendNews(userId, size));
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
        news.setStatus(request.getStatus());

        if (news.getAuthorId() == null) throw new IllegalArgumentException("作者ID不能为空");
        User author = userService.getById(news.getAuthorId());
        if (author == null || author.getStatus() == null || author.getStatus() != 1) {
            throw new IllegalArgumentException("账号不存在或已被禁用");
        }
        if (author.getIsJournalist() == null || author.getIsJournalist() != 1) {
            throw new IllegalArgumentException("请先注册新闻工作者后再发布新闻");
        }

        boolean ok = newsService.save(news);
        if (ok && request.getTagIds() != null && !request.getTagIds().isEmpty()) {
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

    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> update(@PathVariable("id") Long id, @RequestBody UpdateNewsRequest request) {
        News existed = newsService.getById(id);
        if (existed == null) {
            throw new IllegalArgumentException("新闻不存在");
        }
        if (request.getOperatorId() == null) {
            throw new IllegalArgumentException("操作者ID不能为空");
        }
        User operator = userService.getById(request.getOperatorId());
        if (operator == null || operator.getStatus() == null || operator.getStatus() != 1) {
            throw new IllegalArgumentException("账号不存在或已被禁用");
        }
        boolean isAuthor = Objects.equals(existed.getAuthorId(), request.getOperatorId());
        boolean isSystemPublisher = "system".equalsIgnoreCase(operator.getUsername()) || "系统".equals(operator.getNickname());
        if (!isAuthor && !isSystemPublisher) {
            throw new IllegalArgumentException("仅作者本人或系统发布者可修改新闻");
        }

        News news = new News();
        news.setId(id);
        news.setTitle(request.getTitle());
        news.setSummary(request.getSummary());
        news.setContent(request.getContent());
        news.setCategoryId(request.getCategoryId());
        news.setCoverImage(request.getCoverImage());
        news.setVideoUrl(request.getVideoUrl());
        if (isAuthor) {
            news.setStatus(1);
            news.setReviewRemark(null);
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

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") Long id) {
        return ApiResponse.success(newsService.removeById(id));
    }

    @PostMapping("/{id}/like")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> like(@PathVariable("id") Long newsId, @RequestParam("userId") Long userId) {
        NewsLike existed = newsLikeMapper.selectOne(new LambdaQueryWrapper<NewsLike>().eq(NewsLike::getNewsId, newsId).eq(NewsLike::getUserId, userId));
        if (existed == null) {
            NewsLike like = new NewsLike();
            like.setNewsId(newsId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            newsLikeMapper.insert(like);
            newsService.lambdaUpdate().eq(News::getId, newsId).setSql("like_count = like_count + 1").update();
        }
        return ApiResponse.success(true);
    }

    @DeleteMapping("/{id}/like")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> unlike(@PathVariable("id") Long newsId, @RequestParam("userId") Long userId) {
        int deleted = newsLikeMapper.delete(new LambdaQueryWrapper<NewsLike>().eq(NewsLike::getNewsId, newsId).eq(NewsLike::getUserId, userId));
        if (deleted > 0) {
            newsService.lambdaUpdate().eq(News::getId, newsId).setSql("like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END").update();
        }
        return ApiResponse.success(true);
    }

    @PostMapping("/{id}/collect")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> collect(@PathVariable("id") Long newsId, @RequestParam("userId") Long userId) {
        NewsCollect existed = newsCollectMapper.selectOne(new LambdaQueryWrapper<NewsCollect>().eq(NewsCollect::getNewsId, newsId).eq(NewsCollect::getUserId, userId));
        if (existed == null) {
            NewsCollect collect = new NewsCollect();
            collect.setNewsId(newsId);
            collect.setUserId(userId);
            collect.setCreateTime(LocalDateTime.now());
            newsCollectMapper.insert(collect);
            newsService.lambdaUpdate().eq(News::getId, newsId).setSql("collect_count = collect_count + 1").update();
        }
        return ApiResponse.success(true);
    }

    @DeleteMapping("/{id}/collect")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> uncollect(@PathVariable("id") Long newsId, @RequestParam("userId") Long userId) {
        int deleted = newsCollectMapper.delete(new LambdaQueryWrapper<NewsCollect>().eq(NewsCollect::getNewsId, newsId).eq(NewsCollect::getUserId, userId));
        if (deleted > 0) {
            newsService.lambdaUpdate().eq(News::getId, newsId).setSql("collect_count = CASE WHEN collect_count > 0 THEN collect_count - 1 ELSE 0 END").update();
        }
        return ApiResponse.success(true);
    }

    @GetMapping("/{id}/status")
    public ApiResponse<Map<String, Boolean>> getStatus(@PathVariable("id") Long newsId, @RequestParam("userId") Long userId) {
        boolean liked = newsLikeMapper.selectCount(new LambdaQueryWrapper<NewsLike>().eq(NewsLike::getNewsId, newsId).eq(NewsLike::getUserId, userId)) > 0;
        boolean collected = newsCollectMapper.selectCount(new LambdaQueryWrapper<NewsCollect>().eq(NewsCollect::getNewsId, newsId).eq(NewsCollect::getUserId, userId)) > 0;
        return ApiResponse.success(Map.of("liked", liked, "collected", collected));
    }

    @PostMapping("/{id}/comment")
    public ApiResponse<Boolean> comment(@PathVariable("id") Long newsId, @RequestBody CommentRequest request) {
        if (request.getUserId() == null) throw new IllegalArgumentException("用户ID不能为空");
        if (request.getContent() == null || request.getContent().isBlank()) throw new IllegalArgumentException("评论内容不能为空");
        NewsComment comment = new NewsComment();
        comment.setNewsId(newsId);
        comment.setUserId(request.getUserId());
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent().trim());
        comment.setLikeCount(0L);
        comment.setCreateTime(LocalDateTime.now());
        newsCommentMapper.insert(comment);
        return ApiResponse.success(true);
    }

    @GetMapping("/{id}/comments")
    public ApiResponse<List<Map<String, Object>>> comments(@PathVariable("id") Long newsId) {
        List<NewsComment> list = newsCommentMapper.selectList(new LambdaQueryWrapper<NewsComment>().eq(NewsComment::getNewsId, newsId).orderByAsc(NewsComment::getCreateTime));
        if (list.isEmpty()) return ApiResponse.success(Collections.emptyList());
        List<Long> userIds = list.stream().map(NewsComment::getUserId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<User> users = userService.listByIds(userIds);
        Map<Long, String> userNameMap = users.stream().collect(Collectors.toMap(User::getId, u -> (u.getNickname() != null && !u.getNickname().isBlank()) ? u.getNickname() : u.getUsername()));
        Map<Long, String> userAvatarMap = users.stream().collect(Collectors.toMap(User::getId, User::getAvatar, (a, b) -> a));

        List<Map<String, Object>> result = list.stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("newsId", c.getNewsId());
            m.put("userId", c.getUserId());
            m.put("username", userNameMap.getOrDefault(c.getUserId(), "用户" + c.getUserId()));
            m.put("avatar", userAvatarMap.get(c.getUserId()));
            m.put("parentId", c.getParentId());
            m.put("content", c.getContent());
            m.put("likeCount", c.getLikeCount());
            m.put("createTime", c.getCreateTime());
            return m;
        }).collect(Collectors.toList());
        return ApiResponse.success(result);
    }

    @GetMapping("/my-likes")
    public ApiResponse<List<News>> myLikes(@RequestParam("userId") Long userId,
                                           @RequestParam(value = "size", defaultValue = "50") int size) {
        List<NewsLike> likes = newsLikeMapper.selectList(
                new LambdaQueryWrapper<NewsLike>().eq(NewsLike::getUserId, userId).orderByDesc(NewsLike::getCreateTime).last("limit " + Math.max(1, size)));
        if (likes.isEmpty()) return ApiResponse.success(Collections.emptyList());
        List<Long> ids = likes.stream().map(NewsLike::getNewsId).collect(Collectors.toList());
        List<News> newsList = newsService.listByIds(ids);
        Map<Long, News> map = newsList.stream().collect(Collectors.toMap(News::getId, n -> n));
        return ApiResponse.success(ids.stream().map(map::get).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @GetMapping("/my-collects")
    public ApiResponse<List<News>> myCollects(@RequestParam("userId") Long userId,
                                              @RequestParam(value = "size", defaultValue = "50") int size) {
        List<NewsCollect> collects = newsCollectMapper.selectList(
                new LambdaQueryWrapper<NewsCollect>().eq(NewsCollect::getUserId, userId).orderByDesc(NewsCollect::getCreateTime).last("limit " + Math.max(1, size)));
        if (collects.isEmpty()) return ApiResponse.success(Collections.emptyList());
        List<Long> ids = collects.stream().map(NewsCollect::getNewsId).collect(Collectors.toList());
        List<News> newsList = newsService.listByIds(ids);
        Map<Long, News> map = newsList.stream().collect(Collectors.toMap(News::getId, n -> n));
        return ApiResponse.success(ids.stream().map(map::get).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @GetMapping("/history")
    public ApiResponse<List<News>> history(@RequestParam("userId") Long userId,
                                           @RequestParam(value = "size", defaultValue = "20") int size) {
        List<BrowseHistory> historyList = browseHistoryMapper.selectList(new LambdaQueryWrapper<BrowseHistory>().eq(BrowseHistory::getUserId, userId).orderByDesc(BrowseHistory::getBrowseTime).last("limit " + Math.max(1, size)));
        if (historyList.isEmpty()) return ApiResponse.success(Collections.emptyList());
        List<Long> ids = historyList.stream().map(BrowseHistory::getNewsId).distinct().collect(Collectors.toList());
        List<News> newsList = newsService.listByIds(ids);
        Map<Long, News> map = newsList.stream().collect(Collectors.toMap(News::getId, n -> n));
        List<News> sorted = new ArrayList<>();
        for (Long id : ids) {
            News n = map.get(id);
            if (n != null) sorted.add(n);
        }
        return ApiResponse.success(sorted);
    }

    @Data
    private static class CommentRequest {
        private Long userId;
        private Long parentId;
        private String content;
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
        private List<Long> tagIds;
    }

    @Data
    private static class UpdateNewsRequest {
        private Long operatorId;
        private String title;
        private Long categoryId;
        private String summary;
        private String content;
        private String coverImage;
        private String videoUrl;
        private List<Long> tagIds;
    }
}
