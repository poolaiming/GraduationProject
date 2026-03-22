package com.example.newsplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_survey_answer_detail")
public class SurveyAnswerDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long answerId;
    private Long questionId;
    private Long optionId;
    private String answerText;
}

