package com.cs442.assignment5;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SourceLoader implements Runnable {


    private static final String TAG = "SourceLoader";

    private MainActivity mainActivity;
    private HashMap<String, ArrayList<String>> categoryList=new HashMap<>();
    private HashMap<String,String> SourceMap=new HashMap<>();
    private static final String BASE_URL = "https://newsapi.org/v2/sources?language=en&country=us&category=&apiKey=8a4dc567adf44567a95ab676c0917634";

    public SourceLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {

        //TODO: download the data and parse it
        Uri dataUri = Uri.parse(BASE_URL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "run: "+urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent","");
            conn.connect();

            //todo: check for not found 404 error
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "run: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }


        handleResults(sb.toString());

    }

    private void handleResults(String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //todo:handle failures==> mainActivity.downloadFailed();
                }
            });
            return;
        }

        //final ArrayList<Country> countryList = parseJSON(s);
        categoryList=parseJSON(s);

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (categoryList!=null){
                    Log.d(TAG, "run: categorylist"+categoryList.toString());
                    mainActivity.initSetup(categoryList,SourceMap);
                }

               // Toast.makeText(mainActivity, "Loaded category and source.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "run:on ui thread, loaded category and source. " );



            }

        });
    }

    private HashMap<String, ArrayList<String>> parseJSON(String s) {
        try {

            JSONObject body=new JSONObject(s);
            JSONArray sources=body.getJSONArray("sources");
            SourceMap.clear();
            for(int i=0;i<sources.length();i++){
                JSONObject srcObj=sources.getJSONObject(i);
                String sourceid=srcObj.getString("id");
                String sourcename=srcObj.getString("name");
                String category=srcObj.getString("category");
                ArrayList <String> sourceList=new ArrayList<>();
                Log.d(TAG, "parseJSON: category&Source"+category+"->"+sourcename);
                if(!categoryList.containsKey(category)){
                    Log.d(TAG, "parseJSON: in new category");
                    sourceList.add(sourcename);
                    categoryList.put(category,sourceList);
                }else {
                    Log.d(TAG, "parseJSON: in old category");
                    Log.d(TAG, "parseJSON: sourcelist"+categoryList.get(category).toString());
                    sourceList.addAll(categoryList.get(category));
                    if(!sourceList.contains(sourcename)){
                        Log.d(TAG, "parseJSON: sourcename not in list");
                        /*sourceList.add(sourcename);
                        Collections.sort(sourceList);*/
                        categoryList.get(category).add(sourcename);
                        Log.d(TAG, "parseJSON: final list:"+ sourceList.toString());
                    }
                }
                SourceMap.put(sourcename,sourceid);
            }
            Log.d(TAG, "parseJSON: sourcemap"+SourceMap.toString());
            return categoryList;
        }catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


}
