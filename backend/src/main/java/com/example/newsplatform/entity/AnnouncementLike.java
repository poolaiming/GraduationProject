package com.example.newsplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_announcement_like")
public class AnnouncementLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long announcementId;
    private Long userId;
    private LocalDateTime createTime;
}
