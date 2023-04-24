package com.mightcell.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 17:39 2023-02-26
 */
@Data
public class ParamBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileName;

    private Integer type;

    private ArrayList<String> paramList;


}
