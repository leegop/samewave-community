package com.leegop.samewave.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostStatus {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布，审核通过"),
    AUDITING(2, "已发布，审核中"),
    REJECTED(3, "审核不通过");

    private final Integer code;
    private final String desc;

}