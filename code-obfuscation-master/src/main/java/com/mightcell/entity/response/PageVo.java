package com.mightcell.entity.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 22:39 2023-03-08
 */
@Data
public class PageVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer queryFileId;
    private String originalFileName;
    private String resultFileName;
    private LocalDateTime updateTime;
}
