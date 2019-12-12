package com.luli.sellgoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luli.code.entity.PageResult;
import com.luli.code.mapper.ContentCategoryMapper;
import com.luli.code.pojo.Content;
import com.luli.code.pojo.ContentCategory;
import com.luli.code.pojo.ContentCategoryExample;
import com.luli.code.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private ContentCategoryMapper contentCategoryMapper;
    @Override
    public List<ContentCategory> findAll() {
        ContentCategoryExample example = new ContentCategoryExample();
        return contentCategoryMapper.selectByExample(example);
    }

    @Override
    public PageResult search(ContentCategory contentCategory, int page, int rows) {
        PageHelper.startPage(page, rows);
        ContentCategoryExample example = new ContentCategoryExample();
        ContentCategoryExample.Criteria criteria = example.createCriteria();
        if(contentCategory != null && contentCategory.getName()!=null && contentCategory.getName().length()>0){
            criteria.andNameLike("%"+contentCategory.getName()+"%");
        }
        List<ContentCategory> contentCategories = contentCategoryMapper.selectByExample(example);
        PageInfo<ContentCategory> pageInfo = new PageInfo<>(contentCategories);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void insert(ContentCategory contentCategory) {
        contentCategoryMapper.insertSelective(contentCategory);
    }

    @Override
    public void delete(Long[] ids) {
        List<Long> list = Arrays.asList(ids);
        ContentCategoryExample example = new ContentCategoryExample();
        example.createCriteria().andIdIn(list);
        contentCategoryMapper.deleteByExample(example);
    }

    @Override
    public ContentCategory findOne(Long id) {
        return contentCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(ContentCategory contentCategory) {
        contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
    }
}
