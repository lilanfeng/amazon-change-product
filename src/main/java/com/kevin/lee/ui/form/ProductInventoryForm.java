package com.kevin.lee.ui.form;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author lilanfeng2089，微信：lilanfeng2089
 * @description
 * @github https://github.com/lilanfeng
 * @Copyright 公众号：lilanfeng2089 | 博客：https://lilanfeng2089.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
@Getter
public class ProductInventoryForm {
    private static final Log logger = LogFactory.get();

    private static ProductInventoryForm productInventoryForm;
    private JScrollPane mainScrollPane;
    private JPanel mainPanel;
    private JTextArea informationTextArea;
    private JButton openButton;
    private JButton startButton;
    private JButton cancelButton;
    private JButton cleanButton;
    private JButton openLoadButton;
    private JLabel countryLabel;

    public static ProductInventoryForm getInstance() {
        if (productInventoryForm == null) {
            productInventoryForm = new ProductInventoryForm();
        }
        return productInventoryForm;
    }

    public ProductInventoryForm(){


        /**
         * 打开浏览器
         */
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        /**
         * 开始按钮
         */
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        /**
         * 取消按钮
         */
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        /**
         *  清理信息
         */
        cleanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        /**
         * 打开导出目录
         */
        openLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }




}
