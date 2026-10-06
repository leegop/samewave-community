package com.leegop.samewave.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {

    private Long id;
    private Long userId;
    private String title;
    private String coverImage;
    private Integer status;
    private Integer visibility;
    private Integer isTop;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private Integer viewCount;
    private Integer auditStatus;
    private LocalDateTime auditTime;
    private String auditRemark;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}