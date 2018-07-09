package com.cs6111.group15.proj3.Module;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Candidate {
    public List<Integer> supportList;
    public Set<Integer> transactions;

    public Candidate(Set<Integer> transactions) {
        this.transactions = new TreeSet<>(transactions);

        this.supportList = new LinkedList<>();
        this.supportList.add(transactions.size());
    }
}



