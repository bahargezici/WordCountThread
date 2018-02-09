package com.improvedigital.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * General service that holds in memory {@link ConcurrentHashMap} for the {@link Word}s
 *
 * Although {@link ConcurrentHashMap} is not thread safe, putIfAbsent is used to make threads safe.
 */
public class WordCountService {
    private static final Logger LOG = LoggerFactory.getLogger(WordCountService.class);

    /**
     * Parent Process using the given files {@link Word}
     *
     * @param fileList : List of the given files {@link List<String>} to use
     * @return the created {@link Set<Word>}
     */
    public Set<Word> process(List<String> fileList) {
        LOG.debug("process is starting : {}", fileList.toString());
        ExecutorService executor = Executors.newFixedThreadPool(10);
        ConcurrentHashMap<Word, Word> idToWordSortedMap = new ConcurrentHashMap<Word, Word>();
        try {
            processFilesInParallel(fileList, idToWordSortedMap, executor);
            TreeSet<Word> sortedStream = idToWordSortedMap.values().stream().collect(Collectors.toCollection(() -> new TreeSet<Word>((a, b) -> a.getCount() > b.getCount() ? -1 : 1)));

            Future future = executor.submit(() -> sortedStream.forEach((k) -> System.out.println(k.toString())));
            future.get(1, TimeUnit.MINUTES);

            return sortedStream;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Calculating Word Count using the given file {@link Word}
     *
     * @param filePath : Path of the file {@link String} to use
     * @param map : HashMap of the records {@link ConcurrentHashMap<Word, Word>} to use
     */
    public void getWordCount(String filePath, ConcurrentHashMap<Word, Word> map) {
        LOG.debug("Word count process is starting : filePath = {} map = {}", filePath, map.toString());
        Scanner in = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            in = new Scanner(file);
            LOG.debug("Scanning the file row by row: {} ", file.getName());
            while (in.hasNext()) {
                String wordInFile = in.next().trim().replaceAll("[^a-zA-Z ]", "").toLowerCase();
                if (wordInFile.length() == 0) {
                    continue;
                }
                putMap(map, file, wordInFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            in.close();
        }
    }

    /**
     * Calculating Word Count using the given file {@link Word}
     *
     * @param map : HashMap of the records {@link ConcurrentHashMap<Word, Word>} to use
     * @param file : File {@link File} to use
     * @param wordInFile : Word {@link String} to use
     */
    protected void putMap(ConcurrentHashMap<Word, Word> map, File file, String wordInFile) throws InterruptedException {
        LOG.debug("putmap process is starting : map = {} file = {} word = {}", map.toString(), file.getName(), wordInFile);
        if (map.containsKey(new Word(wordInFile))) {
            incrementExistingWord(map, file, wordInFile);
        } else {
            Word word = new Word(wordInFile, file.getName());
            if (map.putIfAbsent(word, word) != null) {
                incrementExistingWord(map, file, wordInFile);
            }
        }
    }

    /**
     * Incrementing the counts of existing word using the given file {@link Word}
     *
     * @param map : HashMap of the records {@link ConcurrentHashMap<Word, Word>} to use
     * @param file : File {@link File} to use
     * @param wordInFile : Word {@link String} to use
     */
    protected static void incrementExistingWord(ConcurrentHashMap<Word, Word> map, File file, String wordInFile) {
        LOG.debug("incrementing existing word: map = {} file = {} word = {}", map.toString(), file.getName(), wordInFile);
        Word word = map.get(new Word(wordInFile));
        word.incrementCount(file.getName());
        map.put(word, word);
    }

    /**
     * Processing file with threads using the given file {@link Word}
     *
     * @param fileList : File {@link List<String>} to use
     * @param map : HashMap of the records {@link ConcurrentHashMap<Word, Word>} to use
     * @param executor : Word {@link ExecutorService} to use
     */
    public void processFilesInParallel(List<String> fileList, ConcurrentHashMap<Word, Word> map, ExecutorService executor) {
        LOG.debug("processing files in parallel : map = {}", map.toString());
        try {
            List<Future> futureList = new ArrayList<Future>();
            fileList.forEach(file -> {
                Future future = executor.submit(() -> getWordCount(file, map));
                futureList.add(future);
            });
            LOG.debug("Future List = {}", futureList);

            for (Future future : futureList) {
                future.get(1, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}