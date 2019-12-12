package com.luli.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luli.code.entity.PageResult;
import com.luli.code.entity.Result;
import com.luli.code.pojo.Brand;
import com.luli.code.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<Brand> list(){
        return brandService.list();
    }
    @RequestMapping("/findPage")
    public PageResult findPage(int page,int rows ){
        return brandService.findPage(page,rows);
    }
    @RequestMapping("/add")
    public Result insert(@RequestBody Brand brand){
        try {
            brandService.insert(brand);
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
    @RequestMapping("/get")
    public Brand get(Long id){
        Brand brand = brandService.get(id);
        return brand;
    }
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        boolean info = brandService.update(brand);
        if(info){
            return new Result(true, "修改成功");
        }else{
            return new Result(false, "修改失败");
        }
    }
    @RequestMapping("/search")
    public PageResult search(@RequestBody Brand brand,int page,int rows){
        PageResult page1 = brandService.findPage(brand, page, rows);
        return page1;
    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
