package com.leegop.samewave.controller;

import com.leegop.samewave.common.context.UserContext;
import com.leegop.samewave.common.result.PageResult;
import com.leegop.samewave.common.result.Result;
import com.leegop.samewave.dto.PostCreateDTO;
import com.leegop.samewave.dto.PostQueryDTO;
import com.leegop.samewave.service.PostService;
import com.leegop.samewave.vo.PostDetailVO;
import com.leegop.samewave.vo.PostListVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 发帖：用户ID 从 token 里取，不用前端传
     */
    @PostMapping
    public Result<PostDetailVO> create(@Valid @RequestBody PostCreateDTO dto) {
        return Result.success(postService.createPost(UserContext.getUserId(), dto));
    }

    /**
     * 帖子详情
     */
    @GetMapping("/{id}")
    public Result<PostDetailVO> detail(@PathVariable Long id) {
        return Result.success(postService.getDetail(id));
    }

    /**
     * 帖子列表（分页 + 过滤）
     */
    @GetMapping("/list")
    public Result<PageResult<PostListVO>> list(@Valid PostQueryDTO query) {
        return Result.success(postService.listPosts(query));
    }

    /**
     * 我的帖子：草稿箱(status=0) / 已发布(status=1) / 已下架(status=2)，
     * 不传 status 则查全部
     */
    @GetMapping("/my")
    public Result<PageResult<PostListVO>> myPosts(@Valid PostQueryDTO query) {
        return Result.success(postService.listMyPosts(query));
    }
}