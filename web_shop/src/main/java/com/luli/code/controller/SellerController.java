package com.luli.code.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luli.code.entity.Result;
import com.luli.code.pojo.Seller;
import com.luli.code.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference
    private SellerService sellerService;

    @RequestMapping("/add")
    public Result insert(@RequestBody Seller seller){
        seller.setStatus("0");
        seller.setCreateTime(new Date());
        try{
            sellerService.insert(seller);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
}
