package com.luli.code.service.impl;

import com.luli.code.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class PageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try{
            Long[] ids = (Long[]) objectMessage.getObject();
            itemPageService.deleteItemHtml(ids);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
