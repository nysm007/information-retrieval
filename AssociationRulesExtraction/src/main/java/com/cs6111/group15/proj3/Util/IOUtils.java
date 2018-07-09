package com.cs6111.group15.proj3.Util;

import com.cs6111.group15.proj3.Module.BitSetConfidenceComparator;
import com.cs6111.group15.proj3.Module.Candidate;
import com.cs6111.group15.proj3.Module.CandidateComparator;

import java.io.*;
import java.util.*;

public class IOUtils {
    private static PrintWriter printWriter = null;

    /**
     * First delete target file if target file is found
     * then create new target file and write to it
     */
    public static void initWriting(String writePath) throws IOException {
        File file = new File(writePath);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        printWriter = new PrintWriter(writePath);
    }

    /**
     * @param s the string to be appended into target file
     */
    public static void printFile(String s) {
        printWriter.println(s);
    }

    public static void closeStreaming() throws IOException {
        try {
            if (printWriter != null) {
                printWriter.close();
            }
        } catch (Exception e) {
            System.out.println("File Writer can not be closed. example_run.txt is not closed.");
        }
    }

    private static void printCandidate(Map<Integer, String> attributeMap, BitSet bs, StringBuilder output) {
        output.append("[");
        int i = bs.nextSetBit(0);
        while (true) {
            output.append(attributeMap.get(i));
            i = bs.nextSetBit(i + 1);
            if (i < 0) {
                break;
            }
            output.append(",");
        }
        output.append("]");
    }

    public static void printFrequentItemsets(double minSupp, int dataCount, Map<Integer, String> attributeMap, List<Map<BitSet, Candidate>> highFreqItems, StringBuilder result) {
        result.append(String.format("==Frequent itemsets (min_sup=%d%%)\n", (int) (minSupp * 100)));

        Map<Candidate, BitSet> printMap = new TreeMap<>(new CandidateComparator());
        for (Map<BitSet, Candidate> currentCountMap : highFreqItems) {
            for (Map.Entry<BitSet, Candidate> currentEntry : currentCountMap.entrySet()) {
                printMap.put(currentEntry.getValue(), currentEntry.getKey());
            }
        }

        for (Map.Entry<Candidate, BitSet> entry : printMap.entrySet()) {
            printCandidate(attributeMap, entry.getValue(), result);
            result.append(", ");
            result.append((int) (entry.getKey().transactions.size() * 100.0 / dataCount));
            result.append("%\n");
        }
        result.append("\n");
    }

    public static void printAssociationRules(double minConf, int dataCount, Map<Integer, String> attributeMap, List<Map<BitSet, Candidate>> highFreqItems, StringBuilder result) {
        result.append(String.format("==High-confidence association rules (min_conf=%d%%)\n", (int) (minConf * 100)));

        Map<List<Object>, Double> printMap = new TreeMap<>(new BitSetConfidenceComparator());
        for (Map<BitSet, Candidate> currentCountMap : highFreqItems) {
            for (Map.Entry<BitSet, Candidate> currentEntry : currentCountMap.entrySet()) {
                if (currentEntry.getValue().supportList.size() > 1) {
                    int i = -1;
                    for (int currentSupport : currentEntry.getValue().supportList) {
                        i = currentEntry.getKey().nextSetBit(i + 1);
                        BitSet prevBits = (BitSet) currentEntry.getKey().clone();
                        prevBits.clear(i);
                        double currentConfidence = (double) currentEntry.getValue().transactions.size() / currentSupport;
                        if (currentConfidence >= minConf) {
                            List<Object> printList = new ArrayList<>();
                            printList.add(prevBits);
                            printList.add(attributeMap.get(i));
                            printList.add(currentConfidence);
                            printMap.put(printList, (double) currentEntry.getValue().transactions.size() / dataCount);
                        }
                    }
                }
            }
        }

        for (Map.Entry<List<Object>, Double> currentEntry : printMap.entrySet()) {
            printCandidate(attributeMap, (BitSet) currentEntry.getKey().get(0), result);
            result.append(String.format(" => [%s](Conf: %d%%, Supp: %d%%)\n",
                    (String) currentEntry.getKey().get(1),
                    (int) ((double) currentEntry.getKey().get(2) * 100),
                    (int) (currentEntry.getValue() * 100)));
        }
    }
}
