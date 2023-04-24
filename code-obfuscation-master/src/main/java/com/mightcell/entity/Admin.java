package com.mightcell.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author MightCell
 * @Date 2023-03-29 21:54
 * @PackageName:com.mightcell.entity
 * @ClassName: Admin
 * @Description: Admin
 * @Version 1.0
 */
@Data
@TableName("admin")
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private String phone;

    private String email;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
