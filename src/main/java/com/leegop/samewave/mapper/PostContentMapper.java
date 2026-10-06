package com.leegop.samewave.mapper;

import com.leegop.samewave.entity.PostContent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostContentMapper {
    void insert(PostContent content);

    PostContent selectByPostId(Long id);
}
