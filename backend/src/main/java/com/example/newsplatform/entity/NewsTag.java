package com.example.newsplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_news_tag")
public class NewsTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long newsId;
    private Long tagId;
}
