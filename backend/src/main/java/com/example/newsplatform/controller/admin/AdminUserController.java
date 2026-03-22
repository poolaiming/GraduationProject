package com.example.newsplatform.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.newsplatform.common.ApiResponse;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/page")
    public ApiResponse<Page<User>> page(@RequestParam(value = "id", required = false) Long id,
                                        @RequestParam(value = "username", required = false) String username,
                                        @RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>();
        if (id != null) {
            wrapper.eq(User::getId, id);
        }
        if (username != null && !username.isBlank()) {
            wrapper.like(User::getUsername, username);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(User::getNickname, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return ApiResponse.success(userService.page(Page.of(pageNum, pageSize), wrapper));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateStatus(@PathVariable("id") Long id, @RequestParam("status") Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        return ApiResponse.success(userService.updateById(user));
    }

    /**
     * 新闻工作者审核状态：0非新闻工作者 1已通过 2待审核 3已拒绝
     */
    @PutMapping("/{id}/journalist-status")
    public ApiResponse<Boolean> updateJournalistStatus(@PathVariable("id") Long id,
                                                       @RequestParam("journalistStatus") Integer journalistStatus) {
        if (journalistStatus == null || journalistStatus < 0 || journalistStatus > 3) {
            throw new IllegalArgumentException("新闻工作者状态不合法");
        }
        User user = new User();
        user.setId(id);
        user.setIsJournalist(journalistStatus);
        return ApiResponse.success(userService.updateById(user));
    }

    /**
     * 管理员删除用户（包含其所有业务数据）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") Long id) {
        return ApiResponse.success(userService.removeUserWithAllData(id));
    }
}
