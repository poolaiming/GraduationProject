package com.example.newsplatform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.newsplatform.entity.News;

import java.util.List;

public interface NewsService extends IService<News> {

    Page<News> pageNews(Long categoryId, Long authorId, String keyword, String authorName, Long tagId, String sortBy, int pageNum, int pageSize);

    void increaseViewCount(Long newsId);

    List<News> recommendNews(Long userId, int size);
}
