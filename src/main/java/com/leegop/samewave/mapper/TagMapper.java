package com.leegop.samewave.mapper;

import com.leegop.samewave.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {
    List<Tag> selectByNames(List<String> names);

    void insert(Tag newTag);

    Tag selectByName(String name);

    void incrPostCount(Integer id);


}
