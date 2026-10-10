package com.leegop.samewave.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateDTO {

    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论最长 1000 字")
    private String content;

    /**
     * 回复的父评论ID。不传或传 0 表示直接评论帖子（顶层评论）
     * 注意：rootId / replyUserId 由服务端推导，前端不要传
     */
    private Long parentId;
}