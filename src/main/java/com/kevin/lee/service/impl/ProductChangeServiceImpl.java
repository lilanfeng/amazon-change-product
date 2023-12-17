package com.kevin.lee.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.kevin.lee.App;
import com.kevin.lee.dto.InputProductInfo;
import com.kevin.lee.service.ProductChangeService;
import com.kevin.lee.utils.ConsoleUtil;
import com.kevin.lee.utils.ExcelReaderUtil;
import com.kevin.lee.utils.ExcelWriteUtil;
import com.kevin.lee.utils.HttpHelpUtil;
import com.kevin.lee.worker.ProductChangeWork;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductChangeServiceImpl implements ProductChangeService {

    private static final Log logger = LogFactory.get();

    private ProductChangeWork productChangeWork;

    public ProductChangeServiceImpl(ProductChangeWork work){
        this.productChangeWork = work;
    }


    @Override
    public List<File> getInputFileList(List<String> selectFileNameList) {
        if(selectFileNameList == null || selectFileNameList.size() <= 0){
            return null;
        }

        List<File> fileList = new ArrayList<>();
        selectFileNameList.forEach(fileName->{
            File inputFile = FileUtil.file(fileName);

            if(isExcelFile(inputFile)){
                fileList.add(inputFile);
            }
        });

        return fileList;

        /**
        File inputFile = FileUtil.file(inputDir);
        List<File> fileList = new ArrayList<>();
        if(inputFile == null){
            return fileList;
        }
        if( inputFile.isDirectory()){
            File[] files = inputFile.listFiles();

            if(files.length <= 0){
                return fileList;
            }

            for (File fi:files) {
                if(isExcelFile(fi)){
                    fileList.add(fi);
                }
            }
            return fileList;

        } else if(inputFile.isFile()) {
            if(isExcelFile(inputFile)){
                fileList.add(inputFile);
            }
            return fileList;
        }

        return fileList;

         **/
    }

    @Override
    public void doProductChange(List<String> selectFileNameList, String outDirName, String cookie) {
        this.productChangeWork.publishEvent(ConsoleUtil.console("doProductChange.getInputFileList.start！"));
        List<File> inputFileList = getInputFileList(selectFileNameList);
        if(CollUtil.isEmpty(inputFileList)){
            this.productChangeWork.publishEvent(ConsoleUtil.console("获取输入的产品数量为空！"));
        }
        /**
        this.productChangeWork.publishEvent(ConsoleUtil.console("doProductChange.getInputData.start！");
        List<InputProductInfo> inputProductInfoList = getInputData(inputFileList);
        this.productChangeWork.publishEvent(ConsoleUtil.console("本次获取输入的产品的数量为："+CollUtil.size(inputProductInfoList));
        List<InputProductInfo> outProductInfoList = changeProduct(textArea,inputProductInfoList,cookie);
        this.productChangeWork.publishEvent(ConsoleUtil.console("本次转换有效的产品的数量为："+CollUtil.size(outProductInfoList));
        ExcelWriteUtil.saveOutFileData(outProductInfoList,outDirName);
         **/
        changeProductByFile(inputFileList,outDirName,cookie);

    }

    private void changeProductByFile(List<File> inputFileList,String outDirName,String cookie){
        Map<String, Integer> existAsinMap = new HashMap<>(1024);

        AtomicInteger outCount = new AtomicInteger();
        AtomicBoolean isSecond = new AtomicBoolean(false);
        inputFileList.forEach(file -> {
            //休眠1秒，不要太频繁请求接口
            if(isSecond.get() && App.config.getFileTimeout(App.config.getCountryWeb()) > 0){
                try {
                    this.productChangeWork.publishEvent(ConsoleUtil.console( "本次文件间隔为：" + App.config.getFileTimeout(App.config.getCountryWeb()) +" ms"));
                    Thread.sleep(App.config.getFileTimeout(App.config.getCountryWeb()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    logger.error(e,"fileTimeout exception");
                }
            }
            isSecond.set(true);

            if (productChangeWork.isStop()) {
                // 完成一个文件后才取消任务
                productChangeWork.publishEvent(ConsoleUtil.console("一个文件处理开始，开始取消处理！"));
                productChangeWork.stopWork();
                return;
            }

            List<InputProductInfo> inputProductInfoList = getInputDataByFile(file, existAsinMap);
            this.productChangeWork.publishEvent(ConsoleUtil.console(
                    "本次" + file.getName() + "获取输入的产品的数量为：" + CollUtil.size(inputProductInfoList)));

            /**
             * 配置可以生成两份文件,一份有销售此商品的数据,一份是没有销售此商品的数据
             */
            List<InputProductInfo> unSellProductInfoList = new ArrayList<>();
            List<InputProductInfo> outProductInfoList = changeProduct( inputProductInfoList, cookie,outCount.get(),unSellProductInfoList);

            this.productChangeWork.publishEvent(ConsoleUtil.console(
                    "本次" + file.getName() + "转换有效的产品的数量为：" + CollUtil.size(outProductInfoList)));
            outCount.addAndGet(CollUtil.size(outProductInfoList));
            String saveDirName  = getOutDir(file);
            ExcelWriteUtil.saveOutFileData(outProductInfoList, saveDirName);

            //没有销售此商品的数据的保存
            String unSellDirName = saveDirName.replace(".xlsx","-unSell.xlsx");
            ExcelWriteUtil.saveOutFileData(unSellProductInfoList, unSellDirName);
        });
    }

    /**
     * @param file
     * @return
     */
    private String getOutDir(File file) {
        String outDir = "";
        String fileName = "";
        if (file != null) {
            outDir = file.getAbsolutePath();
            if (file.isFile()) {
                fileName = file.getName();
                fileName = fileName.substring(0,fileName.lastIndexOf("."));
                fileName = fileName +"_" + DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
                outDir = StrUtil.replace(outDir, file.getName(), "");
            }
        }
        outDir += DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATE_PATTERN) +
                File.separator +fileName+ ".xlsx";
        return outDir;
    }

    /**
     *
     * @param file
     * @param existAsinMap
     * @return
     */
    private List<InputProductInfo> getInputDataByFile(File file,Map<String,Integer> existAsinMap){
        if (!file.exists()) {
            return null;
        }
        List<InputProductInfo> list = ExcelReaderUtil.getInputFileData(file);
        List<InputProductInfo> outList = new ArrayList<>();
        if (list.size() > 0) {
            list.stream().forEach(inputProductInfo -> {
                if (!existAsinMap.containsKey(inputProductInfo.getAsin())) {
                    outList.add(inputProductInfo);
                    existAsinMap.put(inputProductInfo.getAsin(), 1);
                }
            });
        }
        return list;
    }

    private List<InputProductInfo> getInputData(List<File> fileList){
        if(CollUtil.isEmpty(fileList)){
            return null;
        }
        Map<String,InputProductInfo> inputProductInfoMap = new HashMap<>();
        fileList.stream().forEach(file -> {
            List<InputProductInfo> list  =  ExcelReaderUtil.getInputFileData(file);
            if( list.size() > 0) {
                list.stream().forEach(inputProductInfo -> {
                    inputProductInfoMap.putIfAbsent(inputProductInfo.getAsin(),inputProductInfo);
                });

            }
        });

        List<InputProductInfo> inputProductInfoList = new ArrayList<>(inputProductInfoMap.size());
        inputProductInfoMap.forEach((key,value)->{
            inputProductInfoList.add(value);
        });
        return inputProductInfoList;
    }

    /**
     *
     * @param file
     * @return
     */
    private boolean isExcelFile(File file){
        if(file.isFile() ){
            String extName = FileNameUtil.extName(file.getName()).toLowerCase();
            if(StrUtil.equalsAny(extName,StrUtil.splitToArray("xls,xlsx",","))){
                return true;
            }
        }
        return false;
    }


    /**
     *
     * @param list
     * @param cookie
     * @return
     */
    private List<InputProductInfo> changeProduct(List<InputProductInfo> list, String cookie,int totalCount,List<InputProductInfo> unSellProductInfoList) {
        List<InputProductInfo> outProductInfoList = new ArrayList<>();
        if (list == null || list.size() <= 0) {
            return null;
        }
        AtomicInteger outCount = new AtomicInteger();
        AtomicInteger unSellCount = new AtomicInteger();
        list.stream().forEach(inputProductInfo -> {
            InputProductInfo outInfo = new InputProductInfo();
            boolean isSell = HttpHelpUtil.getChangeProduct(productChangeWork,inputProductInfo, cookie,outInfo);
            if (productChangeWork.isStop()) {
                productChangeWork.publishEvent(ConsoleUtil.console("取消进行中，等本文件解析完成就停止！"));
            }
            if (isSell) {
                outCount.getAndIncrement();
                outInfo.setId(outCount.get());
                outProductInfoList.add(outInfo);
                this.productChangeWork.publishEvent(ConsoleUtil.console("Suc:"+outInfo));
            }else {
                unSellCount.getAndIncrement();
                outInfo.setId(unSellCount.get());
                unSellProductInfoList.add(outInfo);
            }
        });
        return outProductInfoList;
    }


}
