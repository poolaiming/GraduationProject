package com.example.newsplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.*;
import com.example.newsplatform.mapper.*;
import com.example.newsplatform.service.SurveyService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;
    private final SurveyQuestionMapper surveyQuestionMapper;
    private final SurveyOptionMapper surveyOptionMapper;
    private final SurveyAnswerMapper surveyAnswerMapper;
    private final SurveyAnswerDetailMapper surveyAnswerDetailMapper;

    @GetMapping("/page")
    public ApiResponse<Page<Survey>> page(@RequestParam(value = "keyword", required = false) String keyword,
                                          @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        LambdaQueryWrapper<Survey> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Survey::getTitle, keyword);
        }
        wrapper.eq(Survey::getStatus, 1);
        wrapper.and(w -> w.isNull(Survey::getStartTime).or().le(Survey::getStartTime, LocalDateTime.now()));
        wrapper.and(w -> w.isNull(Survey::getEndTime).or().ge(Survey::getEndTime, LocalDateTime.now()));
        wrapper.orderByDesc(Survey::getCreateTime);
        return ApiResponse.success(surveyService.page(Page.of(pageNum, pageSize), wrapper));
    }

    @GetMapping("/{id}/detail")
    public ApiResponse<Map<String, Object>> detail(@PathVariable("id") Long id) {
        Survey survey = surveyService.getById(id);
        if (survey == null) throw new IllegalArgumentException("问卷不存在");

        List<SurveyQuestion> questions = surveyQuestionMapper.selectList(
                new LambdaQueryWrapper<SurveyQuestion>()
                        .eq(SurveyQuestion::getSurveyId, id)
                        .orderByAsc(SurveyQuestion::getOrderNo));

        List<Long> questionIds = questions.stream().map(SurveyQuestion::getId).collect(Collectors.toList());
        Map<Long, List<SurveyOption>> optionMap = new HashMap<>();
        if (!questionIds.isEmpty()) {
            List<SurveyOption> allOptions = surveyOptionMapper.selectList(
                    new LambdaQueryWrapper<SurveyOption>()
                            .in(SurveyOption::getQuestionId, questionIds)
                            .orderByAsc(SurveyOption::getOrderNo));
            for (SurveyOption opt : allOptions) {
                optionMap.computeIfAbsent(opt.getQuestionId(), k -> new ArrayList<>()).add(opt);
            }
        }

        List<Map<String, Object>> questionList = questions.stream().map(q -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", q.getId());
            m.put("title", q.getTitle());
            m.put("questionType", q.getQuestionType());
            m.put("required", q.getRequired());
            m.put("options", optionMap.getOrDefault(q.getId(), Collections.emptyList()));
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", survey.getId());
        result.put("title", survey.getTitle());
        result.put("description", survey.getDescription());
        result.put("startTime", survey.getStartTime());
        result.put("endTime", survey.getEndTime());
        result.put("questions", questionList);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}/check")
    public ApiResponse<Boolean> checkSubmitted(@PathVariable("id") Long id,
                                               @RequestParam("userId") Long userId) {
        long count = surveyAnswerMapper.selectCount(
                new LambdaQueryWrapper<SurveyAnswer>()
                        .eq(SurveyAnswer::getSurveyId, id)
                        .eq(SurveyAnswer::getUserId, userId));
        return ApiResponse.success(count > 0);
    }

    @PostMapping("/{id}/answer")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> submitAnswer(@PathVariable("id") Long id,
                                             @RequestBody SubmitAnswerRequest request) {
        if (request.getUserId() == null) throw new IllegalArgumentException("用户ID不能为空");

        long count = surveyAnswerMapper.selectCount(
                new LambdaQueryWrapper<SurveyAnswer>()
                        .eq(SurveyAnswer::getSurveyId, id)
                        .eq(SurveyAnswer::getUserId, request.getUserId()));
        if (count > 0) throw new IllegalArgumentException("您已经提交过该问卷");

        SurveyAnswer answer = new SurveyAnswer();
        answer.setSurveyId(id);
        answer.setUserId(request.getUserId());
        answer.setSubmitTime(LocalDateTime.now());
        surveyAnswerMapper.insert(answer);

        if (request.getAnswers() != null) {
            for (AnswerItem item : request.getAnswers()) {
                if (item.getOptionIds() != null) {
                    for (Long optionId : item.getOptionIds()) {
                        SurveyAnswerDetail detail = new SurveyAnswerDetail();
                        detail.setAnswerId(answer.getId());
                        detail.setQuestionId(item.getQuestionId());
                        detail.setOptionId(optionId);
                        surveyAnswerDetailMapper.insert(detail);
                    }
                }
                if (item.getAnswerText() != null && !item.getAnswerText().isBlank()) {
                    SurveyAnswerDetail detail = new SurveyAnswerDetail();
                    detail.setAnswerId(answer.getId());
                    detail.setQuestionId(item.getQuestionId());
                    detail.setAnswerText(item.getAnswerText().trim());
                    surveyAnswerDetailMapper.insert(detail);
                }
            }
        }
        return ApiResponse.success(true);
    }

    @GetMapping("/{id}/stats")
    public ApiResponse<Map<String, Object>> stats(@PathVariable("id") Long id) {
        Survey survey = surveyService.getById(id);
        if (survey == null) throw new IllegalArgumentException("问卷不存在");

        long totalAnswers = surveyAnswerMapper.selectCount(
                new LambdaQueryWrapper<SurveyAnswer>().eq(SurveyAnswer::getSurveyId, id));

        List<SurveyQuestion> questions = surveyQuestionMapper.selectList(
                new LambdaQueryWrapper<SurveyQuestion>()
                        .eq(SurveyQuestion::getSurveyId, id)
                        .orderByAsc(SurveyQuestion::getOrderNo));

        List<Long> questionIds = questions.stream().map(SurveyQuestion::getId).collect(Collectors.toList());
        Map<Long, List<SurveyOption>> optionMap = new HashMap<>();
        if (!questionIds.isEmpty()) {
            List<SurveyOption> allOptions = surveyOptionMapper.selectList(
                    new LambdaQueryWrapper<SurveyOption>().in(SurveyOption::getQuestionId, questionIds));
            for (SurveyOption opt : allOptions) {
                optionMap.computeIfAbsent(opt.getQuestionId(), k -> new ArrayList<>()).add(opt);
            }
        }

        List<SurveyAnswerDetail> allDetails = new ArrayList<>();
        if (!questionIds.isEmpty()) {
            allDetails = surveyAnswerDetailMapper.selectList(
                    new LambdaQueryWrapper<SurveyAnswerDetail>().in(SurveyAnswerDetail::getQuestionId, questionIds));
        }
        Map<Long, Long> optionCountMap = new HashMap<>();
        for (SurveyAnswerDetail d : allDetails) {
            if (d != null && d.getOptionId() != null) {
                optionCountMap.merge(d.getOptionId(), 1L, Long::sum);
            }
        }

        List<Map<String, Object>> questionStats = questions.stream().map(q -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", q.getId());
            m.put("title", q.getTitle());
            m.put("questionType", q.getQuestionType());
            List<Map<String, Object>> optionStats = optionMap.getOrDefault(q.getId(), Collections.emptyList())
                    .stream().map(opt -> {
                        Map<String, Object> om = new LinkedHashMap<>();
                        om.put("id", opt.getId());
                        om.put("optionLabel", opt.getOptionLabel());
                        om.put("optionContent", opt.getOptionContent());
                        om.put("count", optionCountMap.getOrDefault(opt.getId(), 0L));
                        return om;
                    }).collect(Collectors.toList());
            m.put("options", optionStats);
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("surveyId", id);
        result.put("title", survey.getTitle());
        result.put("totalAnswers", totalAnswers);
        result.put("questions", questionStats);
        return ApiResponse.success(result);
    }

    @Data
    private static class SubmitAnswerRequest {
        private Long userId;
        private List<AnswerItem> answers;
    }

    @Data
    private static class AnswerItem {
        private Long questionId;
        private List<Long> optionIds;
        private String answerText;
    }
}
