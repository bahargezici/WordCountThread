package com.improvedigital.service;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * General service that holds in memory {@link ConcurrentHashMap} for the {@link Word}s
 * <p>
 * Although {@link ConcurrentHashMap} is thread safe it doesn't support the concurrency for
 * submitAbsoluteScore and submitRelativeScore operations
 * Variables for functions left as primitives so that no null check is necessary
 */
public class WordCountService {

    public Stream<Word> process(List<String> fileList) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        ConcurrentHashMap<Word, Word> idToWordSortedMap = new ConcurrentHashMap<Word, Word>();

        try {
            processFilesInParallel(fileList, idToWordSortedMap, executor);

            Stream<Word> sortedStream = idToWordSortedMap.keySet().stream().sorted(((a, b) -> a.getCount() > b.getCount() ? -1 : 1));
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

    public void getWordCount(String filePath, ConcurrentHashMap<Word, Word> map) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            Scanner in = new Scanner(file);
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
    }

    protected void putMap(ConcurrentHashMap<Word, Word> map, File file, String wordInFile) throws InterruptedException {
        if (map.containsKey(new Word(wordInFile))) {
            incrementExistingWord(map, file, wordInFile);
        } else {
            Word word = new Word(wordInFile, file.getName());
            if (map.putIfAbsent(word, word) != null) {
                incrementExistingWord(map, file, wordInFile);
            }
        }
    }

    protected static void incrementExistingWord(ConcurrentHashMap<Word, Word> map, File file, String wordInFile) {
        Word word = map.get(new Word(wordInFile));
        word.incrementCount(file.getName());
        map.put(word, word);
    }

    public void processFilesInParallel(List<String> fileList, ConcurrentHashMap<Word, Word> map, ExecutorService executor) {

        try {
            List<Future> futureList = new ArrayList<Future>();
            fileList.forEach(file -> {
                Future future = executor.submit(() -> getWordCount(file, map));
                futureList.add(future);
            });

            for (Future future : futureList) {
                future.get(1, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}