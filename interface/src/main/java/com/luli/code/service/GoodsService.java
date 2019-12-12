package com.luli.code.service;

import com.luli.code.entity.PageResult;
import com.luli.code.pojo.Goods;
import com.luli.code.pojo.Item;

import java.util.List;

public interface GoodsService {

    void updateStatus(Long[] ids, String status);

    void delete(Long[] ids);

    List<Item> findItemListByGoodsIdandStatus(Long[] goodsIds, String status);
}
