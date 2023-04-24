package com.mightcell.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mightcell.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author MightCell
 * @Date 2023-03-29 21:58
 * @PackageName:com.mightcell.mapper
 * @ClassName: AdminMapper
 * @Description: AdminMapper
 * @Version 1.0
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
