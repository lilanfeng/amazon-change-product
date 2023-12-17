package com.kevin.lee.ui.listener;


import com.kevin.lee.App;
import com.kevin.lee.ui.Init;
import com.kevin.lee.utils.SystemUtil;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Form event monitoring
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/10.
 */
public class FrameListener {


    public static void addListeners() {
        App.mainFrame.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (SystemUtil.isWindowsOs()) {
                    App.mainFrame.setVisible(false);
                } else {
                    App.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }

                Init.shutdown();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }
        });

    }

}
