package com.example.newsplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.newsplatform.entity.ForumPost;
import com.example.newsplatform.mapper.ForumPostMapper;
import com.example.newsplatform.service.ForumPostService;
import org.springframework.stereotype.Service;

@Service
public class ForumPostServiceImpl extends ServiceImpl<ForumPostMapper, ForumPost> implements ForumPostService {
}
