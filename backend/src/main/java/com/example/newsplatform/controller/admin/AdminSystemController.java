package com.example.newsplatform.controller.admin;

import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.*;
import com.example.newsplatform.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class AdminSystemController {

    private final AboutService aboutService;
    private final CarouselService carouselService;
    private final FriendLinkService friendLinkService;
    private final AnnouncementService announcementService;
    private final AnnouncementCategoryService announcementCategoryService;

    // ========== 关于我们 ==========
    @GetMapping("/about")
    public ApiResponse<About> getAbout() {
        About one = aboutService.getOne(null);
        return ApiResponse.success(one);
    }

    @PutMapping("/about")
    public ApiResponse<Boolean> saveAbout(@RequestBody About about) {
        if (about.getId() == null) {
            about.setId(1L);
        }
        return ApiResponse.success(aboutService.saveOrUpdate(about));
    }

    // ========== 轮播图 ==========
    @GetMapping("/carousel/list")
    public ApiResponse<List<Carousel>> listCarousel() {
        return ApiResponse.success(carouselService.list());
    }

    @PostMapping("/carousel")
    public ApiResponse<Boolean> createCarousel(@RequestBody Carousel entity) {
        entity.setId(null);
        return ApiResponse.success(carouselService.save(entity));
    }

    @PutMapping("/carousel/{id}")
    public ApiResponse<Boolean> updateCarousel(@PathVariable("id") Long id, @RequestBody Carousel entity) {
        entity.setId(id);
        return ApiResponse.success(carouselService.updateById(entity));
    }

    @DeleteMapping("/carousel/{id}")
    public ApiResponse<Boolean> deleteCarousel(@PathVariable("id") Long id) {
        return ApiResponse.success(carouselService.removeById(id));
    }

    // ========== 友情链接 ==========
    @GetMapping("/friend-link/list")
    public ApiResponse<List<FriendLink>> listFriendLink() {
        return ApiResponse.success(friendLinkService.list());
    }

    @PostMapping("/friend-link")
    public ApiResponse<Boolean> createFriendLink(@RequestBody FriendLink entity) {
        entity.setId(null);
        return ApiResponse.success(friendLinkService.save(entity));
    }

    @PutMapping("/friend-link/{id}")
    public ApiResponse<Boolean> updateFriendLink(@PathVariable("id") Long id, @RequestBody FriendLink entity) {
        entity.setId(id);
        return ApiResponse.success(friendLinkService.updateById(entity));
    }

    @DeleteMapping("/friend-link/{id}")
    public ApiResponse<Boolean> deleteFriendLink(@PathVariable("id") Long id) {
        return ApiResponse.success(friendLinkService.removeById(id));
    }

    // ========== 公告分类 ==========
    @GetMapping("/announcement-category/list")
    public ApiResponse<List<AnnouncementCategory>> listAnnouncementCategory() {
        return ApiResponse.success(announcementCategoryService.list());
    }

    @PostMapping("/announcement-category")
    public ApiResponse<Boolean> createAnnouncementCategory(@RequestBody AnnouncementCategory entity) {
        entity.setId(null);
        return ApiResponse.success(announcementCategoryService.save(entity));
    }

    @PutMapping("/announcement-category/{id}")
    public ApiResponse<Boolean> updateAnnouncementCategory(@PathVariable("id") Long id, @RequestBody AnnouncementCategory entity) {
        entity.setId(id);
        return ApiResponse.success(announcementCategoryService.updateById(entity));
    }

    @DeleteMapping("/announcement-category/{id}")
    public ApiResponse<Boolean> deleteAnnouncementCategory(@PathVariable("id") Long id) {
        return ApiResponse.success(announcementCategoryService.removeById(id));
    }

    // ========== 公告资讯 ==========
    @GetMapping("/announcement/list")
    public ApiResponse<List<Announcement>> listAnnouncement() {
        return ApiResponse.success(announcementService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Announcement>()
                        .orderByDesc(Announcement::getCreateTime)));
    }

    @PostMapping("/announcement")
    public ApiResponse<Boolean> createAnnouncement(@RequestBody Announcement entity) {
        entity.setId(null);
        return ApiResponse.success(announcementService.save(entity));
    }

    @PutMapping("/announcement/{id}")
    public ApiResponse<Boolean> updateAnnouncement(@PathVariable("id") Long id, @RequestBody Announcement entity) {
        entity.setId(id);
        return ApiResponse.success(announcementService.updateById(entity));
    }

    @DeleteMapping("/announcement/{id}")
    public ApiResponse<Boolean> deleteAnnouncement(@PathVariable("id") Long id) {
        return ApiResponse.success(announcementService.removeById(id));
    }
}
