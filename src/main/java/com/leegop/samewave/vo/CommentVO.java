package com.leegop.samewave.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论（顶层评论和回复共用）
 * 顶层评论会带 replyCount 和 previewReplies（前 2 条回复预览）
 */
@Data
public class CommentVO {

    private Long id;
    private Long postId;
    private Long userId;
    private String content;

    private Long parentId;
    private Long rootId;

    /** 被回复的用户，用于前端显示「回复 @张三」 */
    private Long replyUserId;
    private String replyUserNickname;

    private String authorNickname;
    private String authorAvatar;

    private Integer likeCount;

    /** 顶层评论下的回复总数 */
    private Integer replyCount;

    /** 回复预览，只给前 2 条，避免热评一下拉回几千条 */
    private List<CommentVO> previewReplies;

    private LocalDateTime createTime;
}