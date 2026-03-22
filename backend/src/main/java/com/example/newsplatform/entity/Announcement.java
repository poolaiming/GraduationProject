package com.example.newsplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_announcement")
public class Announcement {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;
    private String title;
    private String content;
    private Long viewCount;
    private Long likeCount;
    private Integer isTop;
    private Integer status;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

