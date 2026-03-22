package com.example.newsplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.newsplatform.entity.Announcement;
import com.example.newsplatform.mapper.AnnouncementMapper;
import com.example.newsplatform.service.AnnouncementService;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
}
