package com.coms6111.group15.proj1.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

/*
 class APIUtils is for utilization
    1. construct RESTful URL for Google Custom Search
    2. call API and obtain objects
 */
public class APIUtils {
    private static final String googleCustomSearchAPI = "https://www.googleapis.com/customsearch/v1?";
    private static final int numOfRetrievedEntry = 10;
    private static final String form = "json";
    private static InputStream inputStream = null;
    enum Parameters {
        SEARCHKEY {
            public String toString() {
                return "&key=";
            }
        },
        SEARCHENGINEID {
            public String toString() { return "&cx="; }
        },
        QUERY {
            public String toString() {
                return "&q=";
            }
        },
        NUM {
            public String toString() { return "&num="; }
        },
        FORMAT {
            public String toString() { return "&alt="; }
        }
    }

    public static String getGoogleCustomSearchUrl(String searchKey, String searchEngineId, String query) throws IOException{
        return googleCustomSearchAPI + Parameters.SEARCHKEY.toString() + searchKey + Parameters.SEARCHENGINEID.toString()
                + searchEngineId + Parameters.QUERY.toString() + getEncodedQuery(query) + Parameters.NUM + numOfRetrievedEntry
                + Parameters.FORMAT + form;
    }

    public static String getEncodedQuery(String query) throws IOException{
        return URLEncoder.encode(query.toString(), Charset.defaultCharset().name());
    }

    public static JSONArray callGoogleApi(String searchKey, String searchEngineId, String query) throws IOException{
        String apiUrl = getGoogleCustomSearchUrl(searchKey, searchEngineId, query);
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = getJsonFromUrl(apiUrl);
            jsonArray = jsonObject.getJSONArray("items");
        } catch (Exception e) {
            System.out.println("HTTP GET failure. Check Internet Connection.");
        }
        return jsonArray;
    }

    private static String readContent(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject getJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readContent(rd);
            JSONObject jsonObject = new JSONObject(jsonText);
            return jsonObject;
        } finally {
                is.close();
            }
        }
    }
