package com.leegop.samewave.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditStatus {

    PENDING(0, "待审核"),
    PASS(1, "审核通过"),
    REJECT(2, "审核不通过");

    private final Integer code;
    private final String desc;
}