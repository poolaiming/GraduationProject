package com.example.newsplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.ForumComment;
import com.example.newsplatform.entity.ForumLike;
import com.example.newsplatform.entity.ForumPost;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.mapper.ForumCommentMapper;
import com.example.newsplatform.mapper.ForumLikeMapper;
import com.example.newsplatform.service.ForumPostService;
import com.example.newsplatform.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
public class ForumController {

    private final ForumPostService forumPostService;
    private final UserService userService;
    private final ForumLikeMapper forumLikeMapper;
    private final ForumCommentMapper forumCommentMapper;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping
    public ApiResponse<Boolean> create(@RequestBody CreateForumRequest request) {
        if (request.getUserId() == null) throw new IllegalArgumentException("用户ID不能为空");
        if (request.getTitle() == null || request.getTitle().isBlank()) throw new IllegalArgumentException("标题不能为空");
        if (request.getContent() == null || request.getContent().isBlank()) throw new IllegalArgumentException("内容不能为空");

        ForumPost post = new ForumPost();
        post.setUserId(request.getUserId());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setStatus(1);
        post.setIsTop(0);
        post.setViewCount(0L);
        post.setLikeCount(0L);
        post.setCommentCount(0L);
        return ApiResponse.success(forumPostService.save(post));
    }

    @GetMapping("/page")
    public ApiResponse<Page<ForumPost>> page(@RequestParam(value = "keyword", required = false) String keyword,
                                             @RequestParam(value = "userId", required = false) Long userId,
                                             @RequestParam(value = "status", required = false) Integer status,
                                             @RequestParam(value = "startTime", required = false) String startTime,
                                             @RequestParam(value = "endTime", required = false) String endTime,
                                             @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        LambdaQueryWrapper<ForumPost> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) wrapper.like(ForumPost::getTitle, keyword);
        if (userId != null) wrapper.eq(ForumPost::getUserId, userId);
        if (status != null) {
            wrapper.eq(ForumPost::getStatus, status);
        } else {
            wrapper.eq(ForumPost::getStatus, 2);
        }
        if (startTime != null && !startTime.isBlank()) wrapper.ge(ForumPost::getCreateTime, LocalDateTime.parse(startTime, DATE_TIME_FORMATTER));
        if (endTime != null && !endTime.isBlank()) wrapper.le(ForumPost::getCreateTime, LocalDateTime.parse(endTime, DATE_TIME_FORMATTER));
        wrapper.orderByDesc(ForumPost::getIsTop).orderByDesc(ForumPost::getCreateTime);

        Page<ForumPost> page = forumPostService.page(Page.of(pageNum, pageSize), wrapper);
        fillAuthorNames(page.getRecords());
        return ApiResponse.success(page);
    }

    @GetMapping("/my")
    public ApiResponse<Page<ForumPost>> myPosts(@RequestParam("userId") Long userId,
                                                @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        LambdaQueryWrapper<ForumPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForumPost::getUserId, userId).orderByDesc(ForumPost::getCreateTime);
        Page<ForumPost> page = forumPostService.page(Page.of(pageNum, pageSize), wrapper);
        fillAuthorNames(page.getRecords());
        return ApiResponse.success(page);
    }

    @GetMapping("/my-likes")
    public ApiResponse<List<ForumPost>> myLikes(@RequestParam("userId") Long userId,
                                                @RequestParam(value = "size", defaultValue = "50") int size) {
        List<ForumLike> likes = forumLikeMapper.selectList(
                new LambdaQueryWrapper<ForumLike>().eq(ForumLike::getUserId, userId).orderByDesc(ForumLike::getCreateTime).last("limit " + Math.max(1, size)));
        if (likes.isEmpty()) return ApiResponse.success(Collections.emptyList());
        List<Long> ids = likes.stream().map(ForumLike::getPostId).collect(Collectors.toList());
        List<ForumPost> posts = forumPostService.listByIds(ids);
        Map<Long, ForumPost> map = posts.stream().collect(Collectors.toMap(ForumPost::getId, p -> p));
        List<ForumPost> sorted = ids.stream().map(map::get).filter(Objects::nonNull).collect(Collectors.toList());
        fillAuthorNames(sorted);
        return ApiResponse.success(sorted);
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable("id") Long id, @RequestBody UpdateForumRequest request) {
        ForumPost post = forumPostService.getById(id);
        if (post == null) throw new IllegalArgumentException("帖子不存在");
        if (request.getUserId() == null || !Objects.equals(post.getUserId(), request.getUserId())) throw new IllegalArgumentException("不能修改他人的帖子");
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        return ApiResponse.success(forumPostService.updateById(post));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        ForumPost post = forumPostService.getById(id);
        if (post == null) throw new IllegalArgumentException("帖子不存在");
        if (!Objects.equals(post.getUserId(), userId)) throw new IllegalArgumentException("不能删除他人的帖子");
        return ApiResponse.success(forumPostService.removeById(id));
    }

    @PostMapping("/{id}/like")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> like(@PathVariable("id") Long postId,
                                     @RequestParam("userId") Long userId) {
        ForumLike existed = forumLikeMapper.selectOne(new LambdaQueryWrapper<ForumLike>()
                .eq(ForumLike::getPostId, postId)
                .eq(ForumLike::getUserId, userId));
        if (existed == null) {
            ForumLike like = new ForumLike();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            forumLikeMapper.insert(like);
            forumPostService.lambdaUpdate().eq(ForumPost::getId, postId).setSql("like_count = like_count + 1").update();
        }
        return ApiResponse.success(true);
    }

    @DeleteMapping("/{id}/like")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> unlike(@PathVariable("id") Long postId,
                                       @RequestParam("userId") Long userId) {
        int deleted = forumLikeMapper.delete(new LambdaQueryWrapper<ForumLike>()
                .eq(ForumLike::getPostId, postId)
                .eq(ForumLike::getUserId, userId));
        if (deleted > 0) {
            forumPostService.lambdaUpdate().eq(ForumPost::getId, postId)
                    .setSql("like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END")
                    .update();
        }
        return ApiResponse.success(true);
    }

    @GetMapping("/{id}/status")
    public ApiResponse<Map<String, Boolean>> status(@PathVariable("id") Long postId,
                                                     @RequestParam("userId") Long userId) {
        boolean liked = forumLikeMapper.selectCount(new LambdaQueryWrapper<ForumLike>()
                .eq(ForumLike::getPostId, postId)
                .eq(ForumLike::getUserId, userId)) > 0;
        return ApiResponse.success(Map.of("liked", liked));
    }

    @PostMapping("/{id}/comment")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> comment(@PathVariable("id") Long postId,
                                        @RequestBody CommentRequest request) {
        if (request.getUserId() == null) throw new IllegalArgumentException("用户ID不能为空");
        if (request.getContent() == null || request.getContent().isBlank()) throw new IllegalArgumentException("评论内容不能为空");

        ForumComment comment = new ForumComment();
        comment.setPostId(postId);
        comment.setUserId(request.getUserId());
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent().trim());
        comment.setLikeCount(0L);
        comment.setCreateTime(LocalDateTime.now());
        forumCommentMapper.insert(comment);

        forumPostService.lambdaUpdate().eq(ForumPost::getId, postId).setSql("comment_count = comment_count + 1").update();
        return ApiResponse.success(true);
    }

    @GetMapping("/{id}/comments")
    public ApiResponse<List<Map<String, Object>>> comments(@PathVariable("id") Long postId) {
        List<ForumComment> list = forumCommentMapper.selectList(new LambdaQueryWrapper<ForumComment>()
                .eq(ForumComment::getPostId, postId)
                .orderByDesc(ForumComment::getCreateTime));
        if (list.isEmpty()) return ApiResponse.success(Collections.emptyList());

        List<Long> userIds = list.stream().map(ForumComment::getUserId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<User> users = userService.listByIds(userIds);
        Map<Long, String> userNameMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> (u.getNickname() != null && !u.getNickname().isBlank()) ? u.getNickname() : u.getUsername()));
        Map<Long, String> userAvatarMap = users.stream()
                .collect(Collectors.toMap(User::getId, User::getAvatar, (a, b) -> a));

        List<Map<String, Object>> result = list.stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("postId", c.getPostId());
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

    private void fillAuthorNames(List<ForumPost> posts) {
        if (posts == null || posts.isEmpty()) return;
        List<Long> userIds = posts.stream().map(ForumPost::getUserId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (userIds.isEmpty()) return;

        List<User> users = userService.listByIds(userIds);
        Map<Long, String> userNameMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> (u.getNickname() != null && !u.getNickname().isBlank()) ? u.getNickname() : u.getUsername()));

        posts.forEach(post -> post.setAuthorName(userNameMap.getOrDefault(post.getUserId(), "用户" + post.getUserId())));
    }

    @Data
    private static class CreateForumRequest {
        private Long userId;
        private String title;
        private String content;
    }

    @Data
    private static class UpdateForumRequest {
        private Long userId;
        private String title;
        private String content;
    }

    @Data
    private static class CommentRequest {
        private Long userId;
        private Long parentId;
        private String content;
    }
}
