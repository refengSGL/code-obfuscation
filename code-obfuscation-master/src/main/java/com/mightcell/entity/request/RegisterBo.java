package com.mightcell.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 20:27 2023-02-22
 */
@Data
public class RegisterBo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String email;
    private String phone;
}
