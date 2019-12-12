package com.luli.code.service.impl;

import com.luli.code.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class PageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try{
            String text = textMessage.getText();
            itemPageService.createStaticPage(Long.parseLong(text),itemPageService.findGoodsData(Long.parseLong(text)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
