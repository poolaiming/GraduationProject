package com.example.newsplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.newsplatform.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

