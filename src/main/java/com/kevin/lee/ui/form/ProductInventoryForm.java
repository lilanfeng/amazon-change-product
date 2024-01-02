package com.kevin.lee.ui.form;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.kevin.lee.App;
import com.kevin.lee.enums.CountryWebEnum;
import com.kevin.lee.worker.ProductChangeWork;
import com.kevin.lee.worker.ProductInventoryWork;
import lombok.Getter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 商品拉取程序实现入口
     */
    private ProductInventoryWork productInventoryWork;

    public static ProductInventoryForm getInstance() {
        if (productInventoryForm == null) {
            productInventoryForm = new ProductInventoryForm();
        }
        return productInventoryForm;
    }

    public ProductInventoryForm() {


        /**
         * 打开浏览器
         */
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI("https://" + CountryWebEnum.getChooseHost(App.config.getCountryWeb()) + "/product-search/search?q=B00DHNJ724&ref_=xx_catadd_dnav_xx"));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }

            }
        });
        /**
         * 开始按钮
         */
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                informationTextArea.setText("");
                startButton.setEnabled(false);
                if (ObjectUtil.isNull(productInventoryWork)) {
                    productInventoryWork = new ProductInventoryWork(startButton, cancelButton, informationTextArea);
                }
                productInventoryWork.execute();
                cancelButton.setEnabled(true);
            }
        });

        /**
         * 取消按钮
         */
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productInventoryWork.setStop(true);
            }
        });
        cancelButton.setEnabled(false);
        /**
         *  清理信息
         */
        cleanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    productInventoryForm.getInformationTextArea().getDocument().remove(0, productInventoryForm.getInformationTextArea().getDocument().getLength());
                    productInventoryForm.getInformationTextArea().setText("");
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }

            }
        });
        /**
         * 打开导出目录
         */
        openLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputDir = App.config.getInventoryInputDir(App.config.getCountryWeb());
                List<String> selectFileNameList = StrUtil.split(inputDir, ";", true, true);
                if (selectFileNameList == null || selectFileNameList.size() <= 0) {
                    JOptionPane.showMessageDialog(mainPanel, "目录暂时不存在!\n", "信息提示!", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String pathName = getOutDir(selectFileNameList.get(0)) + File.separator + DateUtil.format(LocalDateTime.now(),
                        DatePattern.PURE_DATE_PATTERN);
                File outDirFile = FileUtil.file(pathName);
                if (!outDirFile.exists()) {
                    JOptionPane.showMessageDialog(mainPanel, "转换后的目录暂时不存在!\n", "信息提示!", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                //Runtime.getRuntime().exec("explorer /e,/select," + outDirFile.getAbsolutePath());
                try {
                    Desktop.getDesktop().open(outDirFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        updateSelectCountryLabel();
    }

    public void updateSelectCountryLabel() {
        countryLabel.setText("当前的国家站点是:" + CountryWebEnum.getCountry(App.config.getCountryWeb()));
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
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainScrollPane.setViewportView(mainPanel);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 10, 10), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        openButton = new JButton();
        openButton.setText("打开浏览器");
        panel1.add(openButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        startButton = new JButton();
        startButton.setText("开始");
        panel1.add(startButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("取消");
        panel1.add(cancelButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cleanButton = new JButton();
        cleanButton.setText("清理信息");
        panel1.add(cleanButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        openLoadButton = new JButton();
        openLoadButton.setText("打开导出目录");
        panel1.add(openLoadButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        countryLabel = new JLabel();
        countryLabel.setText("当前选择站点");
        panel1.add(countryLabel, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0,
                false));
        informationTextArea = new JTextArea();
        scrollPane1.setViewportView(informationTextArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainScrollPane;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


}
