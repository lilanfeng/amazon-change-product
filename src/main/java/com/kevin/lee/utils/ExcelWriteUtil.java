package com.kevin.lee.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.kevin.lee.dto.InputProductInfo;
import com.kevin.lee.dto.ProductInfoData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelWriteUtil {

    private static final Log logger = LogFactory.get();

    public static void saveOutFileData(List<InputProductInfo> list,String saveFileName){

        if(CollUtil.isEmpty(list)){
            return;
        }

        try {
            boolean isHasSave = false;
            AtomicInteger rowCount = new AtomicInteger();
            if (FileUtil.file(saveFileName).exists()) {
                ExcelReader excelReader = ExcelUtil.getReader(saveFileName);
                rowCount.set(excelReader.getRowCount());
                excelReader.close();
                isHasSave = true;
            }
            ExcelWriter writer  = ExcelUtil.getWriter(saveFileName);
            Map<String,String> headerAliasMap = new HashMap<>();
            headerAliasMap.put("id","序号");
            headerAliasMap.put("upc","UPC");
            headerAliasMap.put("ean","EAN");
            headerAliasMap.put("asin","ASIN");
            headerAliasMap.put("title","标题");
            headerAliasMap.put("score","评分");
            headerAliasMap.put("commentsCount","评论");
            headerAliasMap.put("brand","品牌");
            writer.setHeaderAlias(headerAliasMap);
            List<String> head = new ArrayList<>();
            head.add("序号");
            head.add("UPC");
            head.add("EAN");
            head.add("ASIN");
            head.add("标题");
            head.add("评分");
            head.add("评论");
            head.add("品牌");
            if(isHasSave){
                writer.setSheet(0);
                writer.writeHeadRow(head);
                writer.setCurrentRow(rowCount.get());
            } else {
                writer.renameSheet("Product");
                writer.writeHeadRow(head);
            }
            writer.write(list);
            writer.flush();
            writer.close();
        }catch (Exception e){
            logger.error("文件：" + saveFileName + " 写入失败 exception:",e);
        }
    }

    /**
     *
     * @param list
     * @param saveFileName
     */
    public static void saveProductInfoData(List<ProductInfoData> list, String saveFileName){
        if(CollUtil.isEmpty(list)){
            return;
        }
        try {
            boolean isHasSave = false;
            AtomicInteger rowCount = new AtomicInteger();
            if (FileUtil.file(saveFileName).exists()) {
                ExcelReader excelReader = ExcelUtil.getReader(saveFileName);
                rowCount.set(excelReader.getRowCount());
                excelReader.close();
                isHasSave = true;
            }
            ExcelWriter writer  = ExcelUtil.getWriter(saveFileName);
            Map<String,String> headerAliasMap = new HashMap<>();
            headerAliasMap.put("id","序号");
            headerAliasMap.put("upc","UPC");
            headerAliasMap.put("ean","EAN");
            headerAliasMap.put("asin","ASIN");
            headerAliasMap.put("title","标题");
            headerAliasMap.put("score","评分");
            headerAliasMap.put("commentsCount","评论");
            headerAliasMap.put("brand","品牌");
            headerAliasMap.put("bianTi","变体");
            headerAliasMap.put("shopCartNum","卖家数量（购物车）");
            headerAliasMap.put("color","颜色");
            headerAliasMap.put("picture","有图/无图/rating");
            headerAliasMap.put("gl","GL");
            writer.setHeaderAlias(headerAliasMap);
            List<String> head = new ArrayList<>();
            head.add("序号");
            head.add("UPC");
            head.add("EAN");
            head.add("ASIN");
            head.add("标题");
            head.add("评分");
            head.add("评论");
            head.add("品牌");
            head.add("变体");
            head.add("卖家数量（购物车）");
            head.add("颜色");
            head.add("有图/无图/rating");
            head.add("GL");
            if(isHasSave){
                writer.setSheet(0);
                writer.writeHeadRow(head);
                writer.setCurrentRow(rowCount.get());
            } else {
                writer.renameSheet("Product");
                writer.writeHeadRow(head);
            }
            writer.write(list);
            writer.flush();
            writer.close();
        }catch (Exception e){
            logger.error("文件：" + saveFileName + " 写入失败 exception:",e);
        }
    }

}
