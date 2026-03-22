package com.example.newsplatform.config;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.example.newsplatform.entity.Admin;

import com.example.newsplatform.mapper.AdminMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Component;



@Component

@RequiredArgsConstructor

public class AdminInitRunner implements CommandLineRunner {



    private final AdminMapper adminMapper;



    @Override

    public void run(String... args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();



        Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()

                .eq(Admin::getUsername, "admin")

                .last("limit 1"));



        if (admin == null) {

            Admin newAdmin = new Admin();

            newAdmin.setUsername("admin");

            newAdmin.setPassword(encoder.encode("123456"));

            newAdmin.setNickname("管理员");

            newAdmin.setRole("ADMIN");

            newAdmin.setStatus(1);

            adminMapper.insert(newAdmin);

            return;

        }



        admin.setPassword(encoder.encode("123456"));

        if (admin.getStatus() == null) {

            admin.setStatus(1);

        }

        adminMapper.updateById(admin);

    }

}

