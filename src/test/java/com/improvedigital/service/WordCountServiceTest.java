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
import java.util.Set;
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
        String filePath1 = "./src/test/resources/t3.txt";
        String filePath2 = "./src/test/resources/t4.txt";

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
        Set<Word> map = wordCountService.process(fileList);
        System.out.println("map.values()"+ map);

        Word entry = map.iterator().next();

        System.out.println("key : "+ entry.getWordString());
        System.out.println("key : "+ entry.getCount());

        assertEquals("Map must be : ", "patik 5", entry.getWordString()+ " "+ entry.getCount());
    }

    /**
     * Test for Map
     */
    @Test
    public void testMap() {
        WordCountService wordCountService = new WordCountService();
        Set<Word> map = wordCountService.process(fileList);

        Word entry = map.iterator().next();

        assertEquals("Map must be : ", "patik 5", entry.getWordString()+" "+ entry.getCount());
    }

    /**
     * Test constructor of {@link WordCountService}
     */
    @Test
    public void testConstructor() {
        assertNotNull("WordCountService should not be null", new WordCountService());
    }
}