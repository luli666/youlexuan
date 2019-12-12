package com.luli.code.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luli.code.entity.ReItemCat;
import com.luli.code.entity.Result;
import com.luli.code.pojo.ItemCat;
import com.luli.code.service.ItemCatService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("/findAll")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }
    @RequestMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId){
        return itemCatService.findByParentId(parentId);
    }
    @RequestMapping("/add")
    public Result insert(@RequestBody ItemCat itemCat){
        try{
            itemCatService.insert(itemCat);
            return new Result(true, "添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
    @RequestMapping("/findOne")
    public ReItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }
    @RequestMapping("/update")
    public Result update(@RequestBody ItemCat itemCat){
        try{
            itemCatService.update(itemCat);
            return new Result(true, "修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            itemCatService.delete(ids);
            return new Result(true, "删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
}
