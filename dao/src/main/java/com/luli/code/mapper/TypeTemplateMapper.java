package com.luli.code.mapper;

import com.luli.code.pojo.TypeTemplate;
import com.luli.code.pojo.TypeTemplateExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface TypeTemplateMapper {
    int countByExample(TypeTemplateExample example);

    int deleteByExample(TypeTemplateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TypeTemplate record);

    int insertSelective(TypeTemplate record);

    List<TypeTemplate> selectByExample(TypeTemplateExample example);

    TypeTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TypeTemplate record, @Param("example") TypeTemplateExample example);

    int updateByExample(@Param("record") TypeTemplate record, @Param("example") TypeTemplateExample example);

    int updateByPrimaryKeySelective(TypeTemplate record);

    int updateByPrimaryKey(TypeTemplate record);

    List<Map<String, Object>> selectOptionList();
}