package com.luli.code.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luli.code.entity.PageResult;
import com.luli.code.entity.Result;
import com.luli.code.pojo.TypeTemplate;
import com.luli.code.service.TypeTemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody TypeTemplate typeTemplate,int page,int rows){
        return typeTemplateService.search(typeTemplate,page,rows);
    }
    @RequestMapping("/add")
    public Result insert(@RequestBody TypeTemplate typeTemplate){
        String str = typeTemplate.getCustomAttributeItems();
        if(str.equals("[]")){
            typeTemplate.setCustomAttributeItems("");
        }
        try{
            typeTemplateService.insert(typeTemplate);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true,"添加失败");
        }
    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            typeTemplateService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true,"删除失败");
        }
    }
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate typeTemplate){
        try{
            typeTemplateService.update(typeTemplate);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    @RequestMapping("/selectOptionList")
    public List<Map<String,Object>> selectOptionList(){
        return typeTemplateService.selectOptionList();
    }

    @RequestMapping("/findBySpecList")
    public List<Map> findSpecList(Long id){
        if(id != null) {
            return typeTemplateService.findSpecList(id);
        }
        return null;
    }
}
