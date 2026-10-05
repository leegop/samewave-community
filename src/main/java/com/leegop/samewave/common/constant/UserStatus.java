package com.leegop.samewave.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    NORMAL(0, "正常"),
    MUTED(1, "已被禁言"),
    BANNED(2, "已被封禁"),
    CANCELLED( 3 , "已注销" );

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