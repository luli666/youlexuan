package com.luli.code.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.luli.code.pojo.Item;
import com.luli.code.pojo.Specification;
import com.luli.code.service.ItemSearchService;
import com.luli.code.utils.RedisConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void importList(List list) {         //更新索引库
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {            //删除数据时，删除索引库中的数据
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);

        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    @Override
    public Map<String, Object> search(Map searchMap) {

        Map<String, Object> map = highSearch(searchMap);
        List list = searchCategoryList(searchMap);

        map.put("categoryList", list);
        //查询品牌和规格选项
        if(list.size() > 0){
            map.putAll(searchBrandAndSpecList(list.get(0) + ""));
        }
        return map;
    }

    //1.根据关键字 到solr中查询(分页) 总条数 总页数
    public Map<String, Object> highSearch(Map searchMap) {
        Map map = new HashMap<>();
        HighlightQuery query = new SimpleHighlightQuery();

        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>");         //设置高亮前缀
        highlightOptions.setSimplePostfix("</em>");                           //设置高亮后缀
        query.setHighlightOptions(highlightOptions);                           //设置高亮显示

        //1.按照关键字查询
        String keywords = (String) searchMap.get("keywords");
        if(keywords != null) {
            keywords = keywords.replaceAll(" ", "");     //处理多个分类查询,去掉空格后才能使用分词查询

            Criteria criteria = new Criteria("item_keywords").is(keywords);
            query.addCriteria(criteria);
        }
        //2.按分类筛选
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //3.按品牌筛选
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //4.按规格筛选
        if (searchMap.get("spec") != null){
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filtercriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filtercriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //5.按价格筛选
        if(!"".equals(searchMap.get("price"))){
            //获取价格数组
            String[] prices = ((String) searchMap.get("price")).split("-");
            if(!prices[0].equals("0")){                 //区间的起点不为0
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if(!prices[1].equals("*")){
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //6.排序
        String sortField = (String) searchMap.get("sortField"); //排序字段
        String sort = (String) searchMap.get("sort");           //排序规则
        if(sort != null && sortField != null){
            if(sort.equals("ASC")){
                Sort sort1 = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort1);
            }
            if(sort.equals("DESC")){
                Sort sort1 = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort1);
            }
        }

        //设置查询条件
        Integer pageNo = (Integer) searchMap.get("pageNo");     //获取页数
        Integer pageSize = (Integer) searchMap.get("pageSize"); //获取每页记录数
        query.setOffset((pageNo-1) * pageSize);                 //设置开始查找的索引
        query.setRows(pageSize);                                //设置每页记录数

        //用于存储修改高亮显示后的记录
        List<Item> list = new ArrayList<>();

        HighlightPage<Item> page = solrTemplate.queryForHighlightPage(query, Item.class);
        for (HighlightEntry<Item> highlightEntry : page.getHighlighted()) {
            Item item = highlightEntry.getEntity();
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            if(highlights != null && highlights.size() > 0){
                //获取高亮的标题集合
                List<String> titles = highlights.get(0).getSnipplets();
                if(titles !=null && titles.size() > 0){
                    //获取高亮的标题
                    item.setTitle(titles.get(0));
                }
            }
            list.add(item);
        }

        map.put("rows", list );
        map.put("total", page.getTotalElements());              //返回总记录数
        map.put("totalPages", page.getTotalPages());            //返回总页数

        return map;
    }

    //2.根据查询的参数，到solr中获取对应的分类结果，因为分类有重复，以分组的方式去重复
    public List searchCategoryList(Map searchMap){
        List<String> list = new ArrayList<>();
        //创建查询对象
        SimpleQuery query = new SimpleQuery();
        //创建查询条件对象--按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //将查询条件对象放入查询对象中
        query.addCriteria(criteria);

        //创建分组对象
        GroupOptions groupOptions = new GroupOptions();
        //根据分类域进行分组
        groupOptions.addGroupByField("item_category");
        //将分组对象放入查询对象中
        query.setGroupOptions(groupOptions);

        //使用分组查询    分类集合
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        //获取分类域集合
        GroupResult<Item> item_category = items.getGroupResult("item_category");
        //获取分类域实体集合
        Page<GroupEntry<Item>> groupEntries = item_category.getGroupEntries();
        //遍历实体集合
        for (GroupEntry<Item> groupEntry : groupEntries.getContent()) {
            list.add(groupEntry.getGroupValue());       //将结果封装到list集合中
        }
        return list;
    }

    //4.根据分类名称查询对应品牌集合和规格集合
    public Map searchBrandAndSpecList(String category){
        Map map = new HashMap<>();
        Long typeId = (Long) redisTemplate.boundHashOps(RedisConst.ITEMCAT).get(category);
        if(typeId != null){
            //根据模板id查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps(RedisConst.BRAND_LIST).get(typeId);
            map.put("brandList",brandList );

            //根据模板id查询规格列表
            List<Specification> specList = (List) redisTemplate.boundHashOps(RedisConst.SPEC_LIST).get(typeId);
            map.put("specList", specList);

        }
        return map;
    }
}
