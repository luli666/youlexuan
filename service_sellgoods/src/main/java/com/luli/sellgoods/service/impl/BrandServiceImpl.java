package com.luli.sellgoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luli.code.entity.PageResult;
import com.luli.code.mapper.BrandMapper;
import com.luli.code.pojo.Brand;
import com.luli.code.pojo.BrandExample;
import com.luli.code.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> list() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Brand> brands = brandMapper.selectByExample(null);
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public boolean insert(Brand brand) {
        int row = brandMapper.insertSelective(brand);
        if( row>0 ){
            return true;
        }
        return false;
    }

    @Override
    public Brand get(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean update(Brand brand) {
        int row = brandMapper.updateByPrimaryKeySelective(brand);
        if(row > 0){
            return true;
        }
        return false;
    }

    @Override
    public PageResult findPage(Brand brand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        BrandExample example = new BrandExample();
        BrandExample.Criteria criteria = example.createCriteria();

        if(brand != null){
            if(brand.getName() != null && brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if(brand.getFirstChar() != null && brand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }
        Page<Brand> pages = (Page<Brand>) brandMapper.selectByExample(example);

        return new PageResult(pages.getTotal(),pages.getResult());
    }

    @Override
    public void delete(Long[] ids) {
        /*List<Long> list = Arrays.asList(ids);
        BrandExample example = new BrandExample();
        BrandExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(list);
        brandMapper.deleteByExample(example);*/
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }

}