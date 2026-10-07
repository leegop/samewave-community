package com.leegop.samewave.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子列表项
 * 注意：不含正文。
 */
@Data
public class PostListVO {

    private Long id;
    private Long userId;
    private String title;
    private String coverImage;

    private String authorNickname;
    private String authorAvatar;

    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private Integer viewCount;

    private List<String> tags;

    private LocalDateTime publishTime;
}