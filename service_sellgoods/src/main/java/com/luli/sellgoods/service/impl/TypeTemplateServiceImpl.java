package com.luli.sellgoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luli.code.entity.PageResult;
import com.luli.code.mapper.SpecificationOptionMapper;
import com.luli.code.mapper.TypeTemplateMapper;
import com.luli.code.pojo.SpecificationOption;
import com.luli.code.pojo.SpecificationOptionExample;
import com.luli.code.pojo.TypeTemplate;
import com.luli.code.pojo.TypeTemplateExample;
import com.luli.code.service.TypeTemplateService;
import com.luli.code.utils.RedisConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //将数据存储缓存
    private void saveToRedis(){
        //获取模板数据
        List<TypeTemplate> typeTemplates = findAll();
        //循环模板
        for (TypeTemplate typeTemplate : typeTemplates) {
            //存储品牌列表
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps(RedisConst.BRAND_LIST).put(typeTemplate.getId(), brandList);
            //存储规格列表
            List<Map> specList = findSpecList(typeTemplate.getId());
            redisTemplate.boundHashOps(RedisConst.SPEC_LIST).put(typeTemplate.getId(), specList);
        }
    }
    private List<TypeTemplate> findAll(){
        return typeTemplateMapper.selectByExample(null);
    }

    @Override
    public PageResult search(TypeTemplate typeTemplate, int page, int rows) {
        PageHelper.startPage(page, rows);
        TypeTemplateExample example = new TypeTemplateExample();
        if(typeTemplate != null && typeTemplate.getName() != null && typeTemplate.getName().length() > 0){
            example.createCriteria().andNameLike("%" + typeTemplate.getName() + "%");
        }
        List<TypeTemplate> typeTemplates = typeTemplateMapper.selectByExample(example);
        PageInfo<TypeTemplate> pageInfo = new PageInfo<>(typeTemplates);

        saveToRedis();          //存入数据到redis中
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void insert(TypeTemplate typeTemplate) {
        typeTemplateMapper.insertSelective(typeTemplate);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
    }

    @Override
    public List<Map<String, Object>> selectOptionList() {
        return typeTemplateMapper.selectOptionList();
    }

    @Override
    public List<Map> findSpecList(Long id) {
        //查询模板
        TypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        List<Map> maps = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);

        for (Map map : maps) {
            //查询规格选项列表
            SpecificationOptionExample example = new SpecificationOptionExample();
            example.createCriteria().andSpecIdEqualTo(new Long((Integer)map.get("id")));

            List<SpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(example);
            map.put("options", specificationOptions);
        }
        return maps;
    }
}
