package com.luli.code.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luli.code.mapper.ItemMapper;
import com.luli.code.pojo.Item;
import com.luli.code.pojo.ItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtils {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    /*
        导入商品数据
     */
    public void importItemData(){
        ItemExample example = new ItemExample();
        example.createCriteria().andStatusEqualTo("1");
        List<Item> items = itemMapper.selectByExample(example);

        for (Item item : items) {
            Map map = JSON.parseObject(item.getSpec(),Map.class); //将spec字段的json字符转换为map集合
            item.setSpecMap(map);                       //给注解字段赋值
        }
        solrTemplate.saveBeans(items);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtils solrUtils = context.getBean(SolrUtils.class);
        solrUtils.importItemData();
    }
}
