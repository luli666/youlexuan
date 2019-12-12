package com.luli.sellgoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.luli.code.entity.PageResult;
import com.luli.code.mapper.GoodsMapper;
import com.luli.code.mapper.ItemMapper;
import com.luli.code.pojo.Goods;
import com.luli.code.pojo.GoodsExample;
import com.luli.code.pojo.Item;
import com.luli.code.pojo.ItemExample;
import com.luli.code.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public void updateStatus(Long[] ids, String status) {
        //需要修改的类
        Goods goods = new Goods();
        goods.setAuditStatus(status);

        List<Long> list = Arrays.asList(ids);
        GoodsExample example = new GoodsExample();
        example.createCriteria().andIdIn(list);
        goodsMapper.updateByExampleSelective(goods, example);
    }

    @Override
    public void delete(Long[] ids) {
        Goods goods = new Goods();
        goods.setIsDelete("1");

        List<Long> list = Arrays.asList(ids);
        GoodsExample example = new GoodsExample();
        example.createCriteria().andIdIn(list);
        goodsMapper.updateByExampleSelective(goods, example);
    }

    @Override
    public List<Item> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {

        //修改item表的状态值
        for (Long goodsId : goodsIds) {
            ItemExample example = new ItemExample();
            ItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            List<Item> items = itemMapper.selectByExample(example);
            for (Item item : items) {
                item.setStatus("1");
                itemMapper.updateByPrimaryKeySelective(item);
            }
        }

        List<Long> list = Arrays.asList(goodsIds);
        ItemExample example = new ItemExample();
        ItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(list);
        criteria.andStatusEqualTo(status);
        List<Item> list1 = itemMapper.selectByExample(example);

        return list1;
    }
}
