package com.leegop.samewave.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {

    private Long id;
    private Long postId;
    private Long userId;
    /** 直接父评论ID，0 表示顶层评论 */
    private Long parentId;
    /** 顶层评论ID，0 表示自己就是顶层 */
    private Long rootId;
    /** 被回复用户ID，0 表示没有（顶层评论） */
    private Long replyUserId;
    private String content;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}