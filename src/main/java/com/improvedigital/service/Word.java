package com.improvedigital.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 *
 * This is the Word Object that holds each word's attributes from the source
 */
public class Word {
    private static final Logger LOG = LoggerFactory.getLogger(Word.class);
    private final String wordString;
    private int count;

    private ConcurrentSkipListMap<String, Integer> fileToWordCount = new ConcurrentSkipListMap<String, Integer>();

    /**
     * Constructor of Word Object {@link Word}
     *
     * @param wordString : List of the given files {@link String} to use
     * @param fileName : List of the given files {@link String} to use
     */
    public Word(String wordString, String fileName) {
        LOG.trace("Constructor for Word Class with : {} {}", wordString, fileName);
        this.wordString = wordString;
        this.count = 1;
        fileToWordCount.put(fileName, 1);
    }

    /**
     * Constructor of Word Object {@link Word}
     *
     * @param wordString : List of the given files {@link String} to use
     */
    public Word(String wordString) {
        this.wordString = wordString;
    }

    /**
     * Calculates count of Word Object {@link Word}
     *
     * @return the count {@link Integer}
     */
    public int getCount() {
        return count;
    }

    /**
     * Calculates count of Word Object {@link Word}
     *
     * @return the wordString {@link String}
     */
    public String getWordString() {
        return wordString;
    }

    @Override
    public boolean equals(Object o) {
        LOG.trace("Word Class equals method: {}", o);
        if (this == o) return true;
        if (!(o instanceof Word)) return false;

        Word word = (Word) o;
        return this.wordString.equals(((Word) o).getWordString());
    }

    @Override
    public int hashCode() {
        return wordString.hashCode();
    }

    /**
     * Increments the count of Word Object {@link Word}
     *
     * @param fileName : List of the given files {@link String} to use
     */
    public void incrementCount(String fileName) {
        LOG.debug("Word Class incrementing for per file count and total count: {}", fileName);
        count++;
        if (fileToWordCount.putIfAbsent(fileName, 1) != null) {
            fileToWordCount.put(fileName, fileToWordCount.get(fileName) + 1);
        }
        LOG.debug("After increment, file count = {} and total count = {}", fileToWordCount, count);
    }

    /**
     * Increments the count of Word Object {@link Word}
     *
     * @return new toString() {@link String} to use
     */
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner("+", "<", ">");

        fileToWordCount.forEach((k, v) -> {
            stringJoiner.add(v.toString());
        });

        return "<" + wordString + "> <" + count + "> = " + stringJoiner.toString();
    }
}