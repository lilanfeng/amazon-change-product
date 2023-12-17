package com.kevin.lee.dto;

import lombok.Data;

/**
 * @author lilanfeng
 */
@Data
public class InputProductInfo {

    private int id;

    /**
     *
     */
    private String asin;

    private String upc;

    private String mpn;

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


    private String ean;

    /**
     * 标题
     */
    private String title;

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
