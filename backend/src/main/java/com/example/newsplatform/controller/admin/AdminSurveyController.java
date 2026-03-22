package com.example.newsplatform.controller.admin;

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
@RequestMapping("/api/admin/survey")
@RequiredArgsConstructor
public class AdminSurveyController {

    private final SurveyService surveyService;
    private final SurveyQuestionMapper surveyQuestionMapper;
    private final SurveyOptionMapper surveyOptionMapper;
    private final SurveyAnswerMapper surveyAnswerMapper;
    private final SurveyAnswerDetailMapper surveyAnswerDetailMapper;

    @GetMapping("/page")
    public ApiResponse<Page<Survey>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return ApiResponse.success(surveyService.page(Page.of(pageNum, pageSize),
                new LambdaQueryWrapper<Survey>()
                        .orderByDesc(Survey::getCreateTime)));
    }

    @PostMapping
    public ApiResponse<Boolean> create(@RequestBody Survey survey) {
        survey.setId(null);
        normalizeSurveyTime(survey);
        return ApiResponse.success(surveyService.save(survey));
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable("id") Long id, @RequestBody Survey survey) {
        survey.setId(id);
        normalizeSurveyTime(survey);
        return ApiResponse.success(surveyService.updateById(survey));
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> delete(@PathVariable("id") Long id) {
        List<SurveyQuestion> questions = surveyQuestionMapper.selectList(
                new LambdaQueryWrapper<SurveyQuestion>().eq(SurveyQuestion::getSurveyId, id));
        List<Long> questionIds = questions.stream().map(SurveyQuestion::getId).collect(Collectors.toList());
        if (!questionIds.isEmpty()) {
            surveyOptionMapper.delete(new LambdaQueryWrapper<SurveyOption>().in(SurveyOption::getQuestionId, questionIds));
            surveyQuestionMapper.delete(new LambdaQueryWrapper<SurveyQuestion>().eq(SurveyQuestion::getSurveyId, id));
        }
        return ApiResponse.success(surveyService.removeById(id));
    }

    @GetMapping("/{id}/questions")
    public ApiResponse<List<Map<String, Object>>> getQuestions(@PathVariable("id") Long id) {
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

        List<Map<String, Object>> result = questions.stream().map(q -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", q.getId());
            m.put("title", q.getTitle());
            m.put("questionType", q.getQuestionType());
            m.put("required", q.getRequired());
            m.put("score", q.getScore());
            m.put("orderNo", q.getOrderNo());
            m.put("options", optionMap.getOrDefault(q.getId(), Collections.emptyList()));
            return m;
        }).collect(Collectors.toList());

        return ApiResponse.success(result);
    }

    @PostMapping("/{id}/questions")
    public ApiResponse<Boolean> createQuestion(@PathVariable("id") Long id,
                                               @RequestBody QuestionRequest request) {
        SurveyQuestion question = new SurveyQuestion();
        question.setSurveyId(id);
        question.setTitle(request.getTitle());
        question.setQuestionType(request.getQuestionType());
        question.setRequired(request.getRequired() != null ? request.getRequired() : 0);
        question.setScore(request.getScore());
        question.setOrderNo(request.getOrderNo() != null ? request.getOrderNo() : 0);
        surveyQuestionMapper.insert(question);

        if (request.getOptions() != null) {
            for (OptionRequest opt : request.getOptions()) {
                SurveyOption option = new SurveyOption();
                option.setQuestionId(question.getId());
                option.setOptionLabel(opt.getOptionLabel());
                option.setOptionContent(opt.getOptionContent());
                option.setOrderNo(opt.getOrderNo() != null ? opt.getOrderNo() : 0);
                surveyOptionMapper.insert(option);
            }
        }
        return ApiResponse.success(true);
    }

    @PutMapping("/question/{questionId}")
    public ApiResponse<Boolean> updateQuestion(@PathVariable("questionId") Long questionId,
                                               @RequestBody QuestionRequest request) {
        SurveyQuestion question = new SurveyQuestion();
        question.setId(questionId);
        question.setTitle(request.getTitle());
        question.setQuestionType(request.getQuestionType());
        question.setRequired(request.getRequired() != null ? request.getRequired() : 0);
        question.setScore(request.getScore());
        question.setOrderNo(request.getOrderNo() != null ? request.getOrderNo() : 0);
        surveyQuestionMapper.updateById(question);
        return ApiResponse.success(true);
    }

    @DeleteMapping("/question/{questionId}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Boolean> deleteQuestion(@PathVariable("questionId") Long questionId) {
        surveyOptionMapper.delete(new LambdaQueryWrapper<SurveyOption>().eq(SurveyOption::getQuestionId, questionId));
        surveyQuestionMapper.deleteById(questionId);
        return ApiResponse.success(true);
    }

    @PostMapping("/question/{questionId}/options")
    public ApiResponse<Boolean> createOption(@PathVariable("questionId") Long questionId,
                                             @RequestBody OptionRequest request) {
        SurveyOption option = new SurveyOption();
        option.setQuestionId(questionId);
        option.setOptionLabel(request.getOptionLabel());
        option.setOptionContent(request.getOptionContent());
        option.setOrderNo(request.getOrderNo() != null ? request.getOrderNo() : 0);
        surveyOptionMapper.insert(option);
        return ApiResponse.success(true);
    }

    @PutMapping("/option/{optionId}")
    public ApiResponse<Boolean> updateOption(@PathVariable("optionId") Long optionId,
                                             @RequestBody OptionRequest request) {
        SurveyOption option = new SurveyOption();
        option.setId(optionId);
        option.setOptionLabel(request.getOptionLabel());
        option.setOptionContent(request.getOptionContent());
        option.setOrderNo(request.getOrderNo() != null ? request.getOrderNo() : 0);
        surveyOptionMapper.updateById(option);
        return ApiResponse.success(true);
    }

    @DeleteMapping("/option/{optionId}")
    public ApiResponse<Boolean> deleteOption(@PathVariable("optionId") Long optionId) {
        surveyOptionMapper.deleteById(optionId);
        return ApiResponse.success(true);
    }

    @GetMapping("/{id}/stats")
    public ApiResponse<Map<String, Object>> getStats(@PathVariable("id") Long id) {
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
                        double percentage = totalAnswers > 0 ? (optionCountMap.getOrDefault(opt.getId(), 0L) * 100.0 / totalAnswers) : 0;
                        om.put("percentage", String.format("%.1f%%", percentage));
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

    private void normalizeSurveyTime(Survey survey) {
        if (survey.getStartTime() != null && survey.getEndTime() != null
                && !survey.getEndTime().isAfter(survey.getStartTime())) {
            throw new IllegalArgumentException("结束时间必须晚于开始时间");
        }
        if (survey.getStatus() == null) {
            survey.setStatus(0);
        }
        LocalDateTime now = LocalDateTime.now();
        if (survey.getStatus() == 1 && survey.getEndTime() != null && now.isAfter(survey.getEndTime())) {
            survey.setStatus(2);
        }
    }

    @Data
    private static class QuestionRequest {
        private String title;
        private Integer questionType;
        private Integer required;
        private Integer score;
        private Integer orderNo;
        private List<OptionRequest> options;
    }

    @Data
    private static class OptionRequest {
        private String optionLabel;
        private String optionContent;
        private Integer orderNo;
    }
}