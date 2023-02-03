package com.cs442.assignment5;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private boolean running = true;
    private ArrayList<Article> articleList = new ArrayList<>();
    private  String source;
    private ServiceReceiver serviceReceiver;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public ArrayList<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(ArrayList<Article> articleList) {
        this.articleList.addAll(articleList);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public NewsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //listen to broadcast type ACTION_MSG_TO_SERVICE
        IntentFilter filter1 = new IntentFilter("ACTION_MSG_TO_SERVICE");
        serviceReceiver = new ServiceReceiver(this);
        registerReceiver(serviceReceiver, filter1);


        if (intent.hasExtra("SOURCE_ID")) {
            String source_id = intent.getStringExtra("SOURCE_ID");
            if (source_id != null) {
                source=source_id;
            }
        }


        //Creating new thread for my service
        //ALWAYS write your long running tasks
        // in a separate thread, to avoid an ANR

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (running) {

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(!articleList.isEmpty()){
                        sendArticles(articleList);
                    }
                }



                Log.d(TAG, "run: Ending loop");
            }
        }).start();

        return Service.START_STICKY;
    }

    private void sendArticles(ArrayList<Article> articleLst) {
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_NEWS_STORY);
        intent.putExtra(MainActivity.ARTICLE_DATA, articleLst);
        sendBroadcast(intent);
        articleList.clear();
    }

    public void sendFailed() {
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_NEWS_STORY_FAIL);
        sendBroadcast(intent);
    }

   /* private void sendMessage(String msg) {
        Intent intent = new Intent();
        intent.setAction(MainActivity.MESSAGE_FROM_SERVICE);
        intent.putExtra(MainActivity.MESSAGE_DATA, msg);
        sendBroadcast(intent);
    }*/

    @Override
    public void onDestroy() {
        //todo: unregister receiver
        unregisterReceiver(serviceReceiver);
        running = false;
        super.onDestroy();
    }



}