package com.example.newsplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.Message;
import com.example.newsplatform.service.MessageService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 创建留言（前台在线留言）
     */
    @PostMapping
    public ApiResponse<Boolean> create(@RequestBody CreateMessageRequest request) {
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("留言内容不能为空");
        }
        Message message = new Message();
        message.setUserId(request.getUserId());
        message.setContent(request.getContent());
        message.setStatus(0);
        boolean ok = messageService.save(message);
        return ApiResponse.success(ok);
    }

    /**
     * 分页查询当前用户自己的留言
     */
    @GetMapping("/my")
    public ApiResponse<Page<Message>> myMessages(@RequestParam("userId") Long userId,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
                .orderByDesc(Message::getCreateTime);
        Page<Message> page = messageService.page(Page.of(pageNum, pageSize), wrapper);
        return ApiResponse.success(page);
    }

    /**
     * 修改本人发布的留言内容（仅允许未处理状态）
     */
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable("id") Long id, @RequestBody UpdateMessageRequest request) {
        Message message = messageService.getById(id);
        if (message == null) {
            throw new IllegalArgumentException("留言不存在");
        }
        if (request.getUserId() == null || !Objects.equals(message.getUserId(), request.getUserId())) {
            throw new IllegalArgumentException("不能修改他人的留言");
        }
        if (message.getStatus() != null && message.getStatus() != 0) {
            throw new IllegalArgumentException("只有未处理的留言可以修改");
        }
        message.setContent(request.getContent());
        boolean ok = messageService.updateById(message);
        return ApiResponse.success(ok);
    }

    @Data
    private static class CreateMessageRequest {
        private Long userId;
        private String content;
    }

    @Data
    private static class UpdateMessageRequest {
        private Long userId;
        private String content;
    }
}

