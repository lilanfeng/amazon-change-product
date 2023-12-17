package com.kevin.lee.test;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author lilanfeng2089，微信：lilanfeng2089
 * @description
 * @github https://github.com/lilanfeng
 * @Copyright 公众号：lilanfeng2089 | 博客：https://lilanfeng2089.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class TextReaderWorker extends SwingWorker<StringBuilder, ProgressData> {
    private File file;
    private StringBuilder text = new StringBuilder();
    private JTextArea textArea;
    private JLabel statusLine;
    private JMenuItem openItem;
    private JMenuItem cancelItem;
    public TextReaderWorker(File file,JTextArea textArea,JLabel statusLine,JMenuItem openItem,JMenuItem cancelItem){
        this.file = file;
        this.textArea = textArea;
        this.statusLine = statusLine;
        this.openItem = openItem;
        this.cancelItem = cancelItem;
    }


    @Override
    protected StringBuilder doInBackground() throws Exception {
        int lineNumber = 0;
        Scanner in = new Scanner(new FileInputStream(file));
        while (in.hasNextLine()){
            String line = in.nextLine();
            lineNumber++;
            text.append(line);
            text.append("\n");
            ProgressData data = new ProgressData();
            data.number = lineNumber;
            data.line = line;
            publish(data);
            Thread.sleep(100);
        }
        return null;
    }

    @Override
    protected void  process(List<ProgressData> data){
        if(isCancelled()){
            return;
        }
        StringBuilder b = new StringBuilder();
        statusLine.setText("" + data.get(data.size() -1).number);
        for(ProgressData d :data){
            b.append(d.line);
            b.append("\n");
        }
        textArea.append(b.toString());

    }

    @Override
    protected void done(){
        try{
            StringBuilder result = get();
            if(result != null){
                textArea.setText(result.toString());
                //statusLine.setText("Done");
            }else {
                statusLine.setText("Done");
            }

        } catch (InterruptedException e){

        } catch (CancellationException ex){
            textArea.setText("");
            statusLine.setText("Canceled");
        } catch (ExecutionException ex){
            statusLine.setText("" + ex.getCause());
        }
        cancelItem.setEnabled(false);
        openItem.setEnabled(true);
    }

}