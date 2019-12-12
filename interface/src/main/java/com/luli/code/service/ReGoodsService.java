package com.luli.code.service;

import com.luli.code.entity.PageResult;
import com.luli.code.entity.ReGoods;
import com.luli.code.pojo.Goods;

public interface ReGoodsService {

    void insert(ReGoods reGoods);

    PageResult search(Goods reGoods, int page, int rows);

    ReGoods findOne(Long id);

    void update(ReGoods reGoods);

    PageResult searchGoods(Goods goods, int page, int rows);

}
