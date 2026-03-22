package com.example.newsplatform.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.Message;
import com.example.newsplatform.service.MessageService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/message")
@RequiredArgsConstructor
public class AdminMessageController {

    private final MessageService messageService;

    @GetMapping("/page")
    public ApiResponse<Page<Message>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return ApiResponse.success(messageService.page(Page.of(pageNum, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Message>()
                        .orderByDesc(Message::getCreateTime)));
    }

    @PutMapping("/{id}/reply")
    public ApiResponse<Boolean> reply(@PathVariable("id") Long id, @RequestBody ReplyRequest request) {
        Message msg = new Message();
        msg.setId(id);
        msg.setReplyContent(request.getReplyContent());
        msg.setStatus(1);
        msg.setReplyTime(LocalDateTime.now());
        return ApiResponse.success(messageService.updateById(msg));
    }

    @Data
    private static class ReplyRequest {
        private String replyContent;
    }
}
