package com.cs6111.group15.proj3.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.cs6111.group15.proj3.Module.BitSetComparator;
import com.cs6111.group15.proj3.Module.Candidate;
import com.cs6111.group15.proj3.Util.IOUtils;

public class Main {
    private static String filename;
    private static double minSupp;
    private static double minConf;
    private static int transactionCount = 0;
    private static Map<Integer, String> attributeMap = new TreeMap<>();
    private static Map<Integer, Set<Integer>> recordMap = new TreeMap<>();
    private static String writePath = "output.txt";

    private static void addAttributes(String[] attrs) {
        for (int i = 0, j = attrs.length; i < j; ++i) {
            attributeMap.put(i, attrs[i]);
        }
    }

    private static void addRecords(int lineNum, String line) {
        String[] items = line.split(",");
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals("Y")) {
                Set<Integer> records;
                if (recordMap.containsKey(i)) {
                    records = recordMap.get(i);
                } else {
                    records = new TreeSet<>();
                }
                records.add(lineNum);
                // i'th contribute appears in lineNum'th transaction
                // contribute => transactions
                recordMap.put(i, records);
            }
        }
    }

    private static List<Map<BitSet, Candidate>> apriori(Map<Integer, Set<Integer>> recordMap) {
        List<Map<BitSet, Candidate>> result = new ArrayList<>();
        Map<BitSet, Candidate> prev = new TreeMap<>(new BitSetComparator());

        // generate single-element Map using original recordMap
        for (Map.Entry<Integer, Set<Integer>> entry : recordMap.entrySet()) {
            BitSet bs = new BitSet(attributeMap.size());
            bs.set(entry.getKey());  // the (int)attribute is set to "true"
            prev.put(bs, new Candidate(entry.getValue()));  //
        }

        int candidateCount = 1;
        while (prev.size() != 0 && candidateCount <= attributeMap.size()) {
            // remove low frequent
            List<BitSet> waiting = new ArrayList<>();
            for (Map.Entry<BitSet, Candidate> entry : prev.entrySet()) {
                if (entry.getValue().transactions.size() < minSupp * transactionCount
                        || entry.getValue().supportList.size() < candidateCount) {
                    waiting.add(entry.getKey());
                }
            }
            for (BitSet b : waiting) {
                prev.remove(b);
            }
            // prev is a TreeMap<BitSet(attributes), Candidate(supportList, transactions)>
            result.add(prev);

            // get high frequent attributes from the modified prev
            BitSet prevBits = new BitSet(attributeMap.size());
            for (BitSet attributes : prev.keySet()) {
                prevBits.or(attributes);
            }

            Map<BitSet, Candidate> curr = new TreeMap<>(new BitSetComparator());
            for (Map.Entry<BitSet, Candidate> prevEntry : prev.entrySet()) {
                // augmenting set includes every bits in prevBits except for the current bit
                BitSet augment = (BitSet) prevEntry.getKey().clone();
                augment.xor(prevBits);

                // for every "true" bit, update the corresponding Candidate
                for (int j = augment.nextSetBit(0); j >= 0; j = augment.nextSetBit(j + 1)) {
                    BitSet next = (BitSet) prevEntry.getKey().clone();
                    next.set(j);
                    Candidate candidate = curr.get(next);
                    if (candidate == null) {
                        candidate = new Candidate(prevEntry.getValue().transactions);
                    } else {
                        candidate.transactions.retainAll(prevEntry.getValue().transactions);
                        candidate.supportList.add(prevEntry.getValue().transactions.size());
                    }
                    curr.put(next, candidate);
                }
            }
            prev = curr;
            candidateCount++;
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Wrong Usage!\nUsage: ./run.sh <input.csv> <support> <confidence>");
        }
        filename = args[0];
        minSupp = Double.valueOf(args[1]);
        minConf = Double.valueOf(args[2]);

        //        filename = "INTEGRATED-DATASET.csv";
        //        minSupp = 0.15;
        //        minConf = 0.60;

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(filename)));
        } catch (IOException e) {
            System.out.println(String.format("Error occured when trying to open \"%s\".", filename));
            System.exit(-1);
        }
        // add attributes
        String[] attributes = br.readLine().split(",");
        addAttributes(attributes);
        // add transactions
        String line;
        while ((line = br.readLine()) != null) {
            addRecords(transactionCount, line);
            transactionCount++;
        }
        br.close();
        // generate high-freq items using a-priori Algorithm
        List<Map<BitSet, Candidate>> highFreqItems = apriori(recordMap);
        // cache results to the StringBuilder
        StringBuilder result = new StringBuilder();
        IOUtils.printFrequentItemsets(minSupp, transactionCount, attributeMap, highFreqItems, result);
        IOUtils.printAssociationRules(minConf, transactionCount, attributeMap, highFreqItems, result);
        // system output
        IOUtils.initWriting(writePath);
        IOUtils.printFile(result.toString());
        IOUtils.closeStreaming();
    }
}

