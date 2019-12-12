package com.luli.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.luli.code.entity.PageResult;
import com.luli.code.entity.Result;
import com.luli.code.pojo.Goods;
import com.luli.code.pojo.Item;
import com.luli.code.service.GoodsService;
import com.luli.code.service.ItemPageService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private ReGoodsService reGoodsService;
    @Reference
    private GoodsService goodsService;
    //消息中间件
    @Autowired
    private Destination queueSolrDestination;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination topicPageDestination;

    //解除耦合##########################
    //@Reference
    //private ItemSearchService itemSearchService;
    //@Reference
    //private ItemPageService itemPageService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods, int page, int rows){
        //获取商家id
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(username);

        return reGoodsService.searchGoods(goods, page, rows);
    }
    /*审核商品的方法*/
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try{
            goodsService.updateStatus(ids,status);

            //按照SPU ID查询 SKU列表(状态为1)
            if("1".equals(status)){
                List<Item> list = goodsService.findItemListByGoodsIdandStatus(ids, status);

                //调用搜索接口，实现数据的批量导入
                if(list.size() > 0 ){
                    //解除耦合######################################
                    //itemSearchService.importList(list);         //更新索引库

                    //使用消息中间件处理
                    String jsonString = JSON.toJSONString(list);
                    //发送消息
                    jmsTemplate.send(queueSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(jsonString);
                        }
                    });

                }

                //使用消息中间件解除耦合###########################################
                //根据商品的id 获取商品的详情数据  并且根据详情数据和模板生成详情的页面
                /*for (Long id : ids) {
                    Map<String, Object> goodsData = itemPageService.findGoodsData(id);
                    itemPageService.createStaticPage(id, goodsData);            //使用freemarker创建页面
                }*/
                //消息中间件avtivemq
                //静态页面生成
                for (final Long id : ids) {
                    jmsTemplate.send(topicPageDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(id + "");
                        }
                    });
                }

            }

            if("1".equals(status)) {
                return new Result(true, "审核成功");
            }else {
                return new Result(false, "驳回");
            }
        }catch (Exception e){
            e.printStackTrace();
            if("1".equals(status)) {
                return new Result(true, "审核失败");
            }else {
                return new Result(false, "驳回失败");
            }
        }
    }
    @RequestMapping("/delete")
    public Result delete(final Long[] ids){
        try{
            goodsService.delete(ids);

            /*  //解除耦合##########################################
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));             //删除数据时，删除索引库中的数据
            */

            //消息中间件使用---应该写在商家后台中
            /*jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });*/

            return new Result(true, "删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true, "删除失败");
        }
    }
}
