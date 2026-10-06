package com.leegop.samewave.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDetailVO {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String coverImage;

    private String authorNickname;
    private String authorAvatar;

    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private Integer viewCount;

    private List<String> tags;

    private LocalDateTime publishTime;
    private LocalDateTime createTime;

    private Integer status;
    private Integer auditStatus;
}