package com.example.newsplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.NewsCategory;
import com.example.newsplatform.mapper.NewsCategoryMapper;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.service.NewsCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsCategoryServiceImpl extends ServiceImpl<NewsCategoryMapper, NewsCategory> implements NewsCategoryService {

    private final NewsMapper newsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWithRejectNews(Long categoryId) {
        // 将该分类下所有非草稿新闻驳回
        newsMapper.update(null, new LambdaUpdateWrapper<News>()
                .eq(News::getCategoryId, categoryId)
                .ne(News::getStatus, 0)
                .set(News::getStatus, 3)
                .set(News::getReviewRemark, "所属分类已被删除"));
        // 清空所有关联新闻的 category_id，解除外键约束后再删除分类
        newsMapper.update(null, new LambdaUpdateWrapper<News>()
                .eq(News::getCategoryId, categoryId)
                .set(News::getCategoryId, null));
        removeById(categoryId);
    }
}
