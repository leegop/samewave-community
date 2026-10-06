package com.leegop.samewave.controller;

import com.leegop.samewave.common.context.UserContext;
import com.leegop.samewave.common.result.Result;
import com.leegop.samewave.dto.PostCreateDTO;
import com.leegop.samewave.service.PostService;
import com.leegop.samewave.vo.PostDetailVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /** 发帖：用户ID 从 token 里取，不用前端传 */
    @PostMapping
    public Result<PostDetailVO> create(@Valid @RequestBody PostCreateDTO dto) {
        return Result.success(postService.createPost(UserContext.getUserId(), dto));
    }

    /** 帖子详情 */
    @GetMapping("/{id}")
    public Result<PostDetailVO> detail(@PathVariable Long id) {
        return Result.success(postService.getDetail(id));
    }
}