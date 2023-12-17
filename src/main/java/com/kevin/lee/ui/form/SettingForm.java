package com.kevin.lee.ui.form;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.kevin.lee.App;
import com.kevin.lee.bean.CountryWeb;
import com.kevin.lee.enums.CountryWebEnum;
import lombok.Getter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author lilanfeng
 */
@Getter
public class SettingForm {
    private JPanel mainPanel;
    private JButton buttonCancel;
    private JButton buttonSave;
    private JTextPane textPaneInputDir;
    private JButton buttonSelect;
    private JPanel cookieLabel;
    private JTextArea textAreaCookie;
    private JScrollPane mainScrollPane;
    private JTextPane textPaneSleepTime;
    private JTextPane textPaneFileTimeout;
    private JTextPane textPaneTimeout;
    private JLabel selectCountryLabel;
    private JComboBox selectCountryComboBox;

    private static SettingForm settingForm;

    public SettingForm() {
        buttonSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel表格文件", "xlsx", "xls");
                chooser.setFileFilter(filter);
                chooser.setMultiSelectionEnabled(true);

                //int returnVal = chooser.showOpenDialog(new JPanel());
                chooser.showSaveDialog(mainPanel);

                //File selectFile = chooser.getSelectedFile();
                File[] selectFileArr = chooser.getSelectedFiles();
                StringBuilder inputDir = new StringBuilder();
                if (selectFileArr != null && selectFileArr.length > 0) {
                    for (int i = 0; i < selectFileArr.length; i++) {
                        File selectFile = selectFileArr[i];
                        if (selectFile != null) {
                            if (selectFile.isFile()) {
                                inputDir.append(selectFile.getAbsolutePath()).append(";");
                            }
                        }
                    }
                }

                textPaneInputDir.setText(inputDir.toString());
            }
        });
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonSave.setEnabled(false);
                String cookie = textAreaCookie.getText();
                String inputDir = textPaneInputDir.getText();
                if (StrUtil.isBlank(cookie)) {
                    JOptionPane.showMessageDialog(mainPanel, "Cookies不能为空,请输入对应的值!\n", "失败!", JOptionPane.ERROR_MESSAGE);
                    buttonSave.setEnabled(true);
                    return;
                }
                if (StrUtil.isBlank(inputDir)) {
                    JOptionPane.showMessageDialog(mainPanel, "请选择对应的输入文件!\n", "失败!", JOptionPane.ERROR_MESSAGE);
                    buttonSave.setEnabled(true);
                    return;
                }
                int sleepTime = 500;
                try {
                    sleepTime = NumberUtil.parseInt(textPaneSleepTime.getText());
                } catch (NumberFormatException exception) {
                    sleepTime = -1;
                }

                if (sleepTime < 0) {
                    JOptionPane.showMessageDialog(mainPanel, "请输入正确的接口请求间隔时间（毫秒）!\n", "失败!", JOptionPane.ERROR_MESSAGE);
                    buttonSave.setEnabled(true);
                    return;
                }

                int fileTimeout = 500;
                try {
                    fileTimeout = NumberUtil.parseInt(textPaneFileTimeout.getText());
                } catch (NumberFormatException exception) {
                    fileTimeout = -1;
                }

                if (fileTimeout < 0) {
                    JOptionPane.showMessageDialog(mainPanel, "请输入正确的文件间的请求调用间隔时间(毫秒)!\n", "失败!", JOptionPane.ERROR_MESSAGE);
                    buttonSave.setEnabled(true);
                    return;
                }

                int timeout = 500;
                try {
                    timeout = NumberUtil.parseInt(textPaneTimeout.getText());
                } catch (NumberFormatException exception) {
                    timeout = -1;
                }

                if (timeout < 0) {
                    JOptionPane.showMessageDialog(mainPanel, "请输入正确的超时后再次发起请求间隔时间(毫秒)!\n", "失败!", JOptionPane.ERROR_MESSAGE);
                    buttonSave.setEnabled(true);
                    return;
                }
//                String searchKeys = textArea1.getText();
//
//                String searchDir = textPaneSave.getText();
//                String keePaKey = textPaneKeePaKey.getText();
                String countryWeb = CountryWebEnum.getValue(selectCountryComboBox.getSelectedItem().toString());

                App.config.setCountryWeb(countryWeb);
                App.config.setCookies(cookie, countryWeb);
                App.config.setInputDir(inputDir, countryWeb);
                App.config.setSleepTime(sleepTime, countryWeb);
                App.config.setFileTimeout(fileTimeout, countryWeb);
                App.config.setTimeout(timeout, countryWeb);
//                App.config.setSearchKeys(searchKeys);
//                App.config.setSearchDir(searchDir);
//                App.config.setKeePaKey(keePaKey);

                App.config.save();
                buttonSave.setEnabled(true);
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonCancel.setEnabled(false);
                textPaneInputDir.setText(App.config.getInputDir(App.config.getCountryWeb()));
                textAreaCookie.setText(App.config.getCookies(App.config.getCountryWeb()));
                textPaneSleepTime.setText(String.valueOf(App.config.getSleepTime(App.config.getCountryWeb())));
                textPaneFileTimeout.setText(String.valueOf(App.config.getFileTimeout(App.config.getCountryWeb())));
                textPaneTimeout.setText(String.valueOf(App.config.getTimeout(App.config.getCountryWeb())));
//                textArea1.setText(App.config.getSearchKeys());
//                textPaneSave.setText(App.config.getSearchDir());
//                textPaneKeePaKey.setText(App.config.getKeePaKey());
                buttonCancel.setEnabled(true);
            }
        });
        //if (App.config.getCountryWebJson().isEmpty()) {
        //    App.config.setCountryWebJson(JSON.toJSONString(CountryWebEnum.getCountryWebList()));
        //}


        for (CountryWeb countryWeb : CountryWebEnum.getCountryWebList()) {
            selectCountryComboBox.addItem(countryWeb.getNameKey());
        }
        selectCountryComboBox.setSelectedItem(CountryWebEnum.getCountry(App.config.getCountryWeb()));

        textPaneInputDir.setText(App.config.getInputDir(App.config.getCountryWeb()));
        textAreaCookie.setText(App.config.getCookies(App.config.getCountryWeb()));
        textPaneSleepTime.setText(String.valueOf(App.config.getSleepTime(App.config.getCountryWeb())));
        textPaneFileTimeout.setText(String.valueOf(App.config.getFileTimeout(App.config.getCountryWeb())));
        textPaneTimeout.setText(String.valueOf(App.config.getTimeout(App.config.getCountryWeb())));


//        buttonSearchDir.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser chooser = new JFileChooser();
//                chooser.showSaveDialog(mainPanel);
//
//                File selectFile = chooser.getSelectedFile();
//                textPaneSave.setText(selectFile.getAbsolutePath());
//            }
//        });

        selectCountryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String countryWeb = CountryWebEnum.getValue(selectCountryComboBox.getSelectedItem().toString());

                textPaneInputDir.setText(App.config.getInputDir(countryWeb));
                textAreaCookie.setText(App.config.getCookies(countryWeb));
                textPaneSleepTime.setText(String.valueOf(App.config.getSleepTime(countryWeb)));
                textPaneFileTimeout.setText(String.valueOf(App.config.getFileTimeout(countryWeb)));
                textPaneTimeout.setText(String.valueOf(App.config.getTimeout(countryWeb)));
//                textArea1.setText(App.config.getSearchKeys());
//                textPaneSave.setText(App.config.getSearchDir());
//                textPaneKeePaKey.setText(App.config.getKeePaKey());
                buttonCancel.setEnabled(true);

            }
        });
    }

    public static SettingForm getInstance() {
        if (settingForm == null) {
            settingForm = new SettingForm();
        }
        return settingForm;
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainScrollPane = new JScrollPane();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setMaximumSize(new Dimension(1024, 768));
        mainPanel.setName("设置");
        mainScrollPane.setViewportView(mainPanel);
        cookieLabel = new JPanel();
        cookieLabel.setLayout(new GridLayoutManager(8, 2, new Insets(0, 0, 0, 0), -1, -1));
        cookieLabel.setName("Cookie 值:");
        mainPanel.add(cookieLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        final JLabel label1 = new JLabel();
        label1.setText("Cookie值 :");
        cookieLabel.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("待转换产品目录或文件：");
        cookieLabel.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        cookieLabel.add(panel1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        textPaneInputDir = new JTextPane();
        panel1.add(textPaneInputDir, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW
                , null, new Dimension(150, 50), null, 0, false));
        buttonSelect = new JButton();
        buttonSelect.setText("...");
        panel1.add(buttonSelect, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textAreaCookie = new JTextArea();
        textAreaCookie.setWrapStyleWord(true);
        cookieLabel.add(textAreaCookie, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW
                , null, new Dimension(150, 50), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("接口请求间隔时间（毫秒）：");
        cookieLabel.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
        textPaneSleepTime = new JTextPane();
        cookieLabel.add(textPaneSleepTime, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW
                , null, new Dimension(150, 50), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("文件间的请求调用间隔时间(毫秒):");
        cookieLabel.add(label4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
        textPaneFileTimeout = new JTextPane();
        cookieLabel.add(textPaneFileTimeout, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW
                , null, new Dimension(150, 50), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("超时后再次发起请求间隔时间(毫秒):");
        cookieLabel.add(label5, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
        textPaneTimeout = new JTextPane();
        cookieLabel.add(textPaneTimeout, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW
                , null, new Dimension(150, 50), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setEnabled(true);
        label6.setText("");
        cookieLabel.add(label6, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("");
        cookieLabel.add(label7, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
        selectCountryLabel = new JLabel();
        selectCountryLabel.setText("选中的国家站点:");
        cookieLabel.add(selectCountryLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
        selectCountryComboBox = new JComboBox();
        cookieLabel.add(selectCountryComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        buttonCancel = new JButton();
        buttonCancel.setText("取消");
        panel3.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonSave = new JButton();
        buttonSave.setText("保存");
        panel3.add(buttonSave, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainScrollPane;
    }

}

