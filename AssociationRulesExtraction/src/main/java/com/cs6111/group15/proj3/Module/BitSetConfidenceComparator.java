package com.cs6111.group15.proj3.Module;

import java.util.Comparator;
import java.util.List;

public class BitSetConfidenceComparator implements Comparator<List<Object>> {
    @Override
    public int compare(List<Object> list1, List<Object> list2) {
        double diff = (double) list2.get(2) - (double) list1.get(2);
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        }
        return 0;
    }
}