package com.example.newsplatform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.Announcement;
import com.example.newsplatform.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/announcement")
@RequiredArgsConstructor
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/page")
    public ApiResponse<Page<Announcement>> page(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<Announcement>()
                .like(keyword != null && !keyword.trim().isEmpty(), Announcement::getTitle, keyword)
                .orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getPublishTime)
                .orderByDesc(Announcement::getCreateTime);

        return ApiResponse.success(announcementService.page(Page.of(pageNum, pageSize), wrapper));
    }

    @PostMapping
    public ApiResponse<Boolean> create(@RequestBody Announcement entity) {
        entity.setId(null);
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        if (entity.getIsTop() == null) {
            entity.setIsTop(0);
        }
        if (entity.getPublishTime() == null && entity.getStatus() == 1) {
            entity.setPublishTime(LocalDateTime.now());
        }
        return ApiResponse.success(announcementService.save(entity));
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable("id") Long id, @RequestBody Announcement entity) {
        entity.setId(id);
        if (entity.getIsTop() == null) {
            entity.setIsTop(0);
        }
        if (entity.getPublishTime() == null && entity.getStatus() != null && entity.getStatus() == 1) {
            entity.setPublishTime(LocalDateTime.now());
        }
        return ApiResponse.success(announcementService.updateById(entity));
    }

    @PutMapping("/{id}/top")
    public ApiResponse<Boolean> setTop(@PathVariable("id") Long id, @RequestParam("isTop") Integer isTop) {
        return ApiResponse.success(
                announcementService.lambdaUpdate()
                        .eq(Announcement::getId, id)
                        .set(Announcement::getIsTop, (isTop != null && isTop == 1) ? 1 : 0)
                        .update()
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") Long id) {
        return ApiResponse.success(announcementService.removeById(id));
    }
}
