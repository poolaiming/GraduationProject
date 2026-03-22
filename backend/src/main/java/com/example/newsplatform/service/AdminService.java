package com.example.newsplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.newsplatform.entity.Admin;

public interface AdminService extends IService<Admin> {

    Admin login(String username, String password);
}
