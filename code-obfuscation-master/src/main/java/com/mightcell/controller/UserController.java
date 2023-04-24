package com.mightcell.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.github.pagehelper.PageInfo;
import com.mightcell.entity.User;
import com.mightcell.entity.request.LoginBo;
import com.mightcell.entity.request.PageBo;
import com.mightcell.entity.request.RegisterBo;
import com.mightcell.entity.request.UserVo;
import com.mightcell.entity.response.UserPageDto;
import com.mightcell.exception.CodeException;
import com.mightcell.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.mightcell.constant.UserConstant.IS_LOGOUT;

/**
 * @author: MightCell
 * @description: 用户相关操作控制层
 * @date: Created in 18:54 2023-02-22
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户登录
     *
     * @param loginBo 用户登录信息接收对象
     * @return 登录结果信息
     */
    @PostMapping("/login")
    public SaResult userLogin(@RequestBody LoginBo loginBo) {
        if (Objects.isNull(loginBo)) {
            log.info("LoginBo is null");
            throw new CodeException("用户登录信息接收对象为空");
        }
        User user = userService.userLogin(loginBo);
        if (!Objects.isNull(user)) {
            StpUtil.login(user.getId());
            StpUtil.getSession().set("user", user);
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return SaResult.ok("登录成功").setData(tokenInfo);
        }
        return SaResult.error("登录失败");
    }

    /**
     * 用户注册
     *
     * @param registerBo
     * @return 注册结果信息
     */
    @PostMapping("/register")
    public SaResult userRegister(@RequestBody RegisterBo registerBo) {
        int count = userService.userRegister(registerBo);
        if (count != 1) {
            return SaResult.error("注册失败");
        }
        return SaResult.ok("注册成功");
    }

    /**
     * 用户退出
     *
     * @return 退出结果信息
     */
    @GetMapping("/logout")
    public SaResult userLogout() {
        User user = (User) StpUtil.getSession().get("user");
        Integer id = user.getId();
        user.setIsLogin(IS_LOGOUT);
        userService.updateById(user);
        StpUtil.logout(user.getId());
        return SaResult.ok("退出成功");
    }

    @GetMapping("/status")
    public SaResult userLoginStatus() {
        return SaResult.ok("是否登录：" + StpUtil.isLogin());
    }

    @GetMapping("/query/{id}")
    public SaResult getUserInfoById(@PathVariable Long id) {
        User user = userService.getUserInfoById(id);
        return SaResult.ok("获取成功").setData(user.getUserVo());
    }

    /**
     * 获取当前登录的用户信息
     * @return 用户信息
     */
    @GetMapping("/query/current")
    public SaResult getCurrentLoginUser() {
        UserVo userVo = userService.getCurrentUserInfo();
        if (!Objects.isNull(userVo)) {
            return SaResult.ok("获取成功").setData(userVo);
        }
        return SaResult.ok("获取失败");
    }

    /**
     * 更新用户信息（用户id）
     * @param user 用户
     * @return 是否修改成功
     */
    @PutMapping("/update")
    public SaResult updateById(@RequestBody User user) {
        boolean result = userService.updateByUserId(user);
        if (result) {
            return SaResult.ok("修改成功");
        }
        return SaResult.error("修改失败");
    }


    @GetMapping("/page")
    public SaResult getPageVoInfo(PageBo pageBo){
        if (Objects.isNull(pageBo)){
            log.info("PageBo is null");
            throw new CodeException("分页参数接受对象为空");
        }
        PageInfo<UserPageDto> pageDtoPageInfo = userService.getUserDtoInfo(pageBo);
        if (!Objects.isNull(pageDtoPageInfo)){
            return SaResult.ok("获取成功").setData(pageDtoPageInfo);
        }
        return SaResult.error("获取失败");
    }
}
