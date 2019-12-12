package com.luli.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luli.code.entity.PageResult;
import com.luli.code.entity.Result;
import com.luli.code.pojo.Content;
import com.luli.code.pojo.ContentCategory;
import com.luli.code.service.ContentCategoryService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {
    @Reference
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/findAll")
    public List<ContentCategory> findAll(){
        return contentCategoryService.findAll();
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody ContentCategory contentCategory, int page, int rows){
        return contentCategoryService.search(contentCategory,page,rows);
    }
    @RequestMapping("/add")
    public Result insert(@RequestBody ContentCategory contentCategory){
        try {
            contentCategoryService.insert(contentCategory);
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            contentCategoryService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
    @RequestMapping("/findOne")
    public ContentCategory findOne(Long id){
        return contentCategoryService.findOne(id);
    }
    @RequestMapping("/update")
    public Result updateStatus(@RequestBody ContentCategory contentCategory){
        try {
            contentCategoryService.update(contentCategory);
            return new Result(true, "修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true, "修改失败");
        }
    }
}
