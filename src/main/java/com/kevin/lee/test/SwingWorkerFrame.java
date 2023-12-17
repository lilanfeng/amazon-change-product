package com.kevin.lee.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author lilanfeng2089，微信：lilanfeng2089
 * @description
 * @github https://github.com/lilanfeng
 * @Copyright 公众号：lilanfeng2089 | 博客：https://lilanfeng2089.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class SwingWorkerFrame extends JFrame {
    private JFileChooser chooser;
    private JTextArea textArea;
    private JLabel statusLine;
    private JMenuItem openItem;
    private JMenuItem cancelItem;
    private SwingWorker<StringBuilder, ProgressData> textReader;
    public static final int WIDTH = 450;
    private static final int HEIGHT = 350;

    public SwingWorkerFrame() {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        textArea = new JTextArea();
        add(new JScrollPane(textArea));
        add(new JScrollPane(textArea));
        setSize(WIDTH,HEIGHT);

        statusLine = new JLabel(" ");
        add(statusLine, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        openItem = new JMenuItem("Open");
        menu.add(openItem);

        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = chooser.showOpenDialog(null);
                if(result == JFileChooser.APPROVE_OPTION){
                    textArea.setText("");
                    openItem.setEnabled(false);
                    textReader = new TextReaderWorker(chooser.getSelectedFile(),textArea,statusLine,openItem,cancelItem);
                    textReader.execute();
                    cancelItem.setEnabled(true);
                }
            }
        });

        cancelItem = new JMenuItem("Cancel");
        menu.add(cancelItem);
        cancelItem.setEnabled(false);
        cancelItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textReader.cancel(true);

            }
        });

    }

    /**
    public class ProgressData {
        public int number;
        private String line;
    }


    private class TextReader extends SwingWorker<StringBuilder, ProgressData> {
        private File file;
        private StringBuilder text = new StringBuilder();
        public TextReader(File file){
            this.file = file;
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

        **/

}

