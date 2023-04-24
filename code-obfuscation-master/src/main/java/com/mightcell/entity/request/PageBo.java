package com.mightcell.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: MightCell
 * @description: 接收分页信息
 * @date: Created in 22:11 2023-03-08
 */
@Data
public class PageBo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer page;
    private Integer limit;
    private String fileName;
}
