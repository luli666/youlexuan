package com.luli.code.service;

import com.luli.code.entity.PageResult;
import com.luli.code.pojo.Content;
import com.luli.code.pojo.ContentCategory;

import java.util.List;

public interface ContentCategoryService {
    List<ContentCategory> findAll();

    PageResult search(ContentCategory contentCategory, int page, int rows);

    void insert(ContentCategory contentCategory);

    void delete(Long[] ids);

    ContentCategory findOne(Long id);

    void update(ContentCategory contentCategory);
}
