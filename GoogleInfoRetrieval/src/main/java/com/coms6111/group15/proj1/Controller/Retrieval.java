package com.coms6111.group15.proj1.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.coms6111.group15.proj1.Util.*;
import com.coms6111.group15.proj1.Model.*;
import org.json.JSONObject;

public class Retrieval {
    private static int LOOP_LENGTH = 10;
    private static String query = "";
    private static String searchKey = "";
    private static String searchEngineId = "";
    private static double precision = 0.0;
    private static int precisionLvl = 0;
    public static void main(String[] args) throws IOException, JSONException{
        // illegal usage
        if (args.length != 4) {
            System.out.print("Usage: ");
            System.out.println("<API KEY> <ENGINE KEY> <PRECISION> <QUERY>");
            System.exit(2);
        }
        // obtain parameters from java commands
        searchKey = args[0];
        searchEngineId = args[1];
        precision = Double.parseDouble(args[2]);
        query = args[3];
        precisionLvl = (int)(precision * 10);
        // begin interaction
        // convert query to lowercase form, note that if isDigit == true then it stays the same
        query = new String(query.toLowerCase());
        APIUtils apiUtils = new APIUtils();
        IOUtils ioUtils = new IOUtils();
        ioUtils.initStreaming();
        JSONArray items = null;
        // loop until reached precision, otherwise get 10(or less) result
        while (true) {
            // get "query" from Google, if content.length < 10, break
            ioUtils.showInitInstructions(searchKey, searchEngineId, query, precision);
            ioUtils.printRoundBeginning();
            try {
                items = apiUtils.callGoogleApi(searchKey, searchEngineId, query);
            } catch (Exception e) {
                System.out.println("Google Custom Search Failure");
                ioUtils.closeStreaming();
                System.exit(2);
            }
            if (items.length() < LOOP_LENGTH) {
                ioUtils.printFailure(items.length());
                break;
            }

            // QueryEntry should maintain a tfMap (Query.tfMap, String->Integer)
            QueryEntry queryEntry = new QueryEntry(query);

            // new two document list (relevant or irrelevant)
            List<DocumentEntry> relevant = new ArrayList<DocumentEntry>();
            List<DocumentEntry> irrelevant = new ArrayList<DocumentEntry>();

            // get first 10 response
            for (int i = 0; i < LOOP_LENGTH; i++) {
                // display the ith element from content (title and summary)
                JSONObject jsonObject = items.getJSONObject(i);
                String link = jsonObject.getString("link");
                String title = jsonObject.getString("title");
                String snippet = jsonObject.getString("snippet");
                // new a Document to store the content, which is a String array.
                // This Document class should collect statistics of every term from it
                // DocumentEntry.tfMap (String->Integer)
                // add this document to relevant or irrelevant list
                DocumentEntry documentEntry = new DocumentEntry(title.toLowerCase(), snippet.toLowerCase());
                ioUtils.printEntry(i, link, title, snippet);
                boolean isRelevant = ioUtils.isRelevant();
                if (isRelevant) {
                    relevant.add(documentEntry);
                } else {
                    irrelevant.add(documentEntry);
                }
                System.out.println();
            }
            // calculate the precision. if reached, break; if precision == 0, break
            int relevantCnt = relevant.size();
            boolean isEnd = ioUtils.printResult(query, relevantCnt, precisionLvl);
            if (isEnd) {
                break;
            } else {
                // calculate term-frequency for documents both in relevant and irrelevant list using Rocchio algorithm
                // Rocchio need (query, relevant, irrelevant) three params, and is responsible for:
                // (1) calculate dfMap (String->Integer), the key should be in keys in query.tfMap, the value should be
                // the occurrence in all documents
                // (2) calculate queryweights, counts only the String which appears in the dfMap
                // (3) calculate relaWeights and irreleWeights for every String in all documents
                // (4) new a termWeights map, combine the result from step2 and step3, calculate a brand new termWeights
                // generate a new query from the last termWeights in Rocchio
                Rocchio rocchio = new Rocchio(queryEntry, relevant, irrelevant);
                String newQuery = rocchio.getNewQuery();
                ioUtils.printUnderPrecision();
                ioUtils.printRoundStop(newQuery);
                if (newQuery.length() == 0) {
                    break;
                } else {
                    query = query + " " + newQuery;
                }
            }
        }
        ioUtils.closeStreaming();
    }
}
