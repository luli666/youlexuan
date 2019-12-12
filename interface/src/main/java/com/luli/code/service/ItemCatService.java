package com.luli.code.service;

import com.luli.code.entity.ReItemCat;
import com.luli.code.pojo.ItemCat;

import java.util.List;

public interface ItemCatService {
    List<ItemCat> findByParentId(Long parentId);

    void insert(ItemCat itemCat);

    ReItemCat findOne(Long id);

    void update(ItemCat itemCat);

    void delete(Long[] ids);

    List<ItemCat> findAll();
}
