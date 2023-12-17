package com.kevin.lee.service;

import javax.swing.*;
import java.util.List;

public interface ProductListService {

    void doProductList(JTextArea textArea, List<String> searchKeyList, String outDirName, String cookie);

}
