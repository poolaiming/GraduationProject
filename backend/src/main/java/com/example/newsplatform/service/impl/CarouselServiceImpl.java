package com.example.newsplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.newsplatform.entity.Carousel;
import com.example.newsplatform.mapper.CarouselMapper;
import com.example.newsplatform.service.CarouselService;
import org.springframework.stereotype.Service;

@Service
public class CarouselServiceImpl extends ServiceImpl<CarouselMapper, Carousel> implements CarouselService {
}
