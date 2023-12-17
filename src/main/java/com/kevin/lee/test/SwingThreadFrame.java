package com.kevin.lee.test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author lilanfeng2089，微信：lilanfeng2089
 * @description
 * @github https://github.com/lilanfeng
 * @Copyright 公众号：lilanfeng2089 | 博客：https://lilanfeng2089.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class SwingThreadFrame extends JFrame {

    public SwingThreadFrame(){
        setTitle("SwingThreadTest");

        final JComboBox comboBox = new JComboBox();
        comboBox.insertItemAt(Integer.MAX_VALUE,0);
        comboBox.setPrototypeDisplayValue(comboBox.getItemAt(0));
        comboBox.setSelectedIndex(0);

        JPanel panel = new JPanel();

        JButton goodButton = new JButton("Good");
        goodButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new GoodWorkerRunnable(comboBox)).start();
            }
        });
        panel.add(goodButton);

        JButton badButton = new JButton("Bad");
        badButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new BadWorkerRunnable(comboBox)).start();
            }
        });
        panel.add(badButton);

        panel.add(comboBox);
        add(panel);
        pack();


    }

}
