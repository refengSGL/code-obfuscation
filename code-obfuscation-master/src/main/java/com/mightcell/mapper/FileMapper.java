package com.mightcell.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mightcell.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 22:35 2023-02-23
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {
    /**
     * 用户注册数统计（当日）
     * @param day 当日
     * @return 当日的用户注册数
     */
    @Select("select count(1) from user where date(create_time) = #{day}")
    Integer selectRegisterNumByDay(String day);

    /**
     * 上传文件数统计（当日）
     * @param day 当日
     * @return 当日上传文件数
     */
    @Select("select count(1) from file where date(create_time) = #{day} and type = 'c'")
    Integer selectUploadFileNumByDay(String day);

    /**
     * 保护文件数统计（当日）
     * @param day 当日
     * @return 当日保护文件数
     */
    @Select("select count(1) from file where date(create_time) = #{day} and type in ('ELF 64-bit LSB executable', 'ASCII text')")
    Integer selectProtectFileNumByDay(String day);
}
