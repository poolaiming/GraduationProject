package com.example.newsplatform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.ForumComment;
import com.example.newsplatform.entity.ForumPost;
import com.example.newsplatform.mapper.ForumCommentMapper;
import com.example.newsplatform.service.ForumPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/forum")
@RequiredArgsConstructor
public class AdminForumController {

    private final ForumPostService forumPostService;
    private final ForumCommentMapper forumCommentMapper;

    @GetMapping("/page")
    public ApiResponse<Page<ForumPost>> page(@RequestParam(value = "keyword", required = false) String keyword,
                                             @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        LambdaQueryWrapper<ForumPost> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) wrapper.like(ForumPost::getTitle, keyword);
        wrapper.orderByDesc(ForumPost::getIsTop).orderByDesc(ForumPost::getCreateTime);
        return ApiResponse.success(forumPostService.page(Page.of(pageNum, pageSize), wrapper));
    }

    @PutMapping("/{id}/top")
    public ApiResponse<Boolean> setTop(@PathVariable("id") Long id, @RequestParam("isTop") Integer isTop) {
        ForumPost post = new ForumPost();
        post.setId(id);
        post.setIsTop(isTop);
        return ApiResponse.success(forumPostService.updateById(post));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateStatus(@PathVariable("id") Long id,
                                             @RequestParam("status") Integer status,
                                             @RequestParam(value = "reviewRemark", required = false) String reviewRemark) {
        ForumPost post = new ForumPost();
        post.setId(id);
        post.setStatus(status);
        post.setReviewRemark(reviewRemark);
        return ApiResponse.success(forumPostService.updateById(post));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") Long id) {
        return ApiResponse.success(forumPostService.removeById(id));
    }

    @GetMapping("/{id}/comments")
    public ApiResponse<Page<ForumComment>> comments(@PathVariable("id") Long postId,
                                                    @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Page<ForumComment> page = forumCommentMapper.selectPage(
                Page.of(pageNum, pageSize),
                new LambdaQueryWrapper<ForumComment>()
                        .eq(ForumComment::getPostId, postId)
                        .orderByDesc(ForumComment::getCreateTime)
        );
        return ApiResponse.success(page);
    }

    @DeleteMapping("/comment/{id}")
    public ApiResponse<Boolean> deleteComment(@PathVariable("id") Long commentId) {
        return ApiResponse.success(forumCommentMapper.deleteById(commentId) > 0);
    }
}
