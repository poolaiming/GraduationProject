package com.example.newsplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_news")
public class News {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private Long categoryId;
    private Long authorId;
    @TableField(exist = false)
    private String authorName;
    private String summary;
    private String content;
    private String coverImage;
    private String videoUrl;
    private Long viewCount;
    private Long collectCount;
    private Long likeCount;
    /**
     * 状态：0草稿 1待审核 2已发布 3驳回
     */
    private Integer status;
    /**
     * 审核意见/驳回原因
     */
    private String reviewRemark;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

