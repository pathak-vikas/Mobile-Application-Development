package com.cs442.assignment3;

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
import java.util.HashMap;

public class NameDownloader implements Runnable{

    private static final String TAG = "NameDownloader";

    private HashMap<String,String> allStocks=new HashMap<>();
    private MainActivity mainActivity;
    private  static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";
    private int code=0;

            public NameDownloader(MainActivity mainActivity,int code){
                this.mainActivity=mainActivity;
                this.code=code;
            }

    @Override
    public void run() {

                //TODO: download the data and parse it
        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "run: "+urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

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



                //Todo: send the hashmap to mainactivity's receiver
     //   mainActivity.setAllStocks(allStocks);
        handleResults(sb.toString());

    }

    private void handleResults(String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   //todo:handle failures==> mainActivity.downloadFailed();
                    mainActivity.showNetworkDialog(2);
                    mainActivity.recycleStocks(0);
                }
            });
            return;
        }

        //final ArrayList<Country> countryList = parseJSON(s);
        allStocks=parseJSON(s);

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!allStocks.isEmpty())
                    Toast.makeText(mainActivity, "Loaded all stocks.", Toast.LENGTH_LONG).show();
                if(code==1){
                    mainActivity.showSelectionDialog();
                }
                mainActivity.setAllStocks(allStocks);
                mainActivity.recycleStocks(0);
            }
        });
    }

    private HashMap<String,String> parseJSON(String s) {

        HashMap<String,String> allStocks = new HashMap<>();
        try {
            JSONArray jObjMain = new JSONArray(s);

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jStock=jObjMain.getJSONObject(i);
                String name = jStock.getString("name");
                String symbol=jStock.getString("symbol");
                allStocks.put(symbol,name);
            }
            return allStocks;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return allStocks;
    }
}
