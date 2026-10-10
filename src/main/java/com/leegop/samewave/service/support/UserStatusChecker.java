package com.leegop.samewave.service.support;

import com.leegop.samewave.common.constant.UserStatus;
import com.leegop.samewave.entity.User;

import java.time.LocalDateTime;

/**
 * 用户状态判定。三个维度分开：
 * 注销 -> deleted，封号 -> status，禁言 -> mute_until
 */
public final class UserStatusChecker {

    private UserStatusChecker() {
    }

    /** 能否登录：未被注销、未被封号。禁言仍可登录，只是不能发言 */
    public static boolean canLogin(User user) {
        return user != null && !isCancelled(user) && !isBanned(user);
    }

    /** 能否发言：可登录 且 不在禁言期 */
    public static boolean canSpeak(User user) {
        return canLogin(user) && !isMuted(user);
    }

    public static boolean isCancelled(User user) {
        return user != null && Integer.valueOf(1).equals(user.getDeleted());
    }

    public static boolean isBanned(User user) {
        return user != null && UserStatus.BANNED.getCode().equals(user.getStatus());
    }

    /** 禁言中：mute_until 非空且未到期。*/
    public static boolean isMuted(User user) {
        return user != null
                && user.getMuteUntil() != null
                && user.getMuteUntil().isAfter(LocalDateTime.now());
    }
}