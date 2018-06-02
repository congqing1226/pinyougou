package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author congzi
 * @Description: 用户登录接口
 * @create 2018-06-02
 * @Version 1.0
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name")
    public Map showName(){

        //得到登录人账号
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("登录用户名: "+name);
        Map map = new HashMap();
        map.put("loginName",name);

        return map;
    }

}
