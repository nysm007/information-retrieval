package com.cs6111.group15.proj3.Module;

import java.util.Comparator;

public class CandidateComparator implements Comparator<Candidate> {
    @Override
    public int compare(Candidate c1, Candidate c2) {
        return c2.transactions.size() - c1.transactions.size();
    }
}
