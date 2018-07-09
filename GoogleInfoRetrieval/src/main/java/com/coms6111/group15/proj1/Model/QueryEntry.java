package com.coms6111.group15.proj1.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class QueryEntry {
    private Map<String, Integer> termFreqMap;
    private List<String> wordList;

    /**
     * @param query a String representing a query.
     * @return construct a HashMap representing term frequency of each word
     */
    public QueryEntry(String query) {
        wordList = new ArrayList<String>();
        termFreqMap = new HashMap<String, Integer>();
        String[] words = query.split("\\s+");
        for (String word : words) {
            wordList.add(word);
            if (!termFreqMap.containsKey(word)) {
                termFreqMap.put(word, 0);
            }
            termFreqMap.put(word, termFreqMap.get(word) + 1);
        }
    }

    public Map<String, Integer> getTermFreqMap() {
        return termFreqMap;
    }

    /**

     * @param dfMap a map of doc frequency passed from document.
     * @param termWeightMap targeted map which contains word's weights,
     *                      tf - idf = tf * log (n / df)
     */
    public void modifyTermWeight(Map<String, Integer>dfMap, Map<String, Double> termWeightMap) {
        for (String word: wordList) {
            double df = dfMap.containsKey(word) ? (double)dfMap.get(word) : 0.0;
            int n = 10;
            double idf = df == 0.0 ? 0.0 : Math.log10(n / df);
            if (!termWeightMap.containsKey(word)) {
                termWeightMap.put(word, 0.0);
            }
            termWeightMap.put(word, termWeightMap.get(word) + idf);
        }
    }
}
