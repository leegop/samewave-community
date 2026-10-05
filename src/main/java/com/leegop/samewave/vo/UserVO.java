package com.leegop.samewave.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String bio;
    private Integer gender;
    private Integer followCount;
    private Integer fansCount;
    private LocalDateTime createTime;
}