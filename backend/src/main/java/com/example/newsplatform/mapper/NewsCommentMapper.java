package com.example.newsplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.newsplatform.entity.NewsComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsCommentMapper extends BaseMapper<NewsComment> {
}

