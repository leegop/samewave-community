package com.leegop.samewave.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Tag {

    private Integer id;
    private String name;
    private Integer postCount;
    private LocalDateTime createTime;
}