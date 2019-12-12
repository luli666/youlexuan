package com.luli.sellgoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luli.code.entity.PageResult;
import com.luli.code.mapper.ContentMapper;
import com.luli.code.pojo.Content;
import com.luli.code.pojo.ContentExample;
import com.luli.code.service.ContentService;
import com.luli.code.utils.RedisConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void insert(Content content) {       //redis缓存
        contentMapper.insertSelective(content);
        //清楚缓存
        redisTemplate.boundHashOps(RedisConst.CONTENT).delete(content.getCategoryId());
    }

    @Override
    public PageResult search(int page, int rows) {
        PageHelper.startPage(page, rows);
        ContentExample example = new ContentExample();
        List<Content> contents = contentMapper.selectByExample(example);
        PageInfo<Content> pageInfo = new PageInfo<>(contents);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void delete(Long[] ids) {
        //清空缓存
        for (Long id : ids) {
            Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
            redisTemplate.boundHashOps(RedisConst.CONTENT).delete(categoryId);
        }

        //从数据库删除记录
        List<Long> list = Arrays.asList(ids);
        ContentExample example = new ContentExample();
        example.createCriteria().andIdIn(list);
        contentMapper.deleteByExample(example);
    }

    @Override
    public Content findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(Content content) {
        //查询修改前的分类id
        Long oldCategoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
        redisTemplate.boundHashOps(RedisConst.CONTENT).delete(oldCategoryId);       //清楚缓存
        contentMapper.updateByPrimaryKeySelective(content);

        //如果修改后的分类id发生修改，清楚修改后的分类id的缓存
        if(oldCategoryId.longValue() != content.getCategoryId().longValue()){
            redisTemplate.boundHashOps(RedisConst.CONTENT).delete(content.getCategoryId());
        }
    }
    //根据广告分类id查询广告列表
    @Override
    public List<Content> findByCategoryId(long categoryId) {        //redis缓存机制
        //1.从redis中取数据
        List<Content> contents = (List<Content>) redisTemplate.boundHashOps(RedisConst.CONTENT).get(categoryId);
        //2.判断redis中是否有数据
        if(contents == null){
            //3.从mysql数据库中查找数据
            ContentExample example = new ContentExample();
            ContentExample.Criteria criteria = example.createCriteria();
            criteria.andCategoryIdEqualTo(categoryId);
            criteria.andStatusEqualTo("1");//开启状态
            example.setOrderByClause("sort_order");
            contents = contentMapper.selectByExample(example);
            //4.将查询到的数据库保存到Redis数据库中
            redisTemplate.boundHashOps(RedisConst.CONTENT).put(categoryId, contents);
        }
        return contents;
    }
    /*@Override
    public List<Content> findByCategoryId(long categoryId) {
        //根据广告分类id查询广告列表
        ContentExample example = new ContentExample();
        ContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        criteria.andStatusEqualTo("1");//开启状态
        example.setOrderByClause("sort_order");
        return contentMapper.selectByExample(example);
    }*/
}
