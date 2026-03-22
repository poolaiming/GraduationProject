package com.example.newsplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_survey")
public class Survey {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String description;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long createAdmin;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

