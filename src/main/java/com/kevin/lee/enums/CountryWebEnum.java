package com.kevin.lee.enums;


import com.kevin.lee.bean.CountryWeb;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lilanfeng2089，微信：lilanfeng2089
 * @description 亚马逊站点配置
 * @github https://github.com/lilanfeng
 * @Copyright 公众号：lilanfeng2089 | 博客：https://lilanfeng2089.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public enum CountryWebEnum {
    US("亚马逊美国站-us","sellercentral.amazon.com","亚马逊美国网站"),
    UK("亚马逊英国站-uk","sellercentral.amazon.co.uk","亚马逊英国网站"),
    DE("亚马逊德国站-de","sellercentral.amazon.de","亚马逊德国网站"),
    JP("亚马逊日本站-jp","sellercentral.amazon.co.jp","亚马逊日本网站"),
    CA("亚马逊加拿大-ca","sellercentral.amazon.ca","亚马逊加拿大网站 "),
    FR("亚马逊法国站-fr","sellercentral.amazon.fr","亚马逊法国网站"),
    IT("亚马逊意大利站-it","sellercentral.amazon.it","亚马逊意大利网站"),
    ES("亚马逊西班牙站-es","sellercentral.amazon.es","亚马逊西班牙网站"),
    NL("亚马逊荷兰站-nl","sellercentral.amazon.nl","亚马逊荷兰网站"),
    AU("亚马逊澳大利亚站-au","sellercentral.amazon.com.au","亚马逊澳大利亚网站"),
    SG("亚马逊新加坡站-sg","sellercentral.amazon.sg","亚马逊新加坡网站"),
    BR("亚马逊巴西站-br","sellercentral.amazon.com.br","亚马逊巴西网站"),
    AE("亚马逊阿拉伯联合酋长国站-ae","sellercentral.amazon.ae","亚马逊阿拉伯联合酋长国网站"),
    IN("亚马逊印度站-in","sellercentral.amazon.in","亚马逊印度网站");

    private String country;

    private String host;

    private String desc;

    private CountryWebEnum(String country,String host,String desc){
        this.country = country;
        this.host = host;
        this.desc = desc;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 获取 value
     * @param country
     * @return
     */
    public static String getValue(String country){
        CountryWebEnum countryWebEnum = null;
        for (CountryWebEnum temp : CountryWebEnum.values()) {
            if (temp.getCountry().equals(country)) {
                countryWebEnum = temp;
                break;
            }
        }
        if (countryWebEnum != null) {
            return countryWebEnum.name();
        }
        return "";
    }

    /**
     * 获取 value
     * @param name
     * @return
     */
    public static String getCountry(String name){
        CountryWebEnum countryWebEnum = null;
        for (CountryWebEnum temp : CountryWebEnum.values()) {
            if (temp.name().equals(name)) {
                countryWebEnum = temp;
                break;
            }
        }
        if (countryWebEnum != null) {
            return countryWebEnum.getCountry();
        }

        return "";
    }

    public static List<CountryWeb> getCountryWebList(){
        List<CountryWeb> countryWebList = new ArrayList<>();
        for (CountryWebEnum temp:CountryWebEnum.values()){
            countryWebList.add(new CountryWeb(temp.name(),temp.getCountry(),temp.getHost(),temp.getDesc()));
        }
        return countryWebList;
    }

    /**
     *
     * @param name
     * @return
     */
    public static String getChooseHost(String name){
        for (CountryWebEnum temp:CountryWebEnum.values()){
            if(temp.name().equals(name)){
                return temp.getHost();
            }
        }
        return "";
    }


}
