package com.luli.code.entity;

import com.luli.code.pojo.Goods;
import com.luli.code.pojo.GoodsDesc;
import com.luli.code.pojo.Item;

import java.io.Serializable;
import java.util.List;

public class ReGoods implements Serializable {
    private Goods goods;
    private GoodsDesc goodsDesc;
    private List<Item> itemList;

    public ReGoods(Goods goods, GoodsDesc goodsDesc, List<Item> itemList) {
        this.goods = goods;
        this.goodsDesc = goodsDesc;
        this.itemList = itemList;
    }

    public ReGoods() {
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(GoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}