package com.coms6111.group15.proj1.Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StopWordsBag {
    private static Set<String> stopwords;
    //    private final String path = "src/main/java/com/coms6111/group15/proj1/Util/stopwords.txt";
    private final String path = "stopwords.txt";

    public StopWordsBag() throws IOException{
        stopwords = new HashSet<String>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            String word = null;
            while((word = bufferedReader.readLine()) != null) {
                stopwords.add(word);
            }
        } catch (Exception e) {
            System.out.println("Errors occur when reading stopwords.");
            e.printStackTrace();
        }
        if (bufferedReader != null) {
            bufferedReader.close();
        }
    }

    public static boolean contains(String s) {
        return stopwords.contains(s);
    }
}
