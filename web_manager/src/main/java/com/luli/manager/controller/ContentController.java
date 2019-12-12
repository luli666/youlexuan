package com.luli.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.Page;
import com.luli.code.entity.PageResult;
import com.luli.code.entity.Result;
import com.luli.code.pojo.Content;
import com.luli.code.pojo.ContentCategory;
import com.luli.code.pojo.Seller;
import com.luli.code.service.ContentService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;

    @RequestMapping("/add")
    public Result insert(@RequestBody Content content){
        /*if("true".equals(content.getStatus())){
            content.setStatus("1");
        }else{
            content.setStatus("0");
        }*/
        try {
            contentService.insert(content);
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
    @RequestMapping("/search")
    public PageResult search( int page, int rows){
        return contentService.search(page,rows);
    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            contentService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
    @RequestMapping("/findOne")
    public Content findOne(Long id){
        return contentService.findOne(id);
    }
    @RequestMapping("/update")
    public Result updateStatus(@RequestBody Content content){
        try {
            contentService.update(content);
            return new Result(true, "修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true, "修改失败");
        }
    }
}
