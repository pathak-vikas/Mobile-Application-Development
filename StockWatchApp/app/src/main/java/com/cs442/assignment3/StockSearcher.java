package com.cs442.assignment3;

import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingDeque;

public class StockSearcher implements Runnable{

    private static final String TAG = "StockSearcher";
    private HashMap<String,String> allStocks=new HashMap<>();
    private TreeMap <String,String> list= new TreeMap<>();
    private MainActivity mainActivity;
    private String keyword;

    public StockSearcher(HashMap<String, String> allStocks, MainActivity mainActivity, String keyword) {
        this.allStocks = allStocks;
        this.mainActivity = mainActivity;
        this.keyword = keyword;
    }

    @Override
    public void run() {
        list.clear();
        boolean matchFound=false;
        boolean exactMatch=false;
        Log.d(TAG, "run> keyword: "+keyword);
        for (Map.Entry<String,String> entry : allStocks.entrySet())
        {
            if (entry.getKey().equalsIgnoreCase(keyword)||entry.getValue().equalsIgnoreCase(keyword)){
                matchFound=true;
                exactMatch=true;
                Log.d(TAG, "run: in exactmatch: "+entry.getKey());
                //todo: call stock downloader
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.isDuplicate(entry.getKey());
                    }
                });
                break;
            }

            if(entry.getKey().toUpperCase().contains(keyword))
            {
                Log.d(TAG, "run: in possible key match: "+entry.getKey());
                list.put(entry.getKey(),entry.getValue());
                matchFound=true;
                }else if(entry.getValue().toUpperCase().contains(keyword)){
                Log.d(TAG, "run: in possible value match: "+entry.getKey());
                list.put(entry.getKey(),entry.getValue());
                matchFound=true;
            }
    }
        if(!exactMatch)
        if (!matchFound){
            Log.d(TAG, "run: in no match for: "+keyword);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.showNotFoundDialog(keyword);
                }
            });
        }
        else {
            //todo: pass treemap to list dialog

            if(list.size()==1){
                Log.d(TAG, "run: in only one match found for: "+keyword+" is: "+list.firstKey());
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.isDuplicate(list.firstKey());
                    }
                });

            }
            else {
                Log.d(TAG, "run: more than one match found- preparing list");
                Log.d(TAG, "run: more than one match found- preparing list==>"+list.toString());
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.showListSelectionDialog(list);
                    }
                });
            }

        }

}
}
