package com.example.newsplatform.controller.admin;

import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.NewsCategory;
import com.example.newsplatform.service.NewsCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/news-category")
@RequiredArgsConstructor
public class AdminNewsCategoryController {

    private final NewsCategoryService newsCategoryService;

    @GetMapping("/list")
    public ApiResponse<List<NewsCategory>> list() {
        return ApiResponse.success(newsCategoryService.list());
    }

    @PostMapping
    public ApiResponse<Boolean> create(@RequestBody NewsCategory entity) {
        entity.setId(null);
        return ApiResponse.success(newsCategoryService.save(entity));
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable("id") Long id, @RequestBody NewsCategory entity) {
        entity.setId(id);
        return ApiResponse.success(newsCategoryService.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") Long id) {
        newsCategoryService.deleteWithRejectNews(id);
        return ApiResponse.success(true);
    }
}
