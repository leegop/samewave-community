package com.leegop.samewave.mapper;

import com.leegop.samewave.entity.PostTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostTagMapper {
    void batchInsert(List<PostTag> postTags);

    List<String> selectTagNamesByPostId(Long postId);
}
