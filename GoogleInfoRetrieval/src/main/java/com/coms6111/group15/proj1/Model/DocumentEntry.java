package com.coms6111.group15.proj1.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 DocumentEntry class is the basic model to represent term frequency by constructing a map in a document.
 1 represent term frequency
 2 modify term weight
 */
public class DocumentEntry {
    private Map<String, Integer> termFreqMap;
    private static final StopWordsBag bag = initStopWordsBag();
    /**
    @param words  a list of strings representing a document
    @return  construct a HashMap representing term frequency of each word
     */

    private static StopWordsBag initStopWordsBag() {
        StopWordsBag bag;
        try {
            bag = new StopWordsBag();
        } catch(Exception e) {
            throw new RuntimeException("Got IO exception during initialization", e);
        }
        return bag;
    }
    public DocumentEntry(List<String> words) {
        construct(words);
    }

    public DocumentEntry(String title, String snippet) throws IOException{
        termFreqMap = new HashMap<String, Integer>();
        List<String> words = new ArrayList<String>();
        findUnlistedWords(bag, words, title);
        findUnlistedWords(bag, words, snippet);
        construct(words);
    }

    public void construct(List<String> words) {
        termFreqMap = new HashMap<String, Integer>();
        for (String word : words) {
            if (!termFreqMap.containsKey(word)) {
                termFreqMap.put(word, 0);
            }
            termFreqMap.put(word, termFreqMap.get(word) + 1);
        }
    }
    
    public void findUnlistedWords(StopWordsBag bag, List<String> words, String str) {
        String s = "\\d+.\\d+|\\w+";
        Pattern pattern = Pattern.compile(s);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()){
            String word = matcher.group();
            if (!bag.contains(word)) {
                words.add(word);
            }
        }
    }

    /**
    @param termWeightMap  a <String, Double> HashMap representing the weight of each word
    @return modify termWeightMap according to instance variable termFreqMap
     */
    public void modifyTermWeight(Map<String, Double> termWeightMap) {
        double tfQuadSum = 0.0;
        for (Map.Entry<String, Integer> entry : termFreqMap.entrySet()) {
            tfQuadSum += Math.pow(entry.getValue(), 2);
        }
        double sqrtTfQuadSum = Math.sqrt(tfQuadSum);
        for (String word: termFreqMap.keySet()) {
            double unitTermWeight = termFreqMap.get(word) / sqrtTfQuadSum;
            if (!termWeightMap.containsKey(word)) {
                termWeightMap.put(word, 0.0);
            }
            termWeightMap.put(word, termWeightMap.get(word) + unitTermWeight);
        }
    }

    public Map<String, Integer> getTermFreqMap() {
        return termFreqMap;
    }
}
