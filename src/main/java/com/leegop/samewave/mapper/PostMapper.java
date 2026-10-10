package com.leegop.samewave.mapper;

import com.leegop.samewave.dto.PostQueryDTO;
import com.leegop.samewave.entity.Post;
import com.leegop.samewave.vo.PostListVO;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    void insert(Post post);

    Post selectById(Long id);

    List<PostListVO> selectPage(PostQueryDTO query, Long currentUserId);

    List<PostListVO> selectMyPage(PostQueryDTO query, Long currentUserId);

    void incrCommentCount(Long postId);

    void decrCommentCount(Long postId, int i);
}
