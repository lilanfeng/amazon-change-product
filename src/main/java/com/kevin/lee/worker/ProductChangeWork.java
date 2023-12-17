package com.kevin.lee.worker;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.dialect.console.ConsoleLog;
import com.kevin.lee.App;
import com.kevin.lee.dto.ProgressData;
import com.kevin.lee.service.impl.ProductChangeServiceImpl;
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
public class ProductChangeWork extends SwingWorker<StringBuilder, ProgressData> {

    private JTextArea textArea1;
    private JButton buttonStart;
    private JButton buttonCancel;
    private boolean isStop;

    public ProductChangeWork(JButton buttonStart,JButton buttonCancel,JTextArea textArea1){
        this.buttonStart = buttonStart;
        this.buttonCancel = buttonCancel;
        this.textArea1 = textArea1;
        this.isStop = false;
    }



    @Override
    protected StringBuilder doInBackground() throws Exception {


        publish(new ProgressData(ConsoleUtil.console("亚马逊产品转换开始处理...")));

        String inputDir = App.config.getInputDir(App.config.getCountryWeb());
        if (StrUtil.isBlank(inputDir)) {
            publish(new ProgressData(ConsoleUtil.console("需要转换的产品的表格目录不能为空！！！")));
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
            ProductChangeServiceImpl productChangeService = new ProductChangeServiceImpl(this);
            publish(new ProgressData(ConsoleUtil.console("亚马逊产品转换doProductChange.start...")));
            //Thread.sleep(1000);
            productChangeService.doProductChange(selectFileNameList, outDirName, cookies);
            publish(new ProgressData(ConsoleUtil.console("亚马逊产品转换doProductChange.end...")));

        } catch (Throwable e) {
            publish(new ProgressData(ConsoleUtil.console("产品转换过程中发生异常:" + e.getMessage())));

            return null;
        }

        publish(new ProgressData(ConsoleUtil.console("转换完成!\n")));
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
        textArea1.append(b.toString());

    }

    @Override
    protected void done(){
        try{
            StringBuilder result = get();
            if(result != null){
                textArea1.setText(result.toString());
            }
        } catch (InterruptedException e){

        } catch (CancellationException ex){
            textArea1.append(ConsoleUtil.console("取消完成"));
        } catch (ExecutionException ex){
            textArea1.setText("" + ex.getCause());
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
