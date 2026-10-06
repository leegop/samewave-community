package com.leegop.samewave.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PostCreateDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题最长 100 字")
    private String title;

    @NotBlank(message = "正文不能为空")
    @Size(max = 20000, message = "正文过长")
    private String content;

    private String coverImage;

    // TODO 可见性选择：待开放「0公开 1仅粉丝可见 2私密」
    //   开放时需同步两处：新增 visibility 字段（默认 0）、PostMapper.insert 带上该列
    //   当前固定走数据库默认值 0（公开），前端无法选择

    /**
     * 对集合里每个元素也做校验，这是 jakarta validation 的容器元素校验
     */
    @Size(max = 5, message = "最多选择 5 个标签")
    private List<@NotBlank(message = "标签不能为空")
    @Size(max = 32, message = "标签最长 32 字") String> tags;


}