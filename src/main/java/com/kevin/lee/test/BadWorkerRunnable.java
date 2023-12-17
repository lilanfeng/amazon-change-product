package com.kevin.lee.test;

import javax.swing.*;
import java.util.Random;

/**
 * @author lilanfeng2089，微信：lilanfeng2089
 * @description
 * @github https://github.com/lilanfeng
 * @Copyright 公众号：lilanfeng2089 | 博客：https://lilanfeng2089.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class BadWorkerRunnable implements Runnable{
    private JComboBox comboBox;
    private Random generator;

    public BadWorkerRunnable(JComboBox aCombo){
        comboBox = aCombo;
        generator = new Random();
    }
    @Override
    public void run() {
        try {
            while (true){

                int i = Math.abs(generator.nextInt());
                if(i % 2 == 0){
                    comboBox.insertItemAt(i,0);
                }else {
                    if (comboBox.getItemCount() != 0) {
                        comboBox.removeItemAt(i % comboBox.getItemCount());
                    }
                }
                Thread.sleep(100);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
