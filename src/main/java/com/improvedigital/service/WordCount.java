package com.improvedigital.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * This class to test {@link WordCountService}, with its main method.
 */
public class WordCount {
    private static final Logger LOG = LoggerFactory.getLogger(WordCount.class);

    public static void main(String args[]) {
        LOG.trace("Main Class for Testing WordCountService");
        String filePath1 = "./src/test/resources/data/t1.txt";
        String filePath2 = "./src/test/resources/data/t2.txt";

        WordCountService ws = new WordCountService();
        LOG.trace("WordCountService has created {}", ws);

        List<String> fileList = new ArrayList<String>();
        fileList.add(filePath1);
        fileList.add(filePath2);
        LOG.trace("Current FileList is {}", fileList.toString());

        ws.process(fileList);
    }
}
