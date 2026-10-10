package com.leegop.samewave.common.result;

import lombok.Getter;

/**
 * 业务错误码。按模块分段：
 * 1xxx 账号与认证，2xxx 关注关系，3xxx 文件
 */
@Getter
public enum BusinessCode {

    // ===== 账号与认证 1xxx =====
    USERNAME_TAKEN(1001, "用户名已被占用"),
    BAD_CREDENTIALS(1002, "用户名或密码错误"),
    ACCOUNT_BANNED(1003, "账号已被封禁"),
    ACCOUNT_CANCELLED(1004, "账号已注销"),
    ACCOUNT_MUTED(1005, "账号已被禁言，暂时无法发言"),
    WRONG_OLD_PASSWORD(1006, "原密码错误"),
    INVALID_CODE(1007, "验证码错误或已过期"),
    EMAIL_TAKEN(1008, "邮箱已被其他账号绑定"),
    PHONE_TAKEN(1009, "手机号已被其他账号绑定"),
    WEAK_PASSWORD(1010, "新密码不符合强度要求"),

    // ===== 关注关系 2xxx =====
    CANNOT_FOLLOW_SELF(2001, "不能关注自己"),
    ALREADY_FOLLOWED(2002, "已经关注过了"),
    NOT_FOLLOWED(2003, "尚未关注该用户"),

    // ===== 文件 3xxx =====
    UNSUPPORTED_FILE_TYPE(3001, "文件类型不支持"),
    FILE_TOO_LARGE(3002, "文件大小超出限制");

    private final Integer code;
    private final String message;

    BusinessCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}