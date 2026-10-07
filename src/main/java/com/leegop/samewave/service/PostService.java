package com.leegop.samewave.service;

import com.leegop.samewave.common.result.PageResult;
import com.leegop.samewave.dto.PostCreateDTO;
import com.leegop.samewave.dto.PostQueryDTO;
import com.leegop.samewave.vo.PostDetailVO;
import com.leegop.samewave.vo.PostListVO;

public interface PostService {
    PostDetailVO createPost(Long userId, PostCreateDTO dto);

    PostDetailVO getDetail(Long id);

    PageResult<PostListVO> listPosts(PostQueryDTO query);

    PageResult<PostListVO> listMyPosts(PostQueryDTO query);
}
