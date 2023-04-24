package com.mightcell.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mightcell.entity.User;
import com.mightcell.entity.response.UserPageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 18:49 2023-02-22
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select u.username,u.id,u.password,u.phone,u.email,u.create_time,u.update_time,"+
            "u.is_login,u.status,u.upload_file_name" +
            "from user u ${ew.customSqlSegment}")
    List<UserPageDto> selectUserDtoPage(@Param("ew") Wrapper<User> wrapper);

//    Page<UserPageDto> selectUserDtoPage(Page<UserPageDto> pageInfo, LambdaQueryWrapper<User> queryWrapper);
}
