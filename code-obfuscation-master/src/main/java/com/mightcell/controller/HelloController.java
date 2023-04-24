package com.mightcell.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: MightCell
 * @description: 测试控制器
 * @date: Created in 18:28 2023-02-22
 */
@CrossOrigin
@RequestMapping("/test")
@RestController
public class HelloController {
    /**
     * 测试服务器是否正常启动
     * @return Hello World
     */
    @GetMapping("/helloWorld")
    public Object sayHelloWorld() {
        return "Hello World";
    }
}
