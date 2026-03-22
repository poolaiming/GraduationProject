package com.example.newsplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.newsplatform.entity.Survey;
import com.example.newsplatform.mapper.SurveyMapper;
import com.example.newsplatform.service.SurveyService;
import org.springframework.stereotype.Service;

@Service
public class SurveyServiceImpl extends ServiceImpl<SurveyMapper, Survey> implements SurveyService {
}
