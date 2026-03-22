package com.example.newsplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_survey_option")
public class SurveyOption {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionId;
    private String optionLabel;
    private String optionContent;
    private Integer orderNo;
}

