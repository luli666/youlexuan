package com.luli.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luli.code.entity.PageResult;
import com.luli.code.entity.Result;
import com.luli.code.pojo.Seller;
import com.luli.code.service.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller1")
public class SellerController {
    @Reference
    private SellerService sellerService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller,int page, int rows){
        return sellerService.search(seller,page,rows);
    }
    @RequestMapping("/findOne")
    public Seller findOne(String id){
        return sellerService.findOne(id);
    }
    @RequestMapping("/updateStatus")
    public Result updateStatus(Seller seller){
        try {
            sellerService.updateStatus(seller);
            return new Result(true, "修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true, "修改失败");
        }
    }
}
