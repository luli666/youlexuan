package com.luli.code.entity;

import com.luli.code.pojo.ItemCat;

import java.io.Serializable;
import java.util.Map;

public class ReItemCat implements Serializable {
    private ItemCat itemCat;
    private Map map;

    public ItemCat getItemCat() {
        return itemCat;
    }

    public void setItemCat(ItemCat itemCat) {
        this.itemCat = itemCat;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public ReItemCat(ItemCat itemCat, Map map) {
        this.itemCat = itemCat;
        this.map = map;
    }

    public ReItemCat() {
    }
}
