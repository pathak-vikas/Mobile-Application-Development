package com.cs442.assignment5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceReceiver extends BroadcastReceiver {

    private static final String TAG = "ServiceReceiver";
    private NewsService newsService;


    public ServiceReceiver(NewsService newsService) {
        this.newsService = newsService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action == null)
            return;
        switch (action){
            case MainActivity.ACTION_MSG_TO_SERVICE:
                if (intent.hasExtra("SOURCE_ID")) {
                    String source_id = intent.getStringExtra("SOURCE_ID");
                    if (source_id != null) {
                        new Thread(new ArticleLoader(newsService,source_id)).start();
                    }
                }
                break;

            default:
                Log.d(TAG, "onReceive: Unknown broadcast received");
        }



    }
}
