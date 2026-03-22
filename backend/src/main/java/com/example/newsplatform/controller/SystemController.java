package com.example.newsplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.About;
import com.example.newsplatform.entity.Carousel;
import com.example.newsplatform.entity.FriendLink;
import com.example.newsplatform.service.AboutService;
import com.example.newsplatform.service.CarouselService;
import com.example.newsplatform.service.FriendLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final CarouselService carouselService;
    private final FriendLinkService friendLinkService;
    private final AboutService aboutService;

    @GetMapping("/carousel/list")
    public ApiResponse<List<Carousel>> listCarousel() {
        LocalDateTime now = LocalDateTime.now();
        List<Carousel> list = carouselService.list(new LambdaQueryWrapper<Carousel>()
                .eq(Carousel::getStatus, 1)
                .and(w -> w.isNull(Carousel::getStartTime).or().le(Carousel::getStartTime, now))
                .and(w -> w.isNull(Carousel::getEndTime).or().ge(Carousel::getEndTime, now))
                .orderByAsc(Carousel::getSort)
                .orderByDesc(Carousel::getId));
        return ApiResponse.success(list);
    }

    @GetMapping("/friend-link/list")
    public ApiResponse<List<FriendLink>> listFriendLink() {
        List<FriendLink> list = friendLinkService.list(new LambdaQueryWrapper<FriendLink>()
                .eq(FriendLink::getStatus, 1)
                .orderByAsc(FriendLink::getSort)
                .orderByDesc(FriendLink::getId));
        return ApiResponse.success(list);
    }

    @GetMapping("/about")
    public ApiResponse<About> getAbout() {
        List<About> list = aboutService.list();
        if (list.isEmpty()) {
            return ApiResponse.success(null);
        }
        return ApiResponse.success(list.get(0));
    }
}
