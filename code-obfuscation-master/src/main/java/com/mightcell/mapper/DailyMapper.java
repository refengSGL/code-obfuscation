package com.mightcell.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mightcell.entity.Daily;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author MightCell
 * @Date 2023-03-30 14:34
 * @PackageName:com.mightcell.mapper
 * @ClassName: DailyMapper
 * @Description: DailyMapper
 * @Version 1.0
 */
@Mapper
public interface DailyMapper extends BaseMapper<Daily> {

    @Select("select count(1)")
    Integer selectFileUploadNumByDay(String day);
}
