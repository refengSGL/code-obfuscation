package com.mightcell.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author MightCell
 * @Date 2023-03-30 14:30
 * @PackageName:com.mightcell.entity
 * @ClassName: Daily
 * @Description: Daily
 * @Version 1.0
 */
@Data
@TableName("statistics")
public class Daily implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dateCalculated;

    private Integer registerNum;

    private Integer loginNum;

    private Integer fileUpload;

    private Integer fileProtect;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDateTime updateTime;
}
