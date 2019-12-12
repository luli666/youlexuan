package com.luli.code.service;

import com.luli.code.pojo.User;

public interface UserService {
    void insert(User user);
    void createSmsCode(String phone);
    Boolean checkSmsCode(String phone,String code);
}
