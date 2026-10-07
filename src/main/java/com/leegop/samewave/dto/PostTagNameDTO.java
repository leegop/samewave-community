package com.leegop.samewave.dto;

import lombok.Data;

/**
 * 批量查标签用：(postId, tagName) 展开成多行，
 * 一个帖子有多个标签就会对应多行
 */
@Data
public class PostTagNameDTO {

    private Long postId;
    private String tagName;
}