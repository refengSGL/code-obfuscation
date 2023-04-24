package com.mightcell.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mightcell.entity.Daily;

/**
 * @Author MightCell
 * @Date 2023-03-30 14:35
 * @PackageName:com.mightcell.service
 * @ClassName: DailyService
 * @Description: DailyService
 * @Version 1.0
 */
public interface DailyService extends IService<Daily> {

    /**
     * 统计当日记录数据
     * @param day 日期
     */
    void createStatisticsByDay(String day);
}
