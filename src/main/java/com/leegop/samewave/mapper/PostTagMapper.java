package com.leegop.samewave.mapper;

import com.leegop.samewave.dto.PostTagNameDTO;
import com.leegop.samewave.entity.PostTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.List;

@Mapper
public interface PostTagMapper {
    void batchInsert(List<PostTag> postTags);

    List<String> selectTagNamesByPostId(Long postId);

    List <PostTagNameDTO> selectByPostIds(List<Long> postIds);
}
