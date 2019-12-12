package com.luli.code.service;

import com.luli.code.entity.PageResult;
import com.luli.code.pojo.Seller;

public interface SellerService {
    void insert(Seller seller);

    PageResult search(Seller seller, int page, int rows);

    Seller findOne(String id);

    void updateStatus(Seller seller);
}
