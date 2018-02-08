package com.improvedigital.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * JUnit test for {@link WordCountService}
 */
@ContextConfiguration(classes = {WordCountService.class})
@ComponentScan
@RunWith(SpringJUnit4ClassRunner.class)
public class WordCountServiceTest {
    private static final Logger LOG = LoggerFactory.getLogger(WordCountServiceTest.class);
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
        Word entry = map.iterator().next();
        LOG.info("map: {}",map);

        assertEquals("Map must be patik 5 : ", "patik 5", entry.getWordString()+" "+ entry.getCount());
    }

    /**
     * Test for Map
     */
    @Test
    public void testMap() {
        Set<Word> map = wordCountService.process(fileList);
        List tmp = new ArrayList();
        map.forEach(k -> tmp.add(k.getCount()));
        Collections.sort(tmp, Collections.reverseOrder());
        LOG.info("map: {}", tmp.toString());

        assertEquals("Map must be sorted","[5, 4, 3, 1]", tmp.toString());
    }

    /**
     * Test constructor of {@link WordCountService}
     */
    @Test
    public void testConstructor() {
        assertNotNull("WordCountService should not be null", new WordCountService());
    }
}