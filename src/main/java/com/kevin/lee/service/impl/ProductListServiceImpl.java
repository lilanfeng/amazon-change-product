package com.kevin.lee.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.keepa.api.backend.structs.Product;
import com.kevin.lee.dto.InputProductInfo;
import com.kevin.lee.dto.ProductInfoData;
import com.kevin.lee.service.ProductListService;
import com.kevin.lee.utils.ConsoleUtil;
import com.kevin.lee.utils.ExcelWriteUtil;
import com.kevin.lee.utils.HttpHelpUtil;
import com.kevin.lee.utils.KeepaApiUtil;
import com.kevin.lee.worker.ProductChangeWork;

import javax.swing.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductListServiceImpl implements ProductListService {
    @Override
    public void doProductList(JTextArea textArea, List<String> searchKeyList, String outDir, String cookie) {

        if(CollUtil.isEmpty(searchKeyList)){
            ConsoleUtil.consoleWithLog(textArea,"search list key is empty");
            return;
        }

        for (String searchKey:searchKeyList) {
            if(StrUtil.isEmpty(searchKey)){
                ConsoleUtil.consoleWithLog(textArea,"search key is empty");
                continue;
            }
            ConsoleUtil.consoleWithLog(textArea,"search key:"+searchKey+" start");
//            List<ProductInfoData>  inputProductInfoList = HttpHelpUtil.getProductList(textArea,searchKey,cookie);
//            if(inputProductInfoList == null){
//                ConsoleUtil.consoleWithLog(textArea,"search key:"+searchKey+" end totalCount: 0");
//                continue;
//            }
            List<ProductInfoData> infoDataList = getKeePaData(searchKey);
            ConsoleUtil.consoleWithLog(textArea,"KeePaData search key:"+searchKey+" end totalCount:"+infoDataList.size());

            List<ProductInfoData> productInfoDataList = changeProductInfoDataList(textArea,cookie,infoDataList);

            if(productInfoDataList.size() <= 0){
                continue;
            }
            int number = 0;
            for (int i = 0; i < productInfoDataList.size(); i++) {
                number++;
                productInfoDataList.get(i).setId(number);
            }
            String outDirName = outDir + File.separator + DateUtil.format(LocalDateTime.now(),
                    DatePattern.PURE_DATE_PATTERN) + File.separator + "product_list_"  + DateUtil.format(LocalDateTime.now(),
                    DatePattern.PURE_DATETIME_MS_PATTERN) + ".xlsx";
            ExcelWriteUtil.saveProductInfoData(productInfoDataList,outDirName);
        }

    }

    /**
     *
     * @param key
     */
    private List<ProductInfoData> getKeePaData(String key){
        int page = 0;
        List<Product > list = new ArrayList<>();
        boolean hasNext = KeepaApiUtil.getProductSearch(key,page,list);
        while (hasNext && page <= 9){
            page++;
            KeepaApiUtil.getProductSearch(key,page,list);
        }
        return changeKeePaToProductInfoData(list);
    }

    /**
     *
     * @param list
     * @return
     */
    private List<ProductInfoData> changeKeePaToProductInfoData(List<Product > list){
        if(list == null || list.size() <= 0){
            return null;
        }
        List<ProductInfoData> productInfoDataList = new ArrayList<>();
        list.stream().forEach(product -> {
            ProductInfoData productInfoData = new ProductInfoData();
            if(StrUtil.isEmpty(product.asin)){
                return;
            }

            productInfoData.setAsin(product.asin);
            productInfoData.setTitle(product.title);
            productInfoData.setBrand(product.brand);
            productInfoData.setEan(product.eanList != null ? product.eanList.length > 0? product.eanList[0]:"":"");
            productInfoData.setUpc(product.upcList != null ? product.upcList.length > 0 ? product.upcList[0]:"":"");
            productInfoDataList.add(productInfoData);
        });

        return productInfoDataList;
    }


    /**
     *
     * @param productInfoDataList
     * @return
     */
    private List<ProductInfoData> changeProductInfoDataList(JTextArea textArea, String cookie,List<ProductInfoData> productInfoDataList){

        List<ProductInfoData> list = new ArrayList<>();
        productInfoDataList.stream().forEach(productInfoData -> {
            //ProductInfoData temp = HttpHelpUtil.getChangeProduct(new ProductChangeWork(),productInfoData,cookie);
            ProductInfoData temp = null;
            if(temp != null){
                getProductDetailInfo(temp.getDetailUrl(),temp);
                list.add(temp);
            }
        });
        return list;

    }



    /**
     * 详情页面解析处理
     * @param url
     * @param productInfoData
     */
    private void getProductDetailInfo(String url,ProductInfoData productInfoData) {
        HttpHelpUtil.getProductDetailInfo(url,productInfoData);
    }


}
