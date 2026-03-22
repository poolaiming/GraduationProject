package com.example.newsplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_news_comment")
public class NewsComment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long newsId;
    private Long userId;
    private Long parentId;
    private String content;
    private Long likeCount;
    private LocalDateTime createTime;
}

