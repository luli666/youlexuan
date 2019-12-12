package com.luli.code.service;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;

public interface ItemPageService {
    /*
        生成商品详细页
     */
    Map<String, Object> findGoodsData(Long goodsId);
    void createStaticPage(Long goodsId, Map<String, Object> rootMap) throws IOException, TemplateException;
    boolean deleteItemHtml(Long[] goodsIds);
}
