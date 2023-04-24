package com.mightcell.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 21:36 2023-02-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;

    private String phone;

    private String email;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
