package com.mightcell.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mightcell.entity.Daily;
import com.mightcell.mapper.DailyMapper;
import com.mightcell.service.DailyService;
import org.springframework.stereotype.Service;

/**
 * @Author MightCell
 * @Date 2023-03-30 14:35
 * @PackageName:com.mightcell.service.impl
 * @ClassName: DailyServiceImpl
 * @Description: DailyServiceImpl
 * @Version 1.0
 */
@Service
public class DailyServiceImpl extends ServiceImpl<DailyMapper, Daily> implements DailyService{
    /**
     * 统计当日记录数据
     *
     * @param day 日期
     */
    @Override
    public void createStatisticsByDay(String day) {
        // 如果当日统计记录已存在，则删除重新统计
        LambdaQueryWrapper<Daily> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Daily::getDateCalculated, day);
        baseMapper.delete(queryWrapper);

        // 生成统计记录

    }
}
