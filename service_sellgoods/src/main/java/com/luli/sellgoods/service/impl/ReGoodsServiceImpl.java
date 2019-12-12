package com.luli.sellgoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luli.code.entity.PageResult;
import com.luli.code.entity.ReGoods;
import com.luli.code.mapper.*;
import com.luli.code.pojo.*;
import com.luli.code.service.ReGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReGoodsServiceImpl implements ReGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Autowired
    private ItemMapper itemMapper;
    //运营商审核显示所有
    @Override
    public PageResult search(Goods goods, int page, int rows) {
        PageHelper.startPage(page, rows);
        GoodsExample example = new GoodsExample();
        GoodsExample.Criteria criteria = example.createCriteria();
        if(goods != null){
            if(goods.getSellerId() != null && goods.getSellerId().length()>0){
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if(goods.getAuditStatus() != null && goods.getAuditStatus().length()>0 ){
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if(goods.getGoodsName() != null && goods.getGoodsName().length()>0){
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
        }
        criteria.andIsDeleteNotEqualTo("1");
        List<Goods> goodsList = goodsMapper.selectByExample(example);
        PageInfo<Goods> pageInfo = new PageInfo<>(goodsList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /*商家审核显示所有*/
    @Override
    public PageResult searchGoods(Goods goods, int page, int rows) {
        PageHelper.startPage(page, rows);
        GoodsExample example = new GoodsExample();
        GoodsExample.Criteria criteria = example.createCriteria();
        if(goods != null){
            if(goods.getAuditStatus() != null && goods.getAuditStatus().length()>0 ){
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if(goods.getGoodsName() != null && goods.getGoodsName().length()>0){
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
        }
        //为1表示该记录已被删除
        criteria.andIsDeleteNotEqualTo("1");      //数据库数据有误，null值不能读取
        //criteria.andIsDeleteIsNull();

        List<Goods> goodsList = goodsMapper.selectByExample(example);
        PageInfo<Goods> pageInfo = new PageInfo<>(goodsList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    public void insert(ReGoods reGoods){
        reGoods.getGoods().setAuditStatus("0");//设置默认审核状态
        reGoods.getGoods().setIsDelete("0");        //设置默认删除状态，0表示未删除，1表示删除
        goodsMapper.insertSelective(reGoods.getGoods());//插入商品 表
        reGoods.getGoodsDesc().setGoodsId(reGoods.getGoods().getId());
        goodsDescMapper.insertSelective(reGoods.getGoodsDesc());//插入商品扩展数据
        saveItemList(reGoods);
    }

    @Override
    public ReGoods findOne(Long id) {
        //1.查询商品
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        //2.查询商品详情
        GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        //3.查询商品SKU商品列表
        ItemExample example = new ItemExample();
        example.createCriteria().andGoodsIdEqualTo(id);
        List<Item> items = itemMapper.selectByExample(example);

        return new ReGoods(goods,goodsDesc,items);
    }

    @Override
    public void update(ReGoods reGoods) {
        reGoods.getGoods().setAuditStatus("0");         //设置未申请的状态，经过修改的商品，需要重新设置状态
        //1.保存商品
        goodsMapper.updateByPrimaryKeySelective(reGoods.getGoods());
        //2.保存商品详情
        goodsDescMapper.updateByPrimaryKeySelective(reGoods.getGoodsDesc());
        //3.删除原有的sku列表数据
        ItemExample example = new ItemExample();
        example.createCriteria().andGoodsIdEqualTo(reGoods.getGoods().getId());
        itemMapper.deleteByExample(example);
        //添加新的sku列表数据
        saveItemList(reGoods);

    }

    private void saveItemList(ReGoods reGoods){
        //启用规格
        if("1".equals(reGoods.getGoods().getIsEnableSpec())){
            for (Item item : reGoods.getItemList()) {
                //遍历规格
                String title = reGoods.getGoods().getGoodsName();
                //拼接
                Map<String,Object> map = JSON.parseObject(item.getSpec());
                for (String key : map.keySet()) {
                    title = title + " " + map.get(key);
                }
                //标题
                item.setTitle(title);
                //设置规格方法
                setItemValues(reGoods, item);
                itemMapper.insertSelective(item);
            }
        }else{
            //不启用规格
            Item item = new Item();

            item.setTitle(reGoods.getGoods().getGoodsName());// 商品KPU+规格描述串作为SKU名称
            item.setPrice(reGoods.getGoods().getPrice());// 价格
            item.setStatus("1");// 状态
            item.setIsDefault("1");// 是否默认
            item.setNum(99999);// 库存数量
            item.setSpec("{}");
            setItemValues(reGoods, item);
            itemMapper.insert(item);
        }
    }

    private void setItemValues(ReGoods reGoods, Item item) {
        item.setGoodsId(reGoods.getGoods().getId());//商品SPU编号
        item.setSellerId(reGoods.getGoods().getSellerId());//商家编号
        item.setCategoryid(reGoods.getGoods().getCategory3Id());//商品分类编号（3级）
        item.setCreateTime(new Date());//创建日期
        item.setUpdateTime(new Date());//修改日期

        //品牌名称
        Brand brand = brandMapper.selectByPrimaryKey(reGoods.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //分类名称
        ItemCat itemCat = itemCatMapper.selectByPrimaryKey(reGoods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //商家名称
        Seller seller = sellerMapper.selectByPrimaryKey(reGoods.getGoods().getSellerId());
        item.setSeller(seller.getNickName());
        //图片地址
        List<Map> maps = JSON.parseArray(reGoods.getGoodsDesc().getItemImages(), Map.class);
        if(maps.size()>0){
            item.setImage((String) maps.get(0).get("url"));
        }
    }
    /*@Override
    public void insert(ReGoods reGoods) {
        reGoods.getGoods().setAuditStatus("0");//设置未申请状态
        goodsMapper.insertSelective(reGoods.getGoods());                    //插入商品表
        reGoods.getGoodsDesc().setGoodsId(reGoods.getGoods().getId());
        goodsDescMapper.insertSelective(reGoods.getGoodsDesc());            //插入商品扩展数据

        for (Item item : reGoods.getItems()) {
            //标题
            String title = reGoods.getGoods().getGoodsName();
            Map<String,Object> map = JSON.parseObject(item.getSpec());
            for (String key : map.keySet()) {
                title = " " + map.get(key);
            }
            item.setTitle(title);
            item.setGoodsId(reGoods.getGoods().getId());//商品SPU编号
            item.setSellerId(reGoods.getGoods().getSellerId());//商家编号
            item.setCategoryid(reGoods.getGoods().getCategory3Id());//商品分类编号（3级）
            item.setCreateTime(new Date());//创建日期
            item.setUpdateTime(new Date());//修改日期
            //品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(reGoods.getGoods().getBrandId());
            item.setBrand(brand.getName());
            //分类名称
            ItemCat itemCat = itemCatMapper.selectByPrimaryKey(reGoods.getGoods().getCategory3Id());
            item.setCategory(itemCat.getName());
            //商家名称
            Seller seller = sellerMapper.selectByPrimaryKey(reGoods.getGoods().getSellerId());
            item.setSeller(seller.getNickName());
            //图片地址
            List<Map> maps = JSON.parseArray(reGoods.getGoodsDesc().getItemImages(), Map.class);
            if(maps.size()>0){
                item.setImage((String) maps.get(0).get("url"));
            }
            itemMapper.insertSelective(item);
        }
    }*/
}
