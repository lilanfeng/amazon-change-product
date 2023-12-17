package com.kevin.lee.service;

import javax.swing.*;
import java.io.File;
import java.util.List;

public interface ProductChangeService {

    List<File> getInputFileList(List<String> selectFileNameList);

    void doProductChange(List<String> selectFileNameList,String outDirName,String cookie);

}
