package com.mightcell.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mightcell.entity.Admin;
import com.mightcell.mapper.AdminMapper;
import com.mightcell.service.AdminService;
import org.springframework.stereotype.Service;

/**
 * @Author MightCell
 * @Date 2023-03-29 21:59
 * @PackageName:com.mightcell.service.impl
 * @ClassName: AdminServiceImpl
 * @Description: AdminServiceImpl
 * @Version 1.0
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
}
