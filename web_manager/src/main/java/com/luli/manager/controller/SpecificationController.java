package com.luli.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luli.code.entity.PageResult;
import com.luli.code.entity.Result;
import com.luli.code.entity.Spec;
import com.luli.code.pojo.Specification;
import com.luli.code.service.SpecificationService;
import org.apache.ibatis.io.ResolverUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody Specification specification,int page ,int rows){
        return specificationService.search(specification, page, rows);
    }

    @RequestMapping("/add")
    public Result search(@RequestBody Spec spec){
        try{
            specificationService.insert(spec);
            return new Result(true,"添加成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping("/findOne")
    public Spec findById(Long id){
        return specificationService.findById(id);
    }
    @RequestMapping("/update")
    public Result update(@RequestBody Spec spec){
        try{
            specificationService.update(spec);
            return new Result(true,"修改成功 ");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            specificationService.delete(ids);
            return new Result(true,"删除成功 ");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return specificationService.selectOptionList();
    }
}
