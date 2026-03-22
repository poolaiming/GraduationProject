package com.example.newsplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.newsplatform.entity.AnnouncementCategory;
import com.example.newsplatform.mapper.AnnouncementCategoryMapper;
import com.example.newsplatform.service.AnnouncementCategoryService;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementCategoryServiceImpl extends ServiceImpl<AnnouncementCategoryMapper, AnnouncementCategory> implements AnnouncementCategoryService {
}
