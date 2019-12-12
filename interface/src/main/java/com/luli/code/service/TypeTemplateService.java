package com.luli.code.service;

import com.luli.code.entity.PageResult;
import com.luli.code.pojo.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    PageResult search(TypeTemplate typeTemplate, int page, int rows);

    void insert(TypeTemplate typeTemplate);

    void delete(Long[] ids);

    TypeTemplate findOne(Long id);

    void update(TypeTemplate typeTemplate);

    List<Map<String, Object>> selectOptionList();

    List<Map> findSpecList(Long id);
}
