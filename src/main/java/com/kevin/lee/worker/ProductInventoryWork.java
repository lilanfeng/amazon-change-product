package com.kevin.lee.worker;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.kevin.lee.App;
import com.kevin.lee.dto.ProgressData;
import com.kevin.lee.service.impl.ProductInventoryServiceImpl;
import com.kevin.lee.utils.ConsoleUtil;

import javax.swing.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author lilanfeng2089，微信：lilanfeng2089
 * @description
 * @github https://github.com/lilanfeng
 * @Copyright 公众号：lilanfeng2089 | 博客：https://lilanfeng2089.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class ProductInventoryWork extends SwingWorker<StringBuilder, ProgressData> {

    private JTextArea informationTextArea;
    private JButton buttonStart;
    private JButton buttonCancel;
    private boolean isStop;

    public ProductInventoryWork(JButton buttonStart, JButton buttonCancel, JTextArea informationTextArea){
        this.buttonStart = buttonStart;
        this.buttonCancel = buttonCancel;
        this.informationTextArea = informationTextArea;
        this.isStop = false;
    }



    @Override
    protected StringBuilder doInBackground() throws Exception {


        publish(new ProgressData(ConsoleUtil.console("亚马逊产品抓取开始处理...")));

        String inputDir = App.config.getInventoryInputDir(App.config.getCountryWeb());
        if (StrUtil.isBlank(inputDir)) {
            publish(new ProgressData(ConsoleUtil.console("需要转换的表格目录不能为空！！！")));
            return null;
        }
        List<String> selectFileNameList = StrUtil.split(inputDir, ";", true, true);

        String cookies = App.config.getCookies(App.config.getCountryWeb());
        if (StrUtil.isBlank(cookies)) {
            publish(new ProgressData(ConsoleUtil.console("转换登录网站的Cookie不能为空！！")));
            return null;
        }
        inputDir = selectFileNameList.get(0);

        String outDir = getOutDir(inputDir);
        inputDir = outDir;
        String outDirName = outDir + File.separator + DateUtil.format(LocalDateTime.now(),
                DatePattern.PURE_DATE_PATTERN) + File.separator + "product_change_" + DateUtil.format(LocalDateTime.now(),
                DatePattern.PURE_DATETIME_MS_PATTERN) + ".xlsx";

        try {
            ProductInventoryServiceImpl productInventoryService = new ProductInventoryServiceImpl(this);
            publish(new ProgressData(ConsoleUtil.console("亚马逊产品抓取.start...")));
            //Thread.sleep(1000);
            productInventoryService.doProductChange(selectFileNameList, outDirName, cookies);
            publish(new ProgressData(ConsoleUtil.console("亚马逊产品抓取.end...")));

        } catch (Throwable e) {
            publish(new ProgressData(ConsoleUtil.console("产品抓取过程中发生异常:" + e.getMessage())));

            return null;
        }

        publish(new ProgressData(ConsoleUtil.console("抓取完成!\n")));
        return null;
    }

    @Override
    protected void  process(List<ProgressData> data){
        if(isCancelled()){
            return;
        }
        StringBuilder b = new StringBuilder();
        for(ProgressData d :data){
            b.append(d.getLine());
            b.append("\n");
        }
        informationTextArea.append(b.toString());

    }

    @Override
    protected void done(){
        try{
            StringBuilder result = get();
            if(result != null){
                informationTextArea.setText(result.toString());
            }
        } catch (InterruptedException e){

        } catch (CancellationException ex){
            informationTextArea.append(ConsoleUtil.console("取消抓取完成"));
        } catch (ExecutionException ex){
            informationTextArea.setText("" + ex.getCause());
        }
        buttonCancel.setEnabled(false);
        buttonStart.setEnabled(true);
    }

    public void publishEvent(String message){
        this.publish(new ProgressData(message));
    }


    /**
     * 正在结束任务
     */
    public void stopWork(){
        this.cancel(true);
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
        buttonCancel.setEnabled(false);
        publishEvent(ConsoleUtil.console("开始取消任务...."));
    }

    /**
     * @param inputDir
     * @return
     */
    private String getOutDir(String inputDir) {
        File file = FileUtil.file(inputDir);
        String outDir = "";
        if (file != null) {
            outDir = file.getAbsolutePath();
            if (file.isFile()) {
                outDir = StrUtil.replace(outDir, File.separator + file.getName(), "");
            }
        }
        return outDir;
    }
}
