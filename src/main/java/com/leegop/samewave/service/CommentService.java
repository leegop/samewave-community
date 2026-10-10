package com.leegop.samewave.service;

import com.leegop.samewave.common.result.PageResult;
import com.leegop.samewave.dto.CommentCreateDTO;
import com.leegop.samewave.dto.CommentQueryDTO;
import com.leegop.samewave.vo.CommentVO;
import jakarta.validation.Valid;

public interface CommentService {
    CommentVO create(Long userId, CommentCreateDTO dto);

    PageResult<CommentVO> listByPost(CommentQueryDTO query);

    PageResult<CommentVO> listReplies(CommentQueryDTO query);

    void delete(Long id);
}
