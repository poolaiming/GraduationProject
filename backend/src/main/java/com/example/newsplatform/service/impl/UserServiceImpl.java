package com.example.newsplatform.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.newsplatform.entity.User;

import com.example.newsplatform.mapper.BrowseHistoryMapper;

import com.example.newsplatform.mapper.ForumCommentMapper;

import com.example.newsplatform.mapper.ForumLikeMapper;

import com.example.newsplatform.mapper.ForumPostMapper;

import com.example.newsplatform.mapper.MessageMapper;

import com.example.newsplatform.mapper.NewsCollectMapper;

import com.example.newsplatform.mapper.NewsCommentMapper;

import com.example.newsplatform.mapper.NewsLikeMapper;

import com.example.newsplatform.mapper.NewsMapper;

import com.example.newsplatform.mapper.UserMapper;

import com.example.newsplatform.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.StringUtils;



@Service

@RequiredArgsConstructor

public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {



    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final NewsMapper newsMapper;

    private final NewsCommentMapper newsCommentMapper;

    private final NewsLikeMapper newsLikeMapper;

    private final NewsCollectMapper newsCollectMapper;

    private final ForumPostMapper forumPostMapper;

    private final ForumCommentMapper forumCommentMapper;

    private final ForumLikeMapper forumLikeMapper;

    private final MessageMapper messageMapper;

    private final BrowseHistoryMapper browseHistoryMapper;



    @Override

    public User register(String username, String password, String nickname) {

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {

            throw new IllegalArgumentException("用户名和密码不能为空");

        }

        Long count = this.lambdaQuery().eq(User::getUsername, username).count();

        if (count != null && count > 0) {

            throw new IllegalArgumentException("用户名已存在");

        }

        User user = new User();

        user.setUsername(username);

        user.setPassword(passwordEncoder.encode(password));

        user.setNickname(nickname);

        user.setStatus(1);

        this.save(user);

        user.setPassword(null);

        return user;

    }



    @Override

    public User login(String username, String password) {

        User user = this.getOne(new LambdaQueryWrapper<User>()

                .eq(User::getUsername, username));

        if (user == null || user.getStatus() != 1) {

            throw new IllegalArgumentException("账号不存在或已被禁用");

        }

        if (!passwordEncoder.matches(password, user.getPassword())) {

            throw new IllegalArgumentException("密码错误");

        }

        user.setPassword(null);

        return user;

    }



    @Override

    public boolean requestAccountCancellation(Long userId) {

        if (userId == null) {

            throw new IllegalArgumentException("用户ID不能为空");

        }

        User user = this.getById(userId);

        if (user == null) {

            throw new IllegalArgumentException("用户不存在");

        }

        if (user.getStatus() != null && user.getStatus() == 2) {

            throw new IllegalArgumentException("注销申请已提交，请等待管理员处理");

        }

        User updating = new User();

        updating.setId(userId);

        updating.setStatus(2);

        return this.updateById(updating);

    }



    @Override

    @Transactional(rollbackFor = Exception.class)

    public boolean removeUserWithAllData(Long userId) {

        if (userId == null) {

            throw new IllegalArgumentException("用户ID不能为空");

        }



        newsLikeMapper.delete(new LambdaQueryWrapper<com.example.newsplatform.entity.NewsLike>()

                .eq(com.example.newsplatform.entity.NewsLike::getUserId, userId));

        newsCollectMapper.delete(new LambdaQueryWrapper<com.example.newsplatform.entity.NewsCollect>()

                .eq(com.example.newsplatform.entity.NewsCollect::getUserId, userId));

        newsCommentMapper.delete(new LambdaQueryWrapper<com.example.newsplatform.entity.NewsComment>()

                .eq(com.example.newsplatform.entity.NewsComment::getUserId, userId));



        var myNewsList = newsMapper.selectList(new LambdaQueryWrapper<com.example.newsplatform.entity.News>()

                .eq(com.example.newsplatform.entity.News::getAuthorId, userId));

        if (myNewsList != null) {

            for (var news : myNewsList) {

                Long newsId = news.getId();

                newsLikeMapper.delete(new LambdaQueryWrapper<com.example.newsplatform.entity.NewsLike>()

                        .eq(com.example.newsplatform.entity.NewsLike::getNewsId, newsId));

                newsCollectMapper.delete(new LambdaQueryWrapper<com.example.newsplatform.entity.NewsCollect>()

                        .eq(com.example.newsplatform.entity.NewsCollect::getNewsId, newsId));

                newsCommentMapper.delete(new LambdaQueryWrapper<com.example.newsplatform.entity.NewsComment>()

                        .eq(com.example.newsplatform.entity.NewsComment::getNewsId, newsId));

                newsMapper.deleteById(newsId);

            }

        }



        forumLikeMapper.delete(new LambdaQueryWrapper<com.example.newsplatform.entity.ForumLike>()

                .eq(com.example.newsplatform.entity.ForumLike::getUserId, userId));

        forumCommentMapper.delete(new LambdaQueryWrapper<com.example.newsplatform.entity.ForumComment>()

                .eq(com.example.newsplatform.entity.ForumComment::getUserId, userId));



        var myPostList = forumPostMapper.selectList(new LambdaQueryWrapper<com.example.newsplatform.entity.ForumPost>()

                .eq(com.example.newsplatform.entity.ForumPost::getUserId, userId));

        if (myPostList != null) {

            for (var post : myPostList) {

                Long postId = post.getId();

                forumLikeMapper.delete(new LambdaQueryWrapper<com.example.newsplatform.entity.ForumLike>()

                        .eq(com.example.newsplatform.entity.ForumLike::getPostId, postId));

                forumCommentMapper.delete(new LambdaQueryWrapper<com.example.newsplatform.entity.ForumComment>()

                        .eq(com.example.newsplatform.entity.ForumComment::getPostId, postId));

                forumPostMapper.deleteById(postId);

            }

        }



        messageMapper.delete(new LambdaQueryWrapper<com.example.newsplatform.entity.Message>()

                .eq(com.example.newsplatform.entity.Message::getUserId, userId));

        browseHistoryMapper.delete(new LambdaQueryWrapper<com.example.newsplatform.entity.BrowseHistory>()

                .eq(com.example.newsplatform.entity.BrowseHistory::getUserId, userId));



        return this.removeById(userId);

    }

}

