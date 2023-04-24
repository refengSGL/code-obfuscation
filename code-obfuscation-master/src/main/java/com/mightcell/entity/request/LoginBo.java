package com.mightcell.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 18:58 2023-02-22
 */
@Data
public class LoginBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;

    private String password;
}
