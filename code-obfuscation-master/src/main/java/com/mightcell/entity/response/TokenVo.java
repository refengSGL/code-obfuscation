package com.mightcell.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 21:29 2023-02-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tokenName;
    private String tokenValue;
}
