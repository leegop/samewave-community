package com.leegop.samewave.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leegop.samewave.common.constant.AuditStatus;
import com.leegop.samewave.common.constant.PostStatus;
import com.leegop.samewave.common.context.UserContext;
import com.leegop.samewave.common.exception.BusinessException;
import com.leegop.samewave.common.result.PageResult;
import com.leegop.samewave.common.result.ResultCode;
import com.leegop.samewave.dto.PostCreateDTO;
import com.leegop.samewave.dto.PostQueryDTO;
import com.leegop.samewave.dto.PostTagNameDTO;
import com.leegop.samewave.entity.*;
import com.leegop.samewave.mapper.*;
import com.leegop.samewave.service.PostService;
import com.leegop.samewave.vo.PostDetailVO;
import com.leegop.samewave.vo.PostListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostContentMapper postContentMapper;
    private final TagMapper tagMapper;
    private final PostTagMapper postTagMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostDetailVO createPost(Long userId, PostCreateDTO dto) {
        // 1. 帖子主表：插入后自增ID 会回填到 post.id
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(dto.getTitle());
        post.setCoverImage(dto.getCoverImage() == null ? "" : dto.getCoverImage());
        post.setStatus(PostStatus.PUBLISHED.getCode());
        post.setAuditStatus(AuditStatus.PENDING.getCode());
        post.setPublishTime(LocalDateTime.now());
        postMapper.insert(post);

        // TODO 阶段2：发送 MQ 消息，由审核消费者异步处理
        // 回写审核结果时必须带 WHERE audit_status = 0 来保证幂等

        // 2. 正文单独一张表，用上一步拿到的 postId
        PostContent content = new PostContent();
        content.setPostId(post.getId());
        content.setContent(dto.getContent());
        postContentMapper.insert(content);

        // 3. 标签
        saveTags(post.getId(), dto.getTags());

        // 4. 复用详情逻辑返回给前端
        return getDetail(post.getId());
    }

    @Override
    public PostDetailVO getDetail(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 草稿 / 已下架的帖子只有作者本人能看，其他人一律按不存在处理
        boolean visible = PostStatus.PUBLISHED.getCode().equals(post.getStatus());
        if (!visible && !Objects.equals(post.getUserId(), UserContext.getUserId())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // TODO 可见性 visibility 尚未接入，当前只区分「已发布 / 非已发布」：
        //   0 公开   -> 已发布即可见                      ✅ 符合预期
        //   1 仅粉丝 -> 还应校验「我是否关注了作者」        ⚠️ 未校验
        //   2 私密   -> 仅作者可见                        ❌ 非作者目前也能看到，待修
        //   改造时按这个顺序（不可颠倒）：
        //     作者本人放行 -> status 必须 PUBLISHED -> 再按 visibility 分支
        //   依赖：PostVisibility 枚举 + UserFollowMapper.countFollow(userId, followUserId)
        //   注意：列表 / Feed / 搜索等所有读入口都要用同一套规则，否则会在列表页泄露

        PostContent content = postContentMapper.selectByPostId(id);
        List<String> tags = postTagMapper.selectTagNamesByPostId(id);
        User author = userMapper.selectById(post.getUserId());

        PostDetailVO vo = new PostDetailVO();
        vo.setId(post.getId());
        vo.setAuthorId(post.getUserId());
        vo.setTitle(post.getTitle());
        vo.setContent(content == null ? "" : content.getContent());
        vo.setCoverImage(post.getCoverImage());
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setCollectCount(post.getCollectCount());
        vo.setViewCount(post.getViewCount());
        vo.setTags(tags);
        vo.setPublishTime(post.getPublishTime());
        vo.setCreateTime(post.getCreateTime());

        vo.setStatus(post.getStatus());
        // TODO auditStatus / auditRemark 应只对作者本人返回，避免向他人泄露审核信息
        vo.setAuditStatus(post.getAuditStatus());

        if (author != null) {
            vo.setAuthorNickname(author.getNickname());
            vo.setAuthorAvatar(author.getAvatar());
        }
        return vo;
    }

    @Override
    public PageResult<PostListVO> listPosts(PostQueryDTO query) {
        Long currentUserId = UserContext.getUserId();

        // PageHelper.startPage 只对「紧接着的那一次查询」生效
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<PostListVO> posts = postMapper.selectPage(query, currentUserId);

        fillTags(posts);
        return PageResult.of(new PageInfo<>(posts));
    }

    @Override
    public PageResult<PostListVO> listMyPosts(PostQueryDTO query) {
        // 注意：即使前端传了 userId 也会被忽略，只查当前登录用户
        Long currentUserId = UserContext.getUserId();

        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<PostListVO> posts = postMapper.selectMyPage(query, currentUserId);

        fillTags(posts);
        return PageResult.of(new PageInfo<>(posts));
    }

    /**
     * 批量补标签
     */
    private void fillTags(List<PostListVO> posts) {
        if (CollectionUtils.isEmpty(posts)) {
            return;
        }

        List<Long> postIds = posts.stream().map(PostListVO::getId).toList();
        Map<Long, List<String>> tagMap = postTagMapper.selectByPostIds(postIds).stream()
                .collect(Collectors.groupingBy(
                        PostTagNameDTO::getPostId,
                        Collectors.mapping(PostTagNameDTO::getTagName, Collectors.toList())));

        posts.forEach(post -> post.setTags(tagMap.getOrDefault(post.getId(), List.of())));
    }

    /**
     * 处理标签：标签可复用，不存在才新建
     */
    private void saveTags(Long postId, List<String> tagNames) {
        if (CollectionUtils.isEmpty(tagNames)) {
            return;
        }
        // 去空格 + 去重，避免同一个帖子重复绑同一个标签触发唯一索引冲突
        List<String> names = tagNames.stream()
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .distinct()
                .toList();
        if (names.isEmpty()) {
            // trim 后可能全空，提前收口
            return;
        }
        // 一次查询捞出所有已存在的标签
        Map<String, Tag> nameToTag = tagMapper.selectByNames(names).stream()
                .collect(Collectors.toMap(Tag::getName, tag -> tag));
        List<PostTag> postTags = new ArrayList<>();
        for (String name : names) {
            Tag tag = nameToTag.get(name);
            if (tag == null) {
                Tag newTag = new Tag();
                newTag.setName(name);
                newTag.setPostCount(1);
                try {
                    tagMapper.insert(newTag);
                    tag = newTag;
                } catch (DuplicateKeyException e) {
                    // 并发下别的请求抢先创建了同名标签，重新查一次
                    tag = tagMapper.selectByName(name);
                    tagMapper.incrPostCount(tag.getId());
                }
            } else {
                tagMapper.incrPostCount(tag.getId());
            }
            PostTag postTag = new PostTag();
            postTag.setPostId(postId);
            postTag.setTagId(tag.getId());
            postTags.add(postTag);
        }
        if (!CollectionUtils.isEmpty(postTags)) {
            postTagMapper.batchInsert(postTags);
        }
    }
}
