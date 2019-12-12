package com.luli.code.controller;

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
    public Map<String,Object> getName(){
        Map<String,Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String logintime = sdf.format(date);
        map.put("username", username);
        map.put("logintime", logintime);
        return map;
    }
}
