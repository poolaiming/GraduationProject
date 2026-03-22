package com.example.newsplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.newsplatform.entity.About;
import com.example.newsplatform.mapper.AboutMapper;
import com.example.newsplatform.service.AboutService;
import org.springframework.stereotype.Service;

@Service
public class AboutServiceImpl extends ServiceImpl<AboutMapper, About> implements AboutService {
}
