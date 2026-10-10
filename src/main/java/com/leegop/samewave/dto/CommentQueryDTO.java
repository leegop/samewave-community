package com.leegop.samewave.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CommentQueryDTO {

    @Min(value = 1, message = "页码不能小于 1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 50, message = "每页最多 50 条")
    private Integer pageSize = 10;

    /** 评论列表用：查哪个帖子的评论 */
    private Long postId;

    /** 回复列表用：查哪条顶层评论下的回复 */
    private Long rootId;
}