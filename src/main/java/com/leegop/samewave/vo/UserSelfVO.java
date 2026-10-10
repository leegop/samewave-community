package com.leegop.samewave.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 本人资料。仅用于 /me 与登录/注册响应，不含他人视角的 isFollowed / isFans。
 */
@Data
public class UserSelfVO {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String coverImage;
    private String bio;
    private String location;
    private String website;
    private Integer gender;
    private String email;
    private String phone;
    private Integer role;
    private Integer status;
    private LocalDateTime muteUntil;
    private Integer followCount;
    private Integer fansCount;
    private Integer postCount;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createTime;
}