package com.leegop.samewave.service.support;

import com.leegop.samewave.common.constant.PostStatus;
import com.leegop.samewave.entity.Post;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 帖子可见性判断：所有读入口（详情/列表/评论/Feed/搜索）统一走这里，
 * 避免规则分散在 SQL 和 Java 两处、慢慢不一致
 */
@Component
public class PostAccessChecker {

    public static final int VISIBILITY_PUBLIC = 0;
    public static final int VISIBILITY_FANS_ONLY = 1;
    public static final int VISIBILITY_PRIVATE = 2;

    public boolean canView(Post post, Long currentUserId) {
        if (post == null) {
            return false;
        }

        // 1. 作者本人：草稿 / 已下架 / 私密 全都可见
        if (Objects.equals(post.getUserId(), currentUserId)) {
            return true;
        }

        // 2. 非作者：必须是已发布
        if (!PostStatus.PUBLISHED.getCode().equals(post.getStatus())) {
            return false;
        }

        // 3. 非作者：私密帖不可见
        Integer visibility = post.getVisibility();
        if (visibility != null && visibility == VISIBILITY_PRIVATE) {
            return false;
        }

        // TODO 仅粉丝可见(VISIBILITY_FANS_ONLY)：等关注功能做完，
        //      用 UserFollowMapper.countFollow(currentUserId, post.getUserId()) > 0 放行
        return true;
    }
}