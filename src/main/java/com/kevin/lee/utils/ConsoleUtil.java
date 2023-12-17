package com.kevin.lee.utils;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.swing.*;
import java.util.Date;

/**
 * <pre>
 * ConsoleUtil to print text into textarea
 * </pre>
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/12/15.
 */
@Slf4j
public class ConsoleUtil {

    private static final Log logger = LogFactory.get();

    public static void consoleWithLog(JTextArea textArea, String log) {
        textArea.append(DateFormatUtils.format(new Date(), "yyyyMMdd-HH:mm:ss.SSS") + ":");
        textArea.append(log + "\n");
        textArea.setCaretPosition(textArea.getText().length());
        logger.info(log);
    }

    public static void consoleOnly(JTextArea textArea, String log) {
        textArea.append(log + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    public static String console(String log){
        StringBuilder str = new StringBuilder();
        str.append(DateFormatUtils.format(new Date(), "yyyyMMdd-HH:mm:ss.SSS"));
        str.append(":");
        str.append(log);
        //logger.info(str.toString());
        return str.toString();
    }

}
