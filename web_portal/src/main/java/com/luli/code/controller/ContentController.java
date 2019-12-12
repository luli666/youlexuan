package com.luli.code.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luli.code.pojo.Content;
import com.luli.code.service.ContentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;

    @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryId(long categoryId){
        return contentService.findByCategoryId(categoryId);
    }
}
