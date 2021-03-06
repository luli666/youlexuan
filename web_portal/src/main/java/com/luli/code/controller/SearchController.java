package com.luli.code.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luli.code.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class SearchController {
    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/search")
    public Map<String,Object> searh(@RequestBody Map searchMap){
        return itemSearchService.search(searchMap);
    }
}
