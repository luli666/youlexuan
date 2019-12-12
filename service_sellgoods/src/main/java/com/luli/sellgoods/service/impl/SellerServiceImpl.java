package com.luli.sellgoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luli.code.entity.PageResult;
import com.luli.code.mapper.SellerMapper;
import com.luli.code.pojo.Seller;
import com.luli.code.pojo.SellerExample;
import com.luli.code.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerMapper sellerMapper;
    @Override
    public void insert(Seller seller) {
        sellerMapper.insertSelective(seller);
    }

    @Override
    public PageResult search(Seller seller, int page, int rows) {
        PageHelper.startPage(page, rows);
        SellerExample example = new SellerExample();
        SellerExample.Criteria criteria = example.createCriteria();
        if(seller != null){
            if(seller.getName()!= null && seller.getName().length()>0){
                criteria.andNameLike("%"+seller.getName()+"%");
            }
            if(seller.getNickName()!=null && seller.getNickName().length()>0){
                criteria.andNickNameLike("%"+seller.getNickName()+"%");
            }
        }
        criteria.andStatusEqualTo("0");
        List<Seller> list = sellerMapper.selectByExample(example);
        PageInfo<Seller> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public Seller findOne(String id) {
        return sellerMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateStatus(Seller seller) {
        sellerMapper.updateByPrimaryKeySelective(seller);
    }
}
