package com.example.newsplatform.entity;



import com.baomidou.mybatisplus.annotation.IdType;

import com.baomidou.mybatisplus.annotation.TableId;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;



import java.time.LocalDateTime;



@Data

@TableName("t_user")

public class User {



    @TableId(type = IdType.AUTO)

    private Long id;



    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String phone;

    private String email;

    /**

     * 性别：0未知 1男 2女

     */

    private Integer gender;

    /**

     * 状态：1正常 0禁用 2待注销

     */

    private Integer status;

    /**

     * 是否新闻工作者：0否 1是

     */

    private Integer isJournalist;



    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}

