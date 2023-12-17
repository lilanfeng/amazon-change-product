package com.kevin.lee.ui.form;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.kevin.lee.App;
import com.kevin.lee.service.impl.ProductChangeServiceImpl;
import com.kevin.lee.service.impl.ProductListServiceImpl;
import com.kevin.lee.utils.ConsoleUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lilanfeng
 */
@Getter
public class ProductListForm {
    private static final Log logger = LogFactory.get();
    private static ProductListForm productListForm;
    private JPanel mainPanel;
    private JTextArea textArea1;
    private JButton buttonStart;
    private JButton buttonClear;
    private JButton buttonOpen;
    private JScrollPane mainScrollPane;

    public ProductListForm() {
        buttonStart = new JButton();
        $$$setupUI$$$();

        buttonOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputDir = App.config.getInputDir(App.config.getCountryWeb());
                List<String> selectFileNameList = StrUtil.split(inputDir, ";", true, true);
                if (selectFileNameList == null || selectFileNameList.size() <= 0) {
                    JOptionPane.showMessageDialog(mainPanel, "转换后的目录暂时不存在!\n", "信息提示!", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                // String pathName = getOutDir(selectFileNameList.get(0)) + File.separator + DateUtil.format(LocalDateTime.now(),
                //        DatePattern.PURE_DATE_PATTERN);
                String pathName = "";
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
        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        buttonStart.addActionListener(e -> onOK());
    }

    public static ProductListForm getInstance() {
        if (productListForm == null) {
            productListForm = new ProductListForm();
        }
        return productListForm;
    }

    private void onOK() {
        buttonStart.setEnabled(false);
        try {
            ConsoleUtil.consoleWithLog(textArea1, "Search key Thread start ...");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        doChange();
                    } catch (Exception e) {
                        logger.error(e, "Search key has exception.");
                        ConsoleUtil.consoleWithLog(textArea1, "Search key exception ..." + e.getMessage());
                        endChangeProduct();
                    } catch (Throwable throwable) {
                        logger.error(throwable, "Search key has exception.");
                        ConsoleUtil.consoleWithLog(textArea1, "Search key Throwable ..." + throwable.getMessage());
                        endChangeProduct();
                    }
                }
            });
            thread.start();
            //ThreadUtil.execute(this::doChange);
        } catch (Exception e) {
            logger.error(e, "Search key Thread  has exception.");
            ConsoleUtil.consoleWithLog(textArea1, "Search key Thread exception ..." + e.getMessage());
            endChangeProduct();
        } catch (Throwable throwable) {
            logger.error(throwable, "Search key Thread has exception.");
            ConsoleUtil.consoleWithLog(textArea1, "Search key Thread  Throwable ..." + throwable.getMessage());
            endChangeProduct();
        } finally {
            ConsoleUtil.consoleWithLog(textArea1, "doChange Thread finally ...");
        }
        ConsoleUtil.consoleWithLog(textArea1, "doChange Thread start end ...");
    }

    /**
     * 产品转换入口
     */
    private void doChange() {
        ConsoleUtil.consoleWithLog(textArea1, "亚马逊产品搜索列表开始处理...");
        logger.info("Initializing System...");

        String searchKeys = App.config.getSearchKeys(App.config.getCountryWeb());
        if (StrUtil.isBlank(searchKeys)) {
            ConsoleUtil.consoleWithLog(textArea1, "需要转换的产品的表格目录不能为空！！！");
            endChangeProduct();
            return;
        }
        List<String> searchKeyList = StrUtil.split(searchKeys, ";", true, true);

        String cookies = App.config.getCookies(App.config.getCountryWeb());
        if (StrUtil.isBlank(cookies)) {
            ConsoleUtil.consoleWithLog(textArea1, "转换登录网站的Cookie不能为空！！！");
            endChangeProduct();
            return;
        }


        String outDir = App.config.getSearchDir(App.config.getCountryWeb());
        // String outDirName = outDir + File.separator + DateUtil.format(LocalDateTime.now(),
        //        DatePattern.PURE_DATE_PATTERN) + File.separator + "product_list_" + DateUtil.format(LocalDateTime.now(),
        //        DatePattern.PURE_DATETIME_MS_PATTERN) + ".xlsx";

        try {
            ProductListServiceImpl productListService = new ProductListServiceImpl();
            ConsoleUtil.consoleWithLog(textArea1, "亚马逊产品搜索列表doProductList.start...");
            productListService.doProductList(textArea1, searchKeyList, outDir, cookies);
            ConsoleUtil.consoleWithLog(textArea1, "亚马逊产品搜索列表doProductList.end...");

        } catch (Throwable e) {
            logger.error(e, "ProductListForm.doProductList.throwable searchKeys:{},outDirName:{},cookies:{}", searchKeys, outDir, cookies);
            ConsoleUtil.consoleWithLog(textArea1, "亚马逊产品搜索列表过程中发生异常:" + e.getMessage());
            endChangeProduct();
            return;
        }

        JOptionPane.showMessageDialog(mainPanel, "亚马逊产品搜索列表完成!\n", "成功!", JOptionPane.PLAIN_MESSAGE);
        endChangeProduct();
        return;

    }

    private void endChangeProduct() {
        ConsoleUtil.consoleWithLog(textArea1, "亚马逊产品搜索列表处理结束...");
        buttonStart.setEnabled(true);
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainScrollPane = new JScrollPane();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainScrollPane.setViewportView(mainPanel);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 10, 10), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        textArea1 = new JTextArea();
        panel1.add(textArea1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW
                , null, new Dimension(150, 50), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 10, 10), -1, -1));
        mainPanel.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0,
                false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        buttonStart.setText("开始");
        panel3.add(buttonStart, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        buttonClear = new JButton();
        buttonClear.setText("清理信息");
        panel3.add(buttonClear, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonOpen = new JButton();
        buttonOpen.setText("打开生成文件目录");
        panel3.add(buttonOpen, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainScrollPane;
    }

}
