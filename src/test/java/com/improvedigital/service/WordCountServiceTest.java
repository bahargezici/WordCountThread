package com.improvedigital.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WordCountServiceTest {

    static String filePath1 = "/Users/bahar.gezici/Desktop/t3.txt";
    static String filePath2 = "/Users/bahar.gezici/Desktop/t4.txt";

    static List<String> fileList = new ArrayList<String>();
    static
    {
        fileList.add(filePath1);
        fileList.add(filePath2);
    }

    public static void main(String[] args) {
        testThreading();
    }


    public static void testThreading() {
        WordCountService wordCountService = new WordCountService() {
            @Override
            protected void putMap(ConcurrentHashMap<Word, Word> map, File file, String wordInFile) throws InterruptedException {
                if (map.containsKey(new Word(wordInFile))) {

                    incrementExistingWord(map, file, wordInFile);
                } else {
                    Word word = new Word(wordInFile, file.getName());
                    Thread.sleep(1000);
                    if (map.putIfAbsent(word, word) != null) {
                        incrementExistingWord(map, file, wordInFile);
                    }
                }
            }
        };
        wordCountService.process(fileList);
    }
}
