package com.leegop.samewave.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostContent {

    private Long postId;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}