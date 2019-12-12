package com.luli.code.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.luli.code.mapper.UserMapper;
import com.luli.code.pojo.User;
import com.luli.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import javax.jms.*;
import java.sql.SQLOutput;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination smsDestination;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    private final String TEMPLATE_CODE = "SMS_178980158";
    private final String SIGN_NAME = "品优购";

    @Override
    public void insert(User user) {
        user.setCreated(new Date());        //创建日期
        user.setUpdated(new Date());        //修改日期

        //String password = DigestUtils.md5Hex(user.getPassword());//对密码加密
        //user.setPassword(password);
        userMapper.insertSelective(user);
    }

    @Override
    public void createSmsCode(String phone) {
        //1.生成随机数
        final String SMSCODE = (long)(Math.random()*1000000) + "";

        //2.将验证码放入redis中
        redisTemplate.boundHashOps("smscode").put(phone, SMSCODE);

        //发送ActiveMQ
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile", phone);                      //手机号
                mapMessage.setString("template_code", TEMPLATE_CODE);     //模板编号
                mapMessage.setString("sign_name", SIGN_NAME);              //签名
                Map map = new HashMap();
                map.put("code", SMSCODE);
                mapMessage.setString("param", JSON.toJSONString(map));
                return mapMessage;
            }
        });
    }

    //匹配验证码
    @Override
    public Boolean checkSmsCode(String phone, String code) {
        //获取缓存中的验证码
        String smscode = (String) redisTemplate.boundHashOps("smscode").get(phone);
        if(smscode != null){
            if(smscode.equals(code)){
                return true;
            }
            return false;
        }
        return false;
    }
}
