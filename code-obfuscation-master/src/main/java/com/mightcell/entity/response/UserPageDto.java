package com.mightcell.entity.response;

import com.baomidou.mybatisplus.annotation.TableId;
import com.mightcell.entity.User;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserPageDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;

    private String phone;

    private String email;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;

    @TableId(value = "update_file_num")
    private Integer uploadFileNum;

    @TableId(value = "api_protect_num")
    private Integer apiProductNum;
}
