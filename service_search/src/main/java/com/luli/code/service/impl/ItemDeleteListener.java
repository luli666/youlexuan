package com.luli.code.service.impl;

import com.luli.code.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.sound.midi.Soundbank;
import java.util.Arrays;

@Component
public class ItemDeleteListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try{
            ObjectMessage objectMessage = (ObjectMessage) message;
            Long[] goodsIds = (Long[]) objectMessage.getObject();

            itemSearchService.deleteByGoodsIds(Arrays.asList(goodsIds));            //更新索引库

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
