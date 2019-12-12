package com.luli.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name")
    public Map<String,Object> getUsername(){
        //获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //获取时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String logintime = sdf.format(date);

        HashMap<String, Object> map = new HashMap<>();
        map.put("username", name);
        map.put("logintime", logintime);
        return map;
    }
}
