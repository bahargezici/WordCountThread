package com.improvedigital.service;

import java.util.ArrayList;
import java.util.List;

public class WordCount {

    public static void main(String args[]) {
        String filePath1 = "/Users/bahar.gezici/Desktop/t3.txt";
        String filePath2 = "/Users/bahar.gezici/Desktop/t4.txt";

        WordCountService ws = new WordCountService();

        List<String> fileList = new ArrayList<String>();
        fileList.add(filePath1);
        fileList.add(filePath2);

        ws.process(fileList);
    }
}
