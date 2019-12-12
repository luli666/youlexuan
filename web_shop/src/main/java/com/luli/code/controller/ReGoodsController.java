package com.luli.code.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luli.code.entity.PageResult;
import com.luli.code.entity.ReGoods;
import com.luli.code.entity.Result;
import com.luli.code.pojo.Goods;
import com.luli.code.service.GoodsService;
import com.luli.code.service.ReGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@RestController
@RequestMapping("/goods")
public class ReGoodsController {
    @Reference
    private ReGoodsService reGoodsService;
    @Reference
    private GoodsService goodsService;
    /*消息中间件*/
    @Autowired
    private Destination queueSolrDeleteDestination;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination topicPageDeleteDestination;//删除静态网页信息

    @RequestMapping("/add")
    public Result insert(@RequestBody ReGoods reGoods){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        reGoods.getGoods().setSellerId(username);       //设置商家id
        try{
            reGoodsService.insert(reGoods);
            return new Result(true, "添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods, int page, int rows){
        //获取商家id
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(username);
        return reGoodsService.search(goods,page,rows);
    }
    @RequestMapping("/findOne")
    public ReGoods findOne(Long id){
        return reGoodsService.findOne(id);
    }
    @RequestMapping("/update")
    public Result update(@RequestBody ReGoods reGoods){

        try{
            reGoodsService.update(reGoods);
            return new Result(true,"修改成功" );
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            goodsService.delete(ids);

            /*消息中间件--删除solr索引库中的数据*/
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });
            /*删除页面*/
            jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });

            return new Result(true, "删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
}
