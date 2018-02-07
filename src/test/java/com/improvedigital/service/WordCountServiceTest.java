package com.improvedigital.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertNotNull;

/**
 * JUnit test for {@link WordCountService}
 */
@ContextConfiguration(classes = {WordCountService.class})
@ComponentScan
@SpringBootTest(classes = {WordCountService.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class WordCountServiceTest {

    static String filePath1 = "/Users/bahar.gezici/Desktop/t3.txt";
    static String filePath2 = "/Users/bahar.gezici/Desktop/t4.txt";

    static List<String> fileList = new ArrayList<String>();

    static {
        fileList.add(filePath1);
        fileList.add(filePath2);
    }

//    public static void main(String[] args) {
//        testThreading();
//    }


    @Before
    public void testThreading() {
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

    @Autowired
    private WordCountService wordCountService;

    /**
     * Test constructor of {@link WordCountService}
     */
    @Test
    public void testConstructor() {
        assertNotNull("WordCountService should not be null", new WordCountService());
    }
}
