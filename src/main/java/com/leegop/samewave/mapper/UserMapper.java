package com.leegop.samewave.mapper;

import com.leegop.samewave.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User selectByUsername(String username);

    void insert(User user);

    void updateLastLoginTime(Long id);

    User selectById(Long id);
}
