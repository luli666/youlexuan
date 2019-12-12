package com.luli.sellgoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.luli.code.entity.ReItemCat;
import com.luli.code.mapper.ItemCatMapper;
import com.luli.code.mapper.TypeTemplateMapper;
import com.luli.code.pojo.ItemCat;
import com.luli.code.pojo.ItemCatExample;
import com.luli.code.pojo.TypeTemplate;
import com.luli.code.service.ItemCatService;
import com.luli.code.utils.RedisConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private RedisTemplate redisTemplate;



    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        //每次执行查询的时候，一次性读取缓存进行存储 (因为每次增删改都要执行此方法)
        //获取所有分类的数据
        List<ItemCat> itemCats = findAll();
        for (ItemCat itemCat : itemCats) {      //更新商品分类缓存
            redisTemplate.boundHashOps(RedisConst.ITEMCAT).put(itemCat.getName(),itemCat.getTypeId());
        }

        ItemCatExample example = new ItemCatExample();
        example.createCriteria().andParentIdEqualTo(parentId);
        return itemCatMapper.selectByExample(example);
    }


    @Override
    public void insert(ItemCat itemCat) {
        itemCatMapper.insertSelective(itemCat);
    }

    @Override
    public ReItemCat findOne(Long id) {
        ItemCat itemCat = itemCatMapper.selectByPrimaryKey(id);
        TypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(itemCat.getTypeId());
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", typeTemplate.getId());
        map.put("text", typeTemplate.getName());
        return new ReItemCat(itemCat, map);
    }

    @Override
    public void update(ItemCat itemCat) {
        itemCatMapper.updateByPrimaryKeySelective(itemCat);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            del(id);
        }
    }

    @Override
    public List<ItemCat> findAll() {
        return itemCatMapper.selectByExample(null);
    }

    public void del(Long id) {
        //删除分类
        itemCatMapper.deleteByPrimaryKey(id);
        //删除子分类
        ItemCatExample example = new ItemCatExample();
        example.createCriteria().andParentIdEqualTo(id);
        List<ItemCat> itemCats = itemCatMapper.selectByExample(example);
        if ( itemCats != null && itemCats.size() > 0){       //递归
            for (ItemCat itemCat : itemCats) {
                del(itemCat.getId());
            }
        }
        itemCatMapper.deleteByExample(example);
    }

}
