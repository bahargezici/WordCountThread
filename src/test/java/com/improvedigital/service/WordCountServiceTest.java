package com.improvedigital.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * JUnit test for {@link WordCountService}
 */
@ContextConfiguration(classes = {WordCountService.class})
@ComponentScan
//@SpringBootTest(classes = {WordCountService.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class WordCountServiceTest {

    List<String> fileList = new ArrayList<String>();

    @Autowired
    private WordCountService wordCountService;

    @Before
    public void setup() {
        String filePath1 = "/Users/bahar.gezici/Desktop/t3.txt";
        String filePath2 = "/Users/bahar.gezici/Desktop/t4.txt";

        fileList.add(filePath1);
        fileList.add(filePath2);
    }

    /**
     * Test for Threads
     */
    @Test
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
        ConcurrentHashMap<Word, Word> map = wordCountService.process(fileList);
        System.out.println("map.values()"+ map.values());

        Map.Entry<Word,Word> entry = map.entrySet().iterator().next();
        Word key = entry.getKey();

        System.out.println("key : "+ key.getWordString());
        System.out.println("key : "+ key.getCount());

        assertEquals("Map must be : ", "salim 3", key.getWordString()+ " "+ key.getCount());
    }

    /**
     * Test for Map
     */
    @Test
    public void testMap() {
        WordCountService wordCountService = new WordCountService();
        ConcurrentHashMap<Word, Word> map = wordCountService.process(fileList);

        Map.Entry<Word,Word> entry = map.entrySet().iterator().next();
        Word key = entry.getKey();

        assertEquals("Map must be : ", "salim 3", key.getWordString()+" "+ key.getCount());
    }

    /**
     * Test constructor of {@link WordCountService}
     */
    @Test
    public void testConstructor() {
        assertNotNull("WordCountService should not be null", new WordCountService());
    }
}
