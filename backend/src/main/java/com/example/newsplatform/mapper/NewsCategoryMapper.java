package com.example.newsplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.newsplatform.entity.NewsCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsCategoryMapper extends BaseMapper<NewsCategory> {
}

