package com.leegop.samewave.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leegop.samewave.common.context.UserContext;
import com.leegop.samewave.common.exception.BusinessException;
import com.leegop.samewave.common.result.PageResult;
import com.leegop.samewave.common.result.ResultCode;
import com.leegop.samewave.dto.CommentCreateDTO;
import com.leegop.samewave.dto.CommentQueryDTO;
import com.leegop.samewave.entity.Comment;
import com.leegop.samewave.entity.Post;
import com.leegop.samewave.entity.User;
import com.leegop.samewave.mapper.CommentMapper;
import com.leegop.samewave.mapper.PostMapper;
import com.leegop.samewave.mapper.UserMapper;
import com.leegop.samewave.service.CommentService;
import com.leegop.samewave.service.support.PostAccessChecker;
import com.leegop.samewave.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final long ROOT_COMMENT = 0L;

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final PostAccessChecker postAccessChecker;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO create(Long userId, CommentCreateDTO dto) {
        // 评论前先确定帖子是否可见：看不了的帖子也不该能评论
        Post post = postMapper.selectById(dto.getPostId());
        if (!postAccessChecker.canView(post, userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        long rootId = ROOT_COMMENT;
        long replyUserId = ROOT_COMMENT;
        long parentId = ROOT_COMMENT;

        if (dto.getParentId() != null && dto.getParentId() > 0) {
            Comment parent = commentMapper.selectById(dto.getParentId());
            //父评论必须存在、且属于同一片帖子
            if (parent == null || !parent.getPostId().equals(dto.getPostId())) {
                throw new BusinessException("回复的评论不存在");
            }

            parentId = parent.getId();
            //父评论是顶层评论->root_id就是它自己；父评论本身是回复->继承它的root_id
            rootId = parent.getRootId().equals(ROOT_COMMENT) ? parent.getId() : parent.getRootId();
            replyUserId = parent.getUserId();
        }

        Comment comment = new Comment();
        comment.setPostId(dto.getPostId());
        comment.setUserId(userId);
        comment.setParentId(parentId);
        comment.setRootId(rootId);
        comment.setReplyUserId(replyUserId);
        comment.setContent(dto.getContent());
        commentMapper.insert(comment);

        postMapper.incrCommentCount(dto.getPostId());

        return buildVO(commentMapper.selectById(comment.getId()));
    }

    @Override
    public PageResult<CommentVO> listByPost(CommentQueryDTO query) {
        if (query.getPostId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }

        // 帖子不可见时按「不存在」处理，不要返回空列表——否则会泄露「这个ID有帖子」
        Post post = postMapper.selectById(query.getPostId());
        if (!postAccessChecker.canView(post, UserContext.getUserId())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<CommentVO> comments = commentMapper.selectTopPage(query.getPostId());

        // 整页顶层评论一次查出回复预览，避免每条评论查一次
        if (!comments.isEmpty()) {
            List<Long> rootIds = comments.stream().map(CommentVO::getId).toList();
            Map<Long, List<CommentVO>> previewMap = commentMapper.selectReplyPreviews(query.getPostId(), rootIds).stream()
                    .collect(Collectors.groupingBy(CommentVO::getRootId));

            comments.forEach(c -> c.setPreviewReplies(previewMap.getOrDefault(c.getId(), List.of())));
        }
        return PageResult.of(new PageInfo<>(comments));
    }

    @Override
    public PageResult<CommentVO> listReplies(CommentQueryDTO query) {
        if (query.getRootId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Comment root = commentMapper.selectById(query.getRootId());
        if (root == null || !root.getRootId().equals(ROOT_COMMENT)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        Post post = postMapper.selectById(root.getPostId());
        if (!postAccessChecker.canView(post, UserContext.getUserId())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        } // 带上 postId 是为了命中 idx_post_root_time 这个联合索引
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<CommentVO> replies = commentMapper.selectRepliesPage(root.getPostId(), root.getId());
        return PageResult.of(new PageInfo<>(replies));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        Post post = postMapper.selectById(comment.getPostId());
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        } // 权限：评论作者本人，或帖子的作者（楼主有权清理自己帖子下的评论）
        Long currentUserId = UserContext.getUserId();
        boolean isCommentOwner = Objects.equals(comment.getUserId(), currentUserId);
        boolean isPostOwner = Objects.equals(post.getUserId(), currentUserId);
        if (!isCommentOwner && !isPostOwner) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        if (comment.getRootId().equals(ROOT_COMMENT)) {
            // 删顶层评论：连同它下面的回复一起软删，计数也要把回复一起减掉
            int replyCount = commentMapper.countByRootId(comment.getPostId(), comment.getId());
            commentMapper.softDeleteById(comment.getId());
            commentMapper.softDeleteByRootId(comment.getPostId(), comment.getId());
            postMapper.decrCommentCount(comment.getPostId(), 1 + replyCount);
        } else {
            commentMapper.softDeleteById(comment.getId());
            postMapper.decrCommentCount(comment.getPostId(), 1);
        }
    }

    private CommentVO buildVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setPostId(comment.getPostId());
        vo.setUserId(comment.getUserId());
        vo.setContent(comment.getContent());
        vo.setParentId(comment.getParentId());
        vo.setRootId(comment.getRootId());
        vo.setReplyUserId(comment.getReplyUserId());
        vo.setLikeCount(comment.getLikeCount());
        vo.setCreateTime(comment.getCreateTime());
        User author = userMapper.selectById(comment.getUserId());
        if (author != null) {
            vo.setAuthorNickname(author.getNickname());
            vo.setAuthorAvatar(author.getAvatar());
        }
        // replyUserId = 0 表示顶层评论，没有「回复 @谁」
        if (comment.getReplyUserId() != null && comment.getReplyUserId() > 0) {
            User replyUser = userMapper.selectById(comment.getReplyUserId());
            if (replyUser != null) {
                vo.setReplyUserNickname(replyUser.getNickname());
            }
        }
        return vo;
    }
}
