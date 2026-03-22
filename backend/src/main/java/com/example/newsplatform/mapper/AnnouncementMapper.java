package com.example.newsplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.newsplatform.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}

