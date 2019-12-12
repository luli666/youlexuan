package com.luli.code.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luli.code.entity.Result;
import com.luli.code.pojo.User;
import com.luli.code.service.UserService;
import com.luli.code.utils.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/add")
    public Result register(@RequestBody User user,String smscode){        //注册
        Boolean info = userService.checkSmsCode(user.getPhone(), smscode);
        if(!info){
            return new Result(false,"验证码输入错误" );
        }
        try {
            userService.insert(user);
            return new Result(true, "创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "创建失败");
        }
    }
    /*
        发送验证码
     */
    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        //判断手机号格式
        if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
            return new Result(false,"手机号格式不正确" );
        }
        try{
            userService.createSmsCode(phone);       //生成验证码
            return new Result(true, "验证码发送成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "验证码发送失败");
        }
    }
}
