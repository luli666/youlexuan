package com.luli.code.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.luli.code.entity.Result;
import com.luli.code.pojo.Cart;
import com.luli.code.service.CartService;
import com.luli.code.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    /*
        购物车列表
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListString = CookieUtil.getCookieValue(request,"cartList","UTF-8");

        if(cartListString == null || cartListString.equals("")){
            cartListString = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        if(username.equals("anonymousUser")){       //如果未登录
            return cartList_cookie;
        }else {      //登录状态
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);//从redis中取出
            if (cartList_cookie.size()>0){      //如果存在本地购物车
                //合并购物车
                cartList_redis = cartService.mergeCartList(cartList_redis, cartList_cookie);
                //清楚本地cookie购物车
                CookieUtil.deleteCookie(request, response, "cartList");
                //将合并后的数据存入redis中
                cartService.saveCartListToRedis(username, cartList_redis);
            }
            return cartList_redis;
        }
    }
    /*
        添加商品到购物车
     */
    //@CrossOrigin相当于设置了响应头信息，跨域请求
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:8082",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId,Integer num){

        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try{
            List<Cart> cartList = findCartList();//获取购物车列表
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if(username.equals("anonymousUser")){       //如果是未登录，保存到cookie中
                //向cookie中添加数据
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList),3600*24,"UTF-8");
            }else {//如果登录状态，保存到redis中
                cartService.saveCartListToRedis(username, cartList);
            }

            return new Result(true, "添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
}
