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

public class StockDownloader implements Runnable{

    private static final String TAG = "StockDownloader";

    private Stock stock;
    private MainActivity mainActivity;
    private static final String BASE_URL = "https://cloud.iexapis.com/stable/stock/";
    private static final String TOKEN="pk_35e4d1152b0f411083cce69321cd08ef";
    private String DATA_URL;

    public StockDownloader(MainActivity mainActivity, String symbol) {
        this.mainActivity = mainActivity;
        this.DATA_URL = BASE_URL+symbol+"/quote?token="+TOKEN;
    }

    @Override
    public void run() {

        //Todo: Download data if temporary list is not empty - before calling this
        //todo: Parse it to have five data points
        //todo: send stock object to MainActivity recycler's receiver

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

        //Todo: send the stock to mainactivity's receiver
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
        stock=parseJSON(s);

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (stock!=null)
                 //   Toast.makeText(mainActivity, "Loaded " + stock.toString() + " stock.", Toast.LENGTH_LONG).show();
                mainActivity.addToStocks(stock);
            }
        });
    }

    private Stock parseJSON(String s) {


        try {
            JSONObject jStock = new JSONObject(s);
            String companyName="";
            double change=0.0;
            double changePercent=0.0;
            double latestPrice=0.0;

            if(jStock.has("change")&&!jStock.isNull("change")){
                change=jStock.getDouble("change");
            }

            if(jStock.has("changePercent")&&!jStock.isNull("change")){
                changePercent=jStock.getDouble("changePercent");
            }

            if(jStock.has("latestPrice")&&!jStock.isNull("latestPrice")){
                latestPrice=jStock.getDouble("latestPrice");
            }

            if(jStock.has("companyName")&&!jStock.isNull("companyName")){
                companyName=jStock.getString("companyName");
            }

            Stock stock=new Stock(jStock.getString("symbol"),companyName,latestPrice,change,changePercent);
            return stock;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
