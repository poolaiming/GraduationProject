package com.example.newsplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_news_category")
public class NewsCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String description;
    private Integer sort;
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

