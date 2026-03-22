package com.example.newsplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.newsplatform.entity.Admin;
import com.example.newsplatform.mapper.AdminMapper;
import com.example.newsplatform.service.AdminService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Admin login(String username, String password) {
        Admin admin = getOne(new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, username));
        if (admin == null || admin.getStatus() != 1) {
            throw new IllegalArgumentException("账号不存在或已被禁用");
        }
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }
        admin.setPassword(null);
        return admin;
    }
}
