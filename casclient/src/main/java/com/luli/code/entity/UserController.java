package com.luli.code.entity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @RequestMapping("/findName")
    public void  findLoginUser(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
    }
}