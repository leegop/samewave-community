package com.leegop.samewave.mapper;

import com.leegop.samewave.entity.Comment;
import com.leegop.samewave.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.List;

@Mapper
public interface CommentMapper {
    Comment selectById(Long parentId);

    void insert(Comment comment);

    List<CommentVO> selectTopPage(Long postId);

    List<CommentVO> selectReplyPreviews(Long postId, List<Long> rootIds);

    List<CommentVO> selectRepliesPage(Long postId, Long id);

    int countByRootId(Long postId, Long id);

    void softDeleteById(Long id);

    void softDeleteByRootId(Long postId, Long id);
}
