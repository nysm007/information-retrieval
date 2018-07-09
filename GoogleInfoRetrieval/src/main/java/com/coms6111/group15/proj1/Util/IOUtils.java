package com.coms6111.group15.proj1.Util;

import java.io.*;
import java.util.Scanner;

/*
 class IOUtils is for System IO utilization
    1. show init instructions
 */
public class IOUtils {
    private static FileWriter fileWriter = null;
    private static PrintWriter printWriter = null;
    private static Scanner scanner = null;
//    private static String path = "src/main/java/com/coms6111/group15/proj1/Util/transcript.txt";
    private static String path = "transcript.txt";

    public static void setPath(String s) {
        path = s;
    }
    public static void showInitInstructions(String clientKey, String engineKey, String query, double precision) throws IOException{
        print("Parameters:");
        print("Client Key: = " + clientKey);
        print("Engine Key: = " + engineKey);
        print("Query:      = " + query);
        print("Precision:  = " + precision);
    }
    public static void initStreaming() throws IOException{
        scanner = new Scanner(System.in);
        File file = new File(path);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        fileWriter = new FileWriter(path, true);
        printWriter = new PrintWriter(new BufferedWriter(fileWriter));
    }

    public static void print(String s) {
        System.out.println(s);
        printWriter.println(s);
    }

    public static void printTranscript(String s) {
        printWriter.println(s);
    }

    public static void closeStreaming() throws IOException {
        try {
            if (printWriter != null) {
                printWriter.close();
            }
            if (fileWriter != null) {
                fileWriter.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("File Writer can not be closed. transcript.txt is not closed. ");
        }
    }
    public static void printFailure(int len) throws IOException{
        print("Total number of results : " + len);
        print("The length of returned results is less than 10, stop");
        closeStreaming();
    }
    public static void printRoundBeginning() throws IOException{
        print("Google Search Results:");
        print("======================");
    }

    public static void printEntry(int i, String url, String title, String snippet) {
        print("Result " + (i + 1));
        print("[");
        print("URL         : " + url);
        print("Title       : " + title);
        print("Description : " + snippet);
        print("]\n");
        print("Relevant (Y/N)? ");
    }

    public static boolean isRelevant() {
        String s = scanner.nextLine();
        if (s.equals("Y") || s.equals("y")) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean printResult(String query, int relevantCnt, int lvl) {
        print("======================");
        print("FEEDBACK SUMMARY");
        print("Query " + query.toString());
        print("Precision " + ((double)relevantCnt / 10.0));
        if (relevantCnt >= lvl) {
            print("Desired precision reached, done\n");
            return true;
        }
        else {
            print("Still below the desired precision of " + ((double)lvl / 10.0));
            return false;
        }
    }

    public static void printUnderPrecision() {
        print("Indexing Results ....");
        print("Indexing Results ....");
    }

    public static void printRoundStop(String query) {
        print("Augmenting by " + query);
        if (query.length() == 0) {
            print("Below desired precision, but can no longer augment the query");
        }
        return;
    }
}
