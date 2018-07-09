package com.coms6111.group15.proj1.Model;

import java.util.*;

/**
 * Created by bruceyoung on 10/3/17.
 */
public class Rocchio {
    private final double alpha = 1.0;
    private final double beta = 0.8;
    private final double gamma = -0.2;
    private QueryEntry query = null;
    private List<DocumentEntry> relevant = null;
    private List<DocumentEntry> irrelevant = null;
    private Map<String, Double> relevantWeightMap = null;
    private Map<String, Double> irrelevantWeightMap = null;
    private Map<String, Integer> docFreMap = null;
    private Map<String, Double> termWeight = null;
    private Map<String, Double> queryWeightMap = null;
    public Rocchio(QueryEntry query, List<DocumentEntry> relevant, List<DocumentEntry> irrelevant) {
        this.query = query;
        this.relevant = relevant;
        this.irrelevant = irrelevant;
        this.docFreMap = new HashMap<String, Integer>();
        this.termWeight = new TreeMap<String, Double>();
        this.relevantWeightMap = new HashMap<String, Double>();
        this.irrelevantWeightMap = new HashMap<String, Double>();
        this.queryWeightMap = new HashMap<String, Double>();
        initRocchio();
        query.modifyTermWeight(docFreMap, queryWeightMap);
        setTermWeight();
    }
    // calculate term-frequency for documents both in relevant and irrelevant list using _Rocchio algorithm
    // _Rocchio need (query, relevant, irrelevant) three params, and is responsible for:
    // (1) calculate dfMap (String->Integer), the key should be in keys in query.tfMap, the value should be
    // the occurrence in all documents
    // (2) calculate queryweights, counts only the String which appears in the dfMap
    // (3) calculate releWeights and irreleWeights for every String in all documents
    // (4) new a termWeights map, combine the result from step2 and step3, calculate a brand new termWeights

    private void setMaps(List<DocumentEntry> documentEntryList, Map<String, Double> weightMap) {
        for (DocumentEntry documentEntry : documentEntryList) {
            documentEntry.modifyTermWeight(weightMap);
            for (String queryKey : query.getTermFreqMap().keySet()) {
                if (documentEntry.getTermFreqMap().containsKey(queryKey)) {
                    if (!docFreMap.containsKey(queryKey)) {
                        docFreMap.put(queryKey, 0);
                    }
                    docFreMap.put(queryKey, docFreMap.get(queryKey) + 1);
                }
            }
        }
    }

    private void initRocchio() {
        setMaps(relevant, relevantWeightMap);
        setMaps(irrelevant, irrelevantWeightMap);
    }

    public void setTermWeight() {
        for (String term : queryWeightMap.keySet()) {
            double weight = alpha * queryWeightMap.get(term);
            if (!termWeight.containsKey(term)) {
                termWeight.put(term, weight);
            } else {
                termWeight.put(term, termWeight.get(term) + weight);
            }
        }
        for (String term : relevantWeightMap.keySet()) {
            double reW = beta * relevantWeightMap.get(term);
            double irreW = gamma * (irrelevantWeightMap.containsKey(term) ? irrelevantWeightMap.get(term) : 0);
            double weight = reW + irreW;
            if (!termWeight.containsKey(term)) {
                termWeight.put(term, weight);
            } else {
                termWeight.put(term, termWeight.get(term) + weight);
            }
        }
    }

    // generate a new query from the last termWeights in Rocchio

    /**
     * generate newquery
     * 1. preserve the words in old query
     * 2. add at most two words which has the highest termWeight
     * @return newQuery  a string containing the new query for the next round (10 interactions) with user
     */
    public String getNewQuery() {
        StringBuilder sb = new StringBuilder();
        int oldQuerySize = query.getTermFreqMap().size(); // preserve all
        int cnt = 0; // at most 2
        SortedSet<Map.Entry<String, Double>> sortedSet = entriesSortedByValues(termWeight); // very high cost
        for (Map.Entry<String, Double> entry : sortedSet) {
            String key = entry.getKey();
            if (query.getTermFreqMap().containsKey(key)) {
                if (oldQuerySize > 0) {
//                    sb.append(key + " ");
                    oldQuerySize--;
                }
            }
            else if (cnt < 2) {
                sb.append(key + " ");
                cnt++;
            }
            if (oldQuerySize == 0 && cnt == 2) { // we are done
                break;
            }
        }
        if (sb.length() == 0) { return ""; }
        return sb.substring(0, sb.length() - 1).toString();
    }

    static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e2.getValue().compareTo(e1.getValue());
                        return res != 0 ? res : 1; // Special fix to preserve items with equal values
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

}