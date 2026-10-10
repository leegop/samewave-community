package com.leegop.samewave.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    NORMAL(0, "正常"),
    BANNED(1, "已被封禁");

    private final Integer code;
    private final String desc;

    /**
     * 根据 code 反查枚举
     */
    public static UserStatus of(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}