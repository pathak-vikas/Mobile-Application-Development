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


public class ArticleLoader implements Runnable {

    private static final String TAG = "ArticleLoader";
    NewsService service;
    private static final String BASE_URL = "https://newsapi.org/v2/top-headlines?sources=";
    private static final String TOKEN = "&language=en&apiKey=8a4dc567adf44567a95ab676c0917634";
    private String DATA_URL;
    private ArrayList<Article> lst =new ArrayList<>();

    public ArticleLoader(NewsService service, String sourceId) {
        this.service = service;
        DATA_URL=BASE_URL+sourceId+TOKEN;
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
            service.sendFailed();
            return;
        }

        //final ArrayList<Country> countryList = parseJSON(s);
        lst=parseJSON(s);

        if (lst!=null) {
            service.setArticleList(lst);
        }
    }

    private ArrayList<Article> parseJSON(String s) {
        try {

            JSONObject srcObj=new JSONObject(s);
            JSONArray articlesArr=srcObj.getJSONArray("articles");
            lst.clear();
            for(int i=0;i<articlesArr.length();i++){
                JSONObject articleObj=articlesArr.getJSONObject(i);
                Article article =new Article(articleObj.getString("publishedAt"));
                if(!articleObj.getString("author").equalsIgnoreCase("null")&&!articleObj.getString("author").trim().isEmpty()){
                    article.setAuthor(articleObj.getString("author"));
                }
                if(!articleObj.getString("title").equalsIgnoreCase("null")&&!articleObj.getString("title").trim().isEmpty()){
                    article.setTitle(articleObj.getString("title"));
                }
                if(!articleObj.getString("description").equalsIgnoreCase("null")&&!articleObj.getString("description").trim().isEmpty()){
                    article.setDescription(articleObj.getString("description"));
                }
                if(!articleObj.getString("urlToImage").equalsIgnoreCase("null")&&!articleObj.getString("urlToImage").trim().isEmpty()){
                    article.setPhoto(articleObj.getString("urlToImage"));
                }
                article.setUrl(articleObj.getString("url"));

                lst.add(article);

            }

          return lst;
        }catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }



}
