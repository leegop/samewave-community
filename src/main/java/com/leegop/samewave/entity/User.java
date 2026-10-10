package com.leegop.samewave.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;//用户ID
    private String username;//登录名
    private String password;//密码
    private String nickname;//昵称
    private String avatar;//头像URL
    private String coverImage;//个人主页背景图URL
    private String email;//邮箱
    private String phone;//手机号
    private Integer gender;//性别
    private String bio;//个人简介
    private String location;//所在地
    private String website;//个人主页
    private Integer role;//角色权限
    private Integer status;//账号状态
    private LocalDateTime muteUntil;//禁言到期时间
    private Integer followCount;//关注数
    private Integer fansCount;//粉丝数
    private Integer postCount;//发帖数
    private LocalDateTime passwordUpdateTime;//密码修改时间
    private LocalDateTime lastLoginAt;//最后登录时间
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
    private Integer deleted;//逻辑删除
}