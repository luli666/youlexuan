package com.luli.code.service.impl;

import com.luli.code.mapper.GoodsDescMapper;
import com.luli.code.mapper.GoodsMapper;
import com.luli.code.mapper.ItemCatMapper;
import com.luli.code.mapper.ItemMapper;
import com.luli.code.pojo.*;
import com.luli.code.service.ItemPageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService{
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ServletContext servletContext;

    @Override
    public Map<String, Object> findGoodsData(Long goodsId) {
        Map<String, Object> map = new HashMap<>();
        //1.获取商品的数据
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        //2.获取商品的详情数据
        GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        //3.获取库存集合的数据
        ItemExample example = new ItemExample();
        example.createCriteria().andGoodsIdEqualTo(goodsId);
        List<Item> items = itemMapper.selectByExample(example);
        //4.获取商品对应的分类数据
        if(goods != null){
            ItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
            //封装数据
            map.put("itemCat1", itemCat1);
            map.put("itemCat2", itemCat2);
            map.put("itemCat3", itemCat3);
        }
        //5.将商品的所有数据封装进map
        map.put("goods", goods);
        map.put("goodsDesc", goodsDesc);
        map.put("itemList", items);
        return map;
    }

    @Override
    public void createStaticPage(Long goodsId, Map<String, Object> map) throws IOException, TemplateException {
        //1.获取模板的初始化对象
        Configuration configuration = freeMarkerConfig.getConfiguration();
        //2.获取模板对象
        Template template = configuration.getTemplate("item.ftl");
        //3.创建输出流指定生成静态页面的位置和名称
        String fileName = goodsId + ".html";
        String filePath = servletContext.getRealPath(fileName);             //获取项目的绝对目录
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File(filePath)), "utf-8");
        //4.生成
        template.process(map, osw);
        //5.关流
        if(osw != null){
            osw.close();
        }

    }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try{
            String realPath = servletContext.getRealPath("");
            for (Long id : goodsIds) {
                new File(realPath + id + ".html").delete();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
