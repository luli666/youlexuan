package com.luli.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.luli.code.pojo.Item;
import com.luli.code.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        //监听消息。。。
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            List<Item> items = JSON.parseArray(text, Item.class);
            for (Item item : items) {
                //将spec字段中的json字符串转换为map
                Map specMap = JSON.parseObject(item.getSpec());
                item.setSpecMap(specMap);//给带注解的字段赋值
            }
            itemSearchService.importList(items);            //更新索引库
            //导入结束
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
