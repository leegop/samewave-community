package com.leegop.samewave.common.constant;

import lombok.Getter;

@Getter
public enum CommentStatus {

    NORMAL(0, "正常"),
    AUDITING(1, "审核中"),
    DELETED(2, "已删除");

    private final Integer code;
    private final String desc;

    CommentStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}