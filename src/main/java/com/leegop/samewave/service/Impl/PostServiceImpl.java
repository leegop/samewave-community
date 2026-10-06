package com.leegop.samewave.service.Impl;

import com.leegop.samewave.common.constant.PostStatus;
import com.leegop.samewave.common.exception.BusinessException;
import com.leegop.samewave.common.result.ResultCode;
import com.leegop.samewave.dto.PostCreateDTO;
import com.leegop.samewave.entity.*;
import com.leegop.samewave.mapper.*;
import com.leegop.samewave.service.PostService;
import com.leegop.samewave.vo.PostDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        post.setStatus(PostStatus.AUDITING.getCode());
        post.setPublishTime(LocalDateTime.now());
        postMapper.insert(post);

        //TODO 异步对帖子进行审核

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

        PostContent content = postContentMapper.selectByPostId(id);
        List<String> tags = postTagMapper.selectTagNamesByPostId(id);   // ← 就改了这里
        User author = userMapper.selectById(post.getUserId());

        PostDetailVO vo = new PostDetailVO();
        vo.setId(post.getId());
        vo.setUserId(post.getUserId());
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

        if (author != null) {
            vo.setAuthorNickname(author.getNickname());
            vo.setAuthorAvatar(author.getAvatar());
        }
        return vo;
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
            return ;
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
            PostTag postTag = new PostTag ();
            postTag.setPostId(postId);
            postTag.setTagId(tag.getId());
            postTags.add(postTag);
        }
        if (!CollectionUtils.isEmpty(postTags)) {
            postTagMapper.batchInsert(postTags);
        }
    }
}
