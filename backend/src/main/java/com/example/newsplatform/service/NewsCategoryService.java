package com.example.newsplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.newsplatform.entity.NewsCategory;

public interface NewsCategoryService extends IService<NewsCategory> {

    /**
     * 删除分类，并将该分类下所有新闻状态改为驳回（3）
     */
    void deleteWithRejectNews(Long categoryId);
}
