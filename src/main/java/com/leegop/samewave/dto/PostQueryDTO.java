package com.leegop.samewave.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PostQueryDTO {

    @Min(value = 1, message = "页码不能小于 1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 50, message = "每页最多 50 条")
    private Integer pageSize = 10;

    /** 只看某个作者的帖子 */
    private Long authorId;

    /** 按标签筛选 */
    private Integer tagId;

    /** 标题关键词。现在是 LIKE，阶段2 换成 ES */
    private String keyword;

    private Integer status;
}