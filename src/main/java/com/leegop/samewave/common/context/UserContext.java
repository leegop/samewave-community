package com.leegop.samewave.common.context;

/**
 * 当前登录用户上下文
 * 基于 ThreadLocal：每个请求由独立线程处理，互不干扰
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    private UserContext () {
    }

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    /** 请求结束必须清理，否则 Tomcat 线程复用会串数据 */
    public static void clear() {
        USER_ID.remove();
    }
}