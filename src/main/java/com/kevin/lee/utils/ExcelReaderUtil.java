package com.kevin.lee.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.kevin.lee.dto.InputProductInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelReaderUtil {
    public static final String DEFAULT_SHEET_NAME = "Products";

    private static final Log logger = LogFactory.get();

    /**
     *
     * @param file
     * @return
     */
    public static List<InputProductInfo> getInputFileData(File file){
        List<InputProductInfo> inputProductInfoList = new ArrayList<>();

        try {
            ExcelReader reader = ExcelUtil.getReader(file,DEFAULT_SHEET_NAME);
            List<Map<String,Object>> resultMapList = reader.readAll();
            resultMapList.stream().forEach(dataMap->{

                InputProductInfo info = new InputProductInfo();
                if(dataMap.containsKey("ASIN") && dataMap.get("ASIN") != null){
                    info.setAsin(dataMap.get("ASIN").toString());
                }
                if(dataMap.containsKey("UPC") && dataMap.get("UPC") != null){
                    info.setUpc(dataMap.get("UPC").toString());
                }

                if(dataMap.containsKey("MPN") && dataMap.get("MPN") != null){
                    info.setMpn(dataMap.get("MPN").toString());
                }

                if(dataMap.containsKey("评分") && dataMap.get("评分") != null){
                    info.setScore(dataMap.get("评分").toString());
                }

                if(dataMap.containsKey("评论") && dataMap.get("评论") != null){
                    info.setCommentsCount(dataMap.get("评论").toString());
                }

                if(dataMap.containsKey("品牌") && dataMap.get("品牌") != null){
                    info.setBrand(dataMap.get("品牌").toString());
                }

                if(dataMap.containsKey("标题") && dataMap.get("标题") != null){
                    info.setTitle(dataMap.get("标题").toString());
                }

                if(StrUtil.isNotBlank(info.getAsin())) {
                    inputProductInfoList.add(info);
                }

            });
        } catch (Exception e){
            logger.error(e,"ExcelReaderUtil.getInputFileData.error file:{}",file.getName());
        }
        return inputProductInfoList;
    }


    public static void main(String[] args) {
        String inputDir = "D:\\workspace\\Person\\workspace";
        List<InputProductInfo> inputProductInfoList = ExcelReaderUtil.getInputFileData(FileUtil.file(inputDir));
    }
}
