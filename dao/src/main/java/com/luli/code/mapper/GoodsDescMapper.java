package com.luli.code.mapper;

import com.luli.code.pojo.GoodsDesc;
import com.luli.code.pojo.GoodsDescExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GoodsDescMapper {
    int countByExample(GoodsDescExample example);

    int deleteByExample(GoodsDescExample example);

    int deleteByPrimaryKey(Long goodsId);

    int insert(GoodsDesc record);

    int insertSelective(GoodsDesc record);

    List<GoodsDesc> selectByExample(GoodsDescExample example);

    GoodsDesc selectByPrimaryKey(Long goodsId);

    int updateByExampleSelective(@Param("record") GoodsDesc record, @Param("example") GoodsDescExample example);

    int updateByExample(@Param("record") GoodsDesc record, @Param("example") GoodsDescExample example);

    int updateByPrimaryKeySelective(GoodsDesc record);

    int updateByPrimaryKey(GoodsDesc record);
}