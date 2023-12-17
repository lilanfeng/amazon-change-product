package com.kevin.lee.dto;

import lombok.Data;

/**
 * @author lilanfeng
 */
@Data
public class ProductInfoData {

    private int id;

    private String upc;

    private String ean;

    private String asin;

    /**
     * 标题
     */
    private String title;

    /**
     * 评分
     */
    private String score;

    /**
     * 评论
     */
    private String commentsCount;

    /**
     * 品牌
     */
    private String brand;

    private String mpn;

    /**
     * 变体
     */
    private String bianTi;

    /**
     * 卖家数量（购物车） 卖家数量：0没有购物车，1有购物车
     */
    private String shopCartNum;

    /**
     * 颜色
     */
    private String color;

    /**
     * 有图/无图/rating
     */
    private String picture;

    /**
     * GL
     *
     */
    private String gl;


    /**
     * 详情页面路径
     */
    private String detailUrl;

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", asin='" + asin + '\'' +
                //", upc='" + upc + '\'' +
               // ", mpn='" + mpn + '\'' +
                //", score='" + score + '\'' +
                //", commentsCount='" + commentsCount + '\'' +
                //", brand='" + brand + '\'' +
                //", ean='" + ean + '\'' +
                //", title='" + title + '\'' +
                '}';
    }
}
