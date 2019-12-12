package com.luli.code.service;

import com.luli.code.entity.PageResult;
import com.luli.code.pojo.Content;

import java.util.List;

public interface ContentService {
    void insert(Content content);

    PageResult search(int page, int rows);

    void delete(Long[] ids);

    Content findOne(Long id);

    void update(Content content);

    List<Content> findByCategoryId(long categoryId);
}
