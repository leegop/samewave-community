package com.leegop.samewave.service;

import com.leegop.samewave.dto.PostCreateDTO;
import com.leegop.samewave.vo.PostDetailVO;

public interface PostService {
    PostDetailVO createPost(Long userId, PostCreateDTO dto);

    PostDetailVO getDetail(Long id);
}
