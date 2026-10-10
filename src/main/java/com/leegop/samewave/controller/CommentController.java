package com.leegop.samewave.controller;

import com.leegop.samewave.common.context.UserContext;
import com.leegop.samewave.common.result.PageResult;
import com.leegop.samewave.common.result.Result;
import com.leegop.samewave.dto.CommentCreateDTO;
import com.leegop.samewave.dto.CommentQueryDTO;
import com.leegop.samewave.service.CommentService;
import com.leegop.samewave.vo.CommentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 发表评论 / 回复：传 parentId 就是回复，不传就是顶层评论
     */
    @PostMapping
    public Result<CommentVO> create(@Valid @RequestBody CommentCreateDTO dto) {
        return Result.success(commentService.create(UserContext.getUserId(), dto));
    }

    /**
     * 帖子评论列表（顶层评论分页，每条带前 2 条回复预览）
     */
    @GetMapping("/list")
    public Result<PageResult<CommentVO>> list(@Valid CommentQueryDTO query) {
        return Result.success(commentService.listByPost(query));
    }

    /**
     * 某条顶层评论下的全部回复（分页）
     */
    @GetMapping("/reply/list")
    public Result<PageResult<CommentVO>> replies(@Valid CommentQueryDTO query) {
        return Result.success(commentService.listReplies(query));
    }

    /**
     * 删除评论：评论作者 或 帖子作者
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return Result.success();
    }
}
