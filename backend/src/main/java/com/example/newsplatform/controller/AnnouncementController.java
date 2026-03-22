package com.example.newsplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.Announcement;
import com.example.newsplatform.entity.AnnouncementLike;
import com.example.newsplatform.mapper.AnnouncementLikeMapper;
import com.example.newsplatform.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final AnnouncementLikeMapper announcementLikeMapper;

    @GetMapping("/list")
    public ApiResponse<List<Announcement>> list() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1)
                .orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getPublishTime)
                .orderByDesc(Announcement::getCreateTime);
        return ApiResponse.success(announcementService.list(wrapper));
    }

    @GetMapping("/{id}")
    public ApiResponse<Announcement> detail(@PathVariable("id") Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null || announcement.getStatus() == null || announcement.getStatus() != 1) {
            throw new IllegalArgumentException("公告不存在或未发布");
        }
        return ApiResponse.success(announcement);
    }

    @PostMapping("/{id}/like")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> like(@PathVariable("id") Long announcementId, @RequestParam("userId") Long userId) {
        AnnouncementLike existed = announcementLikeMapper.selectOne(
                new LambdaQueryWrapper<AnnouncementLike>()
                        .eq(AnnouncementLike::getAnnouncementId, announcementId)
                        .eq(AnnouncementLike::getUserId, userId));
        if (existed == null) {
            AnnouncementLike like = new AnnouncementLike();
            like.setAnnouncementId(announcementId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            announcementLikeMapper.insert(like);
            announcementService.lambdaUpdate()
                    .eq(Announcement::getId, announcementId)
                    .setSql("like_count = like_count + 1")
                    .update();
        }
        return ApiResponse.success(true);
    }

    @DeleteMapping("/{id}/like")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> unlike(@PathVariable("id") Long announcementId, @RequestParam("userId") Long userId) {
        int deleted = announcementLikeMapper.delete(
                new LambdaQueryWrapper<AnnouncementLike>()
                        .eq(AnnouncementLike::getAnnouncementId, announcementId)
                        .eq(AnnouncementLike::getUserId, userId));
        if (deleted > 0) {
            announcementService.lambdaUpdate()
                    .eq(Announcement::getId, announcementId)
                    .setSql("like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END")
                    .update();
        }
        return ApiResponse.success(true);
    }

    @GetMapping("/{id}/status")
    public ApiResponse<Map<String, Boolean>> getStatus(@PathVariable("id") Long announcementId, @RequestParam("userId") Long userId) {
        boolean liked = announcementLikeMapper.selectCount(
                new LambdaQueryWrapper<AnnouncementLike>()
                        .eq(AnnouncementLike::getAnnouncementId, announcementId)
                        .eq(AnnouncementLike::getUserId, userId)) > 0;
        return ApiResponse.success(Map.of("liked", liked));
    }
}
