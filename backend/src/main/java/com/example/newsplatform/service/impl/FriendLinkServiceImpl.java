package com.example.newsplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.newsplatform.entity.FriendLink;
import com.example.newsplatform.mapper.FriendLinkMapper;
import com.example.newsplatform.service.FriendLinkService;
import org.springframework.stereotype.Service;

@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements FriendLinkService {
}
