package com.luli.code.service;

import com.luli.code.entity.PageResult;
import com.luli.code.pojo.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    //查找所有
    List<Brand> list();
    //分页显示
    PageResult findPage(int pageNum, int pageSize);
    //新增品牌
    boolean insert(Brand brand);
    //按id查询
    Brand get(Long id);
    //修改
    boolean update(Brand brand);
    //模糊查询
    PageResult findPage(Brand brand,int pageNum, int pageSize);
    //删除
    void delete(Long[] ids);

    List<Map> selectOptionList();
}
