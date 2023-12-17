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
import com.kevin.lee.enums.CountryWebEnum;
import com.kevin.lee.worker.ProductChangeWork;
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
 * @author lilanfeng
 */
@Getter
public class ProductChangeForm {
    private static final Log logger = LogFactory.get();
    private static ProductChangeForm productChangeForm;
    private JPanel mainPanel;
    private JTextArea textArea1;
    private JButton buttonStart;
    private JButton buttonClear;
    private JButton buttonOpenIE;
    private JScrollPane mainScrollPane;
    private JButton buttonOpenExpoler;
    private JLabel selectCountryLabel;
    private JButton buttonCancel;

    /**
     * 商品拉取程序实现入口
     */
    private ProductChangeWork productChangeWork;

    public static ProductChangeForm getInstance() {
        if (productChangeForm == null) {
            productChangeForm = new ProductChangeForm();
        }
        return productChangeForm;
    }

    public ProductChangeForm() {


        buttonClear.addActionListener(e -> {
            try {
                productChangeForm.getTextArea1().getDocument().remove(0, productChangeForm.getTextArea1().getDocument().getLength());
                productChangeForm.getTextArea1().setText("");
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            //productChangeForm.getTextArea1().setText("");
        });

        buttonOpenIE.addActionListener(e -> {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI("https://"+CountryWebEnum.getChooseHost(App.config.getCountryWeb())+"/product-search/search?q=B00DHNJ724&ref_=xx_catadd_dnav_xx"));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });
        buttonStart.addActionListener(e -> {
            textArea1.setText("");
            buttonStart.setEnabled(false);
            productChangeWork = new ProductChangeWork(buttonStart, buttonCancel, textArea1);
            productChangeWork.execute();
            buttonCancel.setEnabled(true);

        });
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productChangeWork.setStop(true);
                //productChangeWork.cancel(true);
            }
        });
        buttonCancel.setEnabled(false);


        buttonOpenExpoler.addActionListener(e -> {
            String inputDir = App.config.getInputDir(App.config.getCountryWeb());
            List<String> selectFileNameList = StrUtil.split(inputDir, ";", true, true);
            if (selectFileNameList == null || selectFileNameList.size() <= 0) {
                JOptionPane.showMessageDialog(mainPanel, "转换后的目录暂时不存在!\n", "信息提示!", JOptionPane.INFORMATION_MESSAGE);
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
        });
        selectCountryLabel.setText("当前的国家站点是:" + CountryWebEnum.getCountry(App.config.getCountryWeb()));
    }


    public void updateSelectCountryLabel() {
        selectCountryLabel.setText("当前的国家站点是:" + CountryWebEnum.getCountry(App.config.getCountryWeb()));
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
        mainPanel.setLayout(new GridLayoutManager(2, 2, new Insets(10, 10, 10, 10), -1, -1));
        mainPanel.setAlignmentX(0.5f);
        mainPanel.setAlignmentY(0.5f);
        mainPanel.setEnabled(true);
        mainPanel.setMaximumSize(new Dimension(1024, 768));
        mainPanel.setPreferredSize(new Dimension(228, 101));
        mainScrollPane.setViewportView(mainPanel);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setEnabled(true);
        panel1.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0,
                false));
        textArea1 = new JTextArea();
        textArea1.setEnabled(true);
        scrollPane1.setViewportView(textArea1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 10, 10), -1, -1));
        mainPanel.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0,
                false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        buttonClear = new JButton();
        buttonClear.setText("清理信息");
        panel3.add(buttonClear, new GridConstraints(0, 3, 3, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonStart = new JButton();
        buttonStart.setHorizontalAlignment(0);
        buttonStart.setText("开始");
        buttonStart.setVerticalAlignment(0);
        panel3.add(buttonStart, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH
                , GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonOpenIE = new JButton();
        buttonOpenIE.setText("启动浏览器");
        panel3.add(buttonOpenIE, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("取消");
        panel3.add(buttonCancel, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        buttonOpenExpoler = new JButton();
        buttonOpenExpoler.setText("打开生成文件目录");
        panel2.add(buttonOpenExpoler, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectCountryLabel = new JLabel();
        selectCountryLabel.setText("Label");
        panel2.add(selectCountryLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
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
