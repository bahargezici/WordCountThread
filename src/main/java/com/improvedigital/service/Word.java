package com.improvedigital.service;

import java.util.StringJoiner;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * setters are not created so that object becomes immutable
 * if there was be more parameters then Builder pattern could have been used not to have too many constructors but not necessary currently
 */
public class Word {

    private final String wordString;
    private int count;
    //senkro ugrasmamak icin, multithread icin optimize
    private ConcurrentSkipListMap<String, Integer> fileToWordCount = new ConcurrentSkipListMap<String, Integer>();

    public Word(String wordString, String fileName) {
        this.wordString = wordString;
        this.count = 1;
        fileToWordCount.put(fileName, 1);
    }

    public Word(String wordString) {
        this.wordString = wordString;
    }

    public int getCount() {
        return count;
    }

    public String getWordString() {
        return wordString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;

        Word word = (Word) o;
        return this.wordString.equals(((Word)o).getWordString());
    }

    @Override
    public int hashCode() {
        return wordString.hashCode();
    }

    public void incrementCount(String fileName) {
        count++;
        if (fileToWordCount.putIfAbsent(fileName, 1) != null) {
            fileToWordCount.put(fileName, fileToWordCount.get(fileName) + 1);
        }
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner("+", "<", ">");

        fileToWordCount.forEach((k,v) -> {
            stringJoiner.add(v.toString());
        });

        return "<" + wordString + "> <" + count + "> = " + stringJoiner.toString();
    }

}