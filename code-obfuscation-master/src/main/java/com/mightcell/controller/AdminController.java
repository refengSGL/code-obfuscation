package com.mightcell.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mightcell.entity.Admin;
import com.mightcell.entity.User;
import com.mightcell.service.AdminService;
import com.mightcell.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;

/**
 * @Author MightCell
 * @Date 2023-03-29 21:56
 * @PackageName:com.mightcell.controller
 * @ClassName: AdminController
 * @Description: AdminController
 * @Version 1.0
 */
@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final AdminService adminService;

}
