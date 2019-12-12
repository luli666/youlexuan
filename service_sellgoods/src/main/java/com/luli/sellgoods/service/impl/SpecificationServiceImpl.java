package com.luli.sellgoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luli.code.entity.PageResult;
import com.luli.code.entity.Spec;
import com.luli.code.mapper.SpecificationMapper;
import com.luli.code.mapper.SpecificationOptionMapper;
import com.luli.code.pojo.Specification;
import com.luli.code.pojo.SpecificationExample;
import com.luli.code.pojo.SpecificationOption;
import com.luli.code.pojo.SpecificationOptionExample;
import com.luli.code.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult search(Specification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        SpecificationExample example = new SpecificationExample();
        SpecificationExample.Criteria criteria = example.createCriteria();
        if( specification != null && specification.getSpecName() != null && specification.getSpecName().length() > 0 ){
            criteria.andSpecNameLike("%" + specification.getSpecName() + "%" );
        }
        List<Specification> specifications = specificationMapper.selectByExample(example);
        PageInfo<Specification> pageInfo = new PageInfo<>(specifications);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void insert(Spec spec) {
        specificationMapper.insert(spec.getSpecification());
        List<SpecificationOption> specificationOptionLists = spec.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionLists) {
            specificationOption.setSpecId(spec.getSpecification().getId());
            specificationOptionMapper.insertSelective(specificationOption);
        }
    }

    @Override
    public Spec findById(Long id) {
        Spec spec = new Spec();
        Specification specification = specificationMapper.selectByPrimaryKey(id);
        SpecificationOptionExample example = new SpecificationOptionExample();
        example.createCriteria().andSpecIdEqualTo(id);
        List<SpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(example);

        spec.setSpecification(specification);
        spec.setSpecificationOptionList(specificationOptions);

        return spec;
    }

    @Override
    public void update(Spec spec) {
        /*先清怪SpecificationOption表数据*/
        SpecificationOptionExample example = new SpecificationOptionExample();
        example.createCriteria().andSpecIdEqualTo(spec.getSpecification().getId());
        specificationOptionMapper.deleteByExample(example);

        /*修改数据*/
        specificationMapper.updateByPrimaryKeySelective(spec.getSpecification());
        /*向SpectificationOption添加数据*/
        List<SpecificationOption> specificationOptionList = spec.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            specificationOption.setSpecId(spec.getSpecification().getId());
            specificationOptionMapper.insertSelective(specificationOption);
        }
    }

    @Override
    public void delete(Long[] ids) {
        List<Long> list = Arrays.asList(ids);
        SpecificationExample example = new SpecificationExample();
        example.createCriteria().andIdIn(list);
        specificationMapper.deleteByExample(example);

        SpecificationOptionExample specificationOptionExample = new SpecificationOptionExample();
        specificationOptionExample.createCriteria().andSpecIdIn(list);
        specificationOptionMapper.deleteByExample(specificationOptionExample);

    }

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }
}
