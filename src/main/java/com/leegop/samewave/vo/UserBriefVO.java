package com.leegop.samewave.vo;

import lombok.Data;

/** 作者信息精简版，用于帖子/评论里展示昵称头像 */
@Data
public class UserBriefVO {

    private Long id;
    private String nickname;
    private String avatar;
}