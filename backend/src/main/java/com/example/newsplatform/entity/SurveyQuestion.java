package com.example.newsplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_survey_question")
public class SurveyQuestion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long surveyId;
    private String title;
    /**
     * 题目类型：1单选 2多选 3判断
     */
    private Integer questionType;
    private Integer required;
    private Integer score;
    private Integer orderNo;
}

