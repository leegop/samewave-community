package com.leegop.samewave.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostTag {

    private Long id;
    private Long postId;
    private Integer tagId;
    private LocalDateTime createTime;
}