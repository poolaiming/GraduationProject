package com.example.newsplatform.service;



import com.baomidou.mybatisplus.extension.service.IService;

import com.example.newsplatform.entity.User;



public interface UserService extends IService<User> {



    User register(String username, String password, String nickname);



    User login(String username, String password);



    boolean requestAccountCancellation(Long userId);



    boolean removeUserWithAllData(Long userId);

}

