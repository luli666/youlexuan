package com.luli.code.service;

import com.luli.code.entity.PageResult;
import com.luli.code.entity.Spec;
import com.luli.code.pojo.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    PageResult search(Specification specification,int pageNum,int pageSize);
    void insert(Spec spec);

    Spec findById(Long id);

    void update(Spec spec);

    void delete(Long[] ids);

    List<Map> selectOptionList();
}
