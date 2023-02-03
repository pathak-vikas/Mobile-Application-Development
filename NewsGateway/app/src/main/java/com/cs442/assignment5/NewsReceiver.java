package com.cs442.assignment5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class NewsReceiver extends BroadcastReceiver {

    private static final String TAG = "NewsReceiver";
    private MainActivity mainActivity;

    public NewsReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action == null)
            return;
        switch (action){
            case MainActivity.ACTION_NEWS_STORY:
                if (intent.hasExtra(MainActivity.ARTICLE_DATA)) {
                    ArrayList<Article> lst = (ArrayList<Article>) intent.getSerializableExtra(MainActivity.ARTICLE_DATA);
                    if (lst != null) {
                        mainActivity.setArticles(lst);
                    }
                }
                break;
            case MainActivity.ACTION_NEWS_STORY_FAIL:
                mainActivity.showNetworkDialog();
                break;
            default:
                Log.d(TAG, "onReceive: Unknown broadcast received");
        }


    }
}
